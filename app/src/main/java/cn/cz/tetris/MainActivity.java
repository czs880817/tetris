package cn.cz.tetris;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import cn.cz.tetris.game.GameEngine;
import cn.cz.tetris.music.MusicService;
import cn.cz.tetris.renderer.GameRenderer;
import cn.cz.tetris.renderer.IRendererInterface;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        View.OnTouchListener,
        IRendererInterface {
    private static final String TAG = "MainActivity";

    private static final int REQ_SETTING = 1000;

    private GLSurfaceView mSurfaceView;
    private GameRenderer mRenderer;
    private GameEngine mGameEngine;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGameEngine = new GameEngine(this);
        initView(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSurfaceView.onResume();
        MusicService.resumeMusic(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameEngine.startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameEngine.stopGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSurfaceView.onPause();
        MusicService.pauseMusic(this);
    }

    @Override
    public void onBackPressed() {
        MusicService.stopMusic(this);
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SETTING && resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game:
                startGame();
                break;
            case R.id.setting:
                startActivityForResult(new Intent(this, SettingActivity.class), REQ_SETTING);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return false;
    }

    @Override
    public int[] getBlocksData() {
        return new int[1];
    }

    private void initView(Bundle savedInstanceState) {
        findViewById(R.id.start_game).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);

        mSurfaceView = findViewById(R.id.gl_surface_view);
        mSurfaceView.setEGLContextClientVersion(2);
        mRenderer = new GameRenderer(this, this);
        mSurfaceView.setRenderer(mRenderer);

        mGestureDetector = new GestureDetector(this, mGestureListener);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSurfaceView.performClick();
                return false;
            }
        });

        if (savedInstanceState == null) {
            MusicService.startMusic(this);
        } else {

        }
    }

    private void startGame() {
        findViewById(R.id.button_layout).setVisibility(View.GONE);
        findViewById(R.id.game_layout).setVisibility(View.VISIBLE);
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };
}
