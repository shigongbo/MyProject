package com.example.danny.myproject.musicproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.danny.myproject.R;
import com.example.danny.myproject.musicproject.musicbeens.LyricObject;
import com.example.danny.myproject.musicproject.musicconstant.MusicConstant;
import com.example.danny.myproject.musicproject.musicui.LyricView;
import com.example.danny.myproject.musicproject.musicutility.MusicUtility;
import com.example.danny.myproject.utility.Logger;
import com.example.danny.myproject.utility.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = getClass().getSimpleName();

    private MediaPlayer mMediaPlayer;
    private Thread thread;

    private List<LyricObject> lyricObjectList = new ArrayList<>();
    private String mp3Path;
    private int mDuration;
    private Boolean hasLyric = false;
    private Boolean isRunning = false;

    private float ldriftx;
    private float ldrifty;

    private LyricView mLyricView;
    private ImageButton mPlayButton;
    private ImageButton mPlayRewButton;
    private ImageButton mPlayFFButton;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        mp3Path = MusicUtility.getMusicPath();

        new Thread(readLyricRunnable).start();
        prepareMusicPlayer();

        initViews();
    }

    private Runnable readLyricRunnable = new Runnable() {
        @Override
        public void run() {
            String lrcPath = mp3Path;
            lrcPath = lrcPath.substring(0, lrcPath.length() - 4).trim() + ".lrc".trim();
            hasLyric = read(lrcPath);
        }
    };

    private void initViews() {

        mLyricView = (LyricView) findViewById(R.id.music_lrc);
//        mLyricView.setLongClickable(true);
//        mLyricView.setOnTouchListener(new LyricGesture(this));

//        if (hasLyric) {
        mLyricView.initLyricText(lyricObjectList, hasLyric);
//        }


        mPlayButton = (ImageButton) findViewById(R.id.music_play_pause);
        mPlayRewButton = (ImageButton) findViewById(R.id.music_play_rew);
        mPlayFFButton = (ImageButton) findViewById(R.id.music_play_ff);

        mPlayButton.setOnClickListener(this);
        mPlayRewButton.setOnClickListener(this);
        mPlayFFButton.setOnClickListener(this);

        mSeekBar = (SeekBar) findViewById(R.id.music_seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Logger.d(TAG, "SeekBar onProgressChanged");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Logger.d(TAG, "SeekBar onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Logger.d(TAG, "SeekBar onStopTrackingTouch");
                mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mMediaPlayer.start();
        mHandler.sendEmptyMessageDelayed(MusicConstant.MUSIC_UPDATE_lYRICtEXT, 200);
    }

    private void prepareMusicPlayer() {

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Logger.d(TAG, "MediaPlayer onPrepared getDuration == " + mp.getDuration());
                mDuration = mp.getDuration();
                mHandler.sendEmptyMessage(MusicConstant.MUSIC_UPDATE_DURATION);
            }
        });

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mp3Path);
            mMediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            Logger.w(TAG, Utility.getExceptionMessage(e));
        } catch (IllegalStateException e) {
            Logger.w(TAG, Utility.getExceptionMessage(e));
        } catch (IOException e) {
            Logger.w(TAG, Utility.getExceptionMessage(e));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null && thread != null) {
            isRunning = false;
            thread.interrupt();
            thread = null;
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_play_pause:
                if (mMediaPlayer.isPlaying()) {
                    mPlayButton.setImageResource(R.drawable.ic_media_play);
                    mMediaPlayer.pause();
                } else {
                    mPlayButton.setImageResource(R.drawable.ic_media_pause);
                    mMediaPlayer.start();
                }
                break;
            case R.id.music_play_rew:
                int rewTo = mMediaPlayer.getCurrentPosition() - 5000;
                if (rewTo > 0) {
                    mMediaPlayer.seekTo(rewTo);
                } else {
                    mMediaPlayer.seekTo(0);
                }
                break;
            case R.id.music_play_ff:
                int ffTo = mMediaPlayer.getCurrentPosition() + 5000;
                if (ffTo < mMediaPlayer.getDuration()) {
                    mMediaPlayer.seekTo(ffTo);
                } else {
                    mMediaPlayer.seekTo(mMediaPlayer.getDuration());
                }
                break;
            default:
                break;
        }
    }

    /**
     * read and format the lyric file content
     *
     * @param filePath lyric file path
     */
    private boolean read(String filePath) {
        String data = "";
        try {
            File lrcFile = new File(filePath);
            if (!lrcFile.exists()) {
                Logger.e(TAG, " the " + filePath + " is not exist");
            }
            if (!lrcFile.isFile()) {
                return false;
            }

            FileInputStream stream = new FileInputStream(lrcFile);// context.openFileInput(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            int i = 0;
            Pattern pattern = Pattern.compile("\\d{2}");
            while ((data = br.readLine()) != null) {
                data = data.replace("[", "");// 将前面的替换成后面的
                data = data.replace("]", "@");
                String splitdata[] = data.split("@");// 分隔
                if (data.endsWith("@")) {
                    for (int k = 0; k < splitdata.length; k++) {
                        String str = splitdata[k];
                        str = str.replace(":", ".");
                        str = str.replace(".", "@");
                        String timedata[] = str.split("@");
                        Matcher matcher = pattern.matcher(timedata[0]);
                        if (timedata.length == 3 && matcher.matches()) {
                            int m = Integer.parseInt(timedata[0]); // 分
                            int s = Integer.parseInt(timedata[1]); // 秒
                            int ms = Integer.parseInt(timedata[2]); // 毫秒
                            int currTime = (m * 60 + s) * 1000 + ms * 10;
                            LyricObject lyricObject = new LyricObject();
                            lyricObject.setLrcTimestamp(currTime);
                            lyricObject.setLrcText("");
                            lyricObjectList.add(lyricObject);
                        }
                    }
                } else {
                    String lrcContent = splitdata[splitdata.length - 1];
                    for (int j = 0; j < splitdata.length - 1; j++) {
                        String tmpStr = splitdata[j];

                        tmpStr = tmpStr.replace(":", ".");
                        tmpStr = tmpStr.replace(".", "@");
                        String timedata[] = tmpStr.split("@");
                        Matcher matcher = pattern.matcher(timedata[0]);
                        if (timedata.length == 3 && matcher.matches()) {
                            int m = Integer.parseInt(timedata[0]); // 分
                            int s = Math.abs(Integer.parseInt(timedata[1])); // 秒
                            int ms = Integer.parseInt(timedata[2]); // 毫秒
                            int currTime = (m * 60 + s) * 1000 + ms * 10;
                            LyricObject lyricObject = new LyricObject();
                            lyricObject.setLrcTimestamp(currTime);
                            lyricObject.setLrcText(lrcContent);
                            lyricObjectList.add(lyricObject);
                        }
                    }
                }
            }
            stream.close();
            return true;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "FileNotFoundException e == " + e.toString());
            return false;
        } catch (IOException e) {
            Logger.e(TAG, "IOException e == " + e.toString());
            return false;
        }
    }

    public void slidestart() {
        if (thread != null) {
            if (thread.isAlive()) {
                mLyricView.showProgress = true;
                isRunning = false;
                thread.interrupt();
                thread = null;
            }
        }
    }

    public boolean updatelab(float dx, float dy, boolean toggle) {
        if (toggle) {
            ldriftx = dx + ldriftx;
            ldrifty = dy + ldrifty;
        } else {
            if (Math.abs(ldriftx) < Math.abs(ldrifty)) {
                return true;
            }
            ldriftx = 0;
            ldrifty = 0;
        }
        return false;
    }

    public void updateprogress(float dx, float dy) {
        mLyricView.driftX = dx + mLyricView.driftX;
        mLyricView.driftY = dy + mLyricView.driftY;
        mLyricView.invalidate();// 更新视图
    }

    public void updatePlayer() {
        mLyricView.showProgress = false;
        // if(Math.abs(lyricView.driftx)<Math.abs(lyricView.drifty)){
        mLyricView.index = mLyricView.index + mLyricView.temp;
        mLyricView.driftX = 0;
        mLyricView.driftY = 0;
        if (mLyricView.repair()) {
            mMediaPlayer.seekTo(lyricObjectList.get(mLyricView.index - 1).getLrcTimestamp());
        } else {
            mMediaPlayer.seekTo(0);
        }

        /*// 重启线程
        isRunning = true;
        thread = new Thread(new UIUpdateThread());
        thread.start();*/
        // }
    }

    private void updateLyricView() {
        if (mMediaPlayer.isPlaying()) {
            int time = mMediaPlayer.getCurrentPosition();
            mLyricView.updateindex(time);
            ((TextView) findViewById(R.id.current_progress)).setText(formatDuration(time));
            mSeekBar.setProgress(time);
            mHandler.post(mUpdateResults);
        }

        mHandler.sendEmptyMessageDelayed(MusicConstant.MUSIC_UPDATE_lYRICtEXT, 200);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MusicConstant.MUSIC_UPDATE_lYRICtEXT:
                    updateLyricView();
                    break;
                case MusicConstant.MUSIC_UPDATE_DURATION:
                    mSeekBar.setMax(mDuration);
                    ((TextView) findViewById(R.id.duration)).setText(formatDuration(mDuration));
                    ((TextView) findViewById(R.id.current_progress)).setText("0:00");
                    break;
                default:
                    break;
            }
        }
    };

    Runnable mUpdateResults = new Runnable() {

        @Override
        public void run() {
            mLyricView.invalidate();// 更新视图
        }
    };

    private String formatDuration(int duration) {
        duration /= 1000;
        String ss = String.valueOf(duration % 60);
        String mm = String.valueOf(duration / 60);
        if (ss.length() == 1) ss = "0" + ss;
        if (mm.length() == 1) mm = "0" + mm;
        String time = mm + ":" + ss;
        return time;
    }
}
