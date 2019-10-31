package cn.cz.tetris.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;

import cn.cz.tetris.R;
import cn.cz.tetris.utils.DebugLog;

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    private static final String STR_COMMAND = "str_command";

    private static final int COMMAND_NONE = 0;
    private static final int COMMAND_START = 1;
    private static final int COMMAND_STOP = 2;
    private static final int COMMAND_RESUME = 3;
    private static final int COMMAND_PAUSE = 4;

    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.i(TAG, "MusicService created");
        mMediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(STR_COMMAND, COMMAND_NONE)) {
                case COMMAND_START:
                    try {
                        mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.aigei_com));
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        mMediaPlayer.setLooping(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case COMMAND_STOP:
                    stopSelf();
                    break;
                case COMMAND_RESUME:
                    if (!mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    }
                    break;
                case COMMAND_PAUSE:
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DebugLog.i(TAG, "MusicService destroyed");
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    public static void startMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_START);
        context.startService(intent);
    }

    public static void stopMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_STOP);
        context.startService(intent);
    }

    public static void resumeMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_RESUME);
        context.startService(intent);
    }

    public static void pauseMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra(STR_COMMAND, COMMAND_PAUSE);
        context.startService(intent);
    }
}
