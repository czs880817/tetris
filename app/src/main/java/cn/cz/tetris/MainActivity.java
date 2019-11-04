package cn.cz.tetris;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import cn.cz.tetris.game.GameConstants;
import cn.cz.tetris.game.GameEngine;
import cn.cz.tetris.game.IGameInterface;
import cn.cz.tetris.game.Piece;
import cn.cz.tetris.music.MusicService;
import cn.cz.tetris.renderer.GameRenderer;
import cn.cz.tetris.utils.DebugLog;
import cn.cz.tetris.utils.SPUtils;
import cn.cz.tetris.utils.Utils;
import cn.cz.tetris.view.GameSurfaceView;
import cn.cz.tetris.view.PieceView;
import cn.cz.tetris.view.ViewBox;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GameSurfaceView.ITouchInterface,
        IGameInterface {
    private static final String TAG = "MainActivity";

    private static final int MSG_CHANGE_PIECE = 0;
    private static final int MSG_FAILED = 1;
    private static final int MSG_ADD_SCORE = 2;
    private static final int MSG_FPS = 3;

    private static final int REQ_SETTING = 1000;

    private GameHandler mHandler;
    private ViewBox mViewBox;
    private GameSurfaceView mSurfaceView;
    private GameRenderer mRenderer;
    private GameEngine mGameEngine;

    private TextView mLevelText;
    private TextView mScoreText;
    private TextView mSpeedText;
    private ImageView mPauseImage;
    private PieceView mPieceView;

    private TextView mFPSText;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler = new GameHandler(this);
        mViewBox = new ViewBox(this);
        mGameEngine = new GameEngine(this, this);
        initView(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSurfaceView.onResume();
        mPauseImage.setImageResource(R.mipmap.baseline_pause_white_36);
        mGameEngine.resumeGame();
        MusicService.resumeMusic(this);

        if (BuildConfig.DEBUG && mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.what = MSG_FPS;
                    message.arg1 = mRenderer.getFPS();
                    mHandler.sendMessage(message);
                }
            }, GameConstants.FPS_TIME_PERIOD, GameConstants.FPS_TIME_PERIOD);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSurfaceView.onPause();
        mPauseImage.setImageResource(R.mipmap.baseline_play_arrow_white_36);
        mGameEngine.pauseGame();
        MusicService.pauseMusic(this);

        if (BuildConfig.DEBUG && mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mGameEngine.isStarted()) {
            mGameEngine.pauseGame();
            mViewBox.showDialog(getString(R.string.quit_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MusicService.stopMusic(MainActivity.this);
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mGameEngine.resumeGame();
                }
            });
        } else {
            MusicService.stopMusic(this);
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mGameEngine.onSave(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SETTING && resultCode == RESULT_OK) {
            mGameEngine.readSetting();
            mLevelText.setText(Utils.getLevelStrings(this)[mGameEngine.getLevel()]);
            mSpeedText.setText(Utils.getSpeedStrings(this)[Utils.getSpeedIndex(mGameEngine.getSpeed(), Utils.getSpeeds())]);
            if (data != null && data.getIntExtra(MusicService.STR_MUSIC_ID, GameConstants.INVALID_VALUE) != GameConstants.INVALID_VALUE) {
                MusicService.startMusic(this, data.getIntExtra(MusicService.STR_MUSIC_ID, GameConstants.INVALID_VALUE));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game:
                startGame();
                break;
            case R.id.setting_image:
            case R.id.setting:
                startActivityForResult(new Intent(this, SettingActivity.class), REQ_SETTING);
                break;
            case R.id.pause_image:
                if (mGameEngine.isPaused()) {
                    mPauseImage.setImageResource(R.mipmap.baseline_pause_white_36);
                    mGameEngine.resumeGame();
                } else {
                    mPauseImage.setImageResource(R.mipmap.baseline_play_arrow_white_36);
                    mGameEngine.pauseGame();
                }
                break;
        }
    }

    @Override
    public void onRotate() {
        mGameEngine.rotate();
    }

    @Override
    public void onDropDown() {
        mGameEngine.setFastMode();
    }

    @Override
    public void onTranslate(boolean isLeft) {
        if (!mGameEngine.isFastMode()) {
            mGameEngine.translate(isLeft);
        }
    }

    @Override
    public void onFailed(int score) {
        DebugLog.i(TAG, "Game over: " + score);
        Message message = Message.obtain();
        message.what = MSG_FAILED;
        message.arg1 = score;
        mHandler.sendMessage(message);
    }

    @Override
    public void onPieceChanged(Piece piece) {
        Message message = Message.obtain();
        message.what = MSG_CHANGE_PIECE;
        message.obj = piece;
        mHandler.sendMessage(message);
    }

    @Override
    public void onScoreAdded(int score, int[] indexArray) {
        DebugLog.i(TAG, "Score: " + score);
        mRenderer.startClear(indexArray);
        Message message = Message.obtain();
        message.what = MSG_ADD_SCORE;
        message.arg1 = score;
        mHandler.sendMessage(message);
    }

    private void initView(Bundle savedInstanceState) {
        findViewById(R.id.start_game).setOnClickListener(this);
        findViewById(R.id.setting_image).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);

        mSurfaceView = findViewById(R.id.gl_surface_view);
        mSurfaceView.setTouchInterface(this);
        mRenderer = new GameRenderer(this, mGameEngine);
        mSurfaceView.setRenderer(mRenderer);

        mLevelText = findViewById(R.id.level_text);
        mScoreText = findViewById(R.id.score_text);
        mSpeedText = findViewById(R.id.speed_text);
        mPauseImage = findViewById(R.id.pause_image);
        mPauseImage.setOnClickListener(this);
        mPieceView = findViewById(R.id.piece_view);

        LinearLayout linearLayout = findViewById(R.id.right_layout);
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        int width = Utils.getDisplayWidth(this);
        int size = layoutParams.width = width * 5 / 16;
        linearLayout.setLayoutParams(layoutParams);

        layoutParams = mPieceView.getLayoutParams();
        layoutParams.width = layoutParams.height = size - (int)getResources().getDimension(R.dimen.line_size) * 16;
        mPieceView.setLayoutParams(layoutParams);

        RelativeLayout relativeLayout = findViewById(R.id.game_detail_layout);
        size = width - (int)getResources().getDimension(R.dimen.line_size) * 2 - size;
        layoutParams = relativeLayout.getLayoutParams();
        layoutParams.height = size * 2;
        relativeLayout.setLayoutParams(layoutParams);

        View line = findViewById(R.id.top_line);
        layoutParams = line.getLayoutParams();
        size = layoutParams.width = size + (int)getResources().getDimension(R.dimen.line_size) * 2;
        line.setLayoutParams(layoutParams);

        line = findViewById(R.id.bottom_line);
        layoutParams = line.getLayoutParams();
        layoutParams.width = size;
        line.setLayoutParams(layoutParams);

        if (savedInstanceState == null) {
            MusicService.startMusic(this, SPUtils.getMusic(this));
        } else {
            mGameEngine.onLoad(savedInstanceState);
            if (mGameEngine.isStarted()) {
                findViewById(R.id.button_layout).setVisibility(View.GONE);
                findViewById(R.id.game_layout).setVisibility(View.VISIBLE);
            }
        }

        mScoreText.setText(String.valueOf(mGameEngine.getScore()));
        mLevelText.setText(Utils.getLevelStrings(this)[mGameEngine.getLevel()]);
        mSpeedText.setText(Utils.getSpeedStrings(this)[Utils.getSpeedIndex(mGameEngine.getSpeed(), Utils.getSpeeds())]);

        mFPSText = findViewById(R.id.fps_text);
    }

    private void startGame() {
        findViewById(R.id.button_layout).setVisibility(View.GONE);
        findViewById(R.id.game_layout).setVisibility(View.VISIBLE);

        mGameEngine.startGame();
    }

    private static class GameHandler extends Handler {
        private WeakReference<MainActivity> mReference;

        private GameHandler(MainActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            final MainActivity activity = mReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_CHANGE_PIECE:
                        if (msg.obj instanceof Piece) {
                            activity.mPieceView.setPiece((Piece)msg.obj);
                        }
                        break;
                    case MSG_FAILED:
                        activity.mViewBox.showDialog(activity.getString(R.string.game_over),
                                activity.getString(R.string.game_over_hint), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        activity.mGameEngine.reset();
                                        activity.mScoreText.setText(String.valueOf(activity.mGameEngine.getScore()));
                                        activity.mGameEngine.startGame();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        activity.mGameEngine.reset();
                                        activity.findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
                                        activity.findViewById(R.id.game_layout).setVisibility(View.GONE);
                                    }
                                });
                        break;
                    case MSG_ADD_SCORE:
                        activity.mScoreText.setText(String.valueOf(msg.arg1));
                        break;
                    case MSG_FPS:
                        activity.mFPSText.setText(activity.getString(R.string.fps, msg.arg1));
                        break;
                }
            }
        }
    }
}
