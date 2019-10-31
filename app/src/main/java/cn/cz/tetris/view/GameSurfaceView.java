package cn.cz.tetris.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GameSurfaceView extends GLSurfaceView implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    private ITouchInterface iTouchInterface;

    private float mDistance = 0.0f;

    public GameSurfaceView(Context context) {
        super(context);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setTouchInterface(ITouchInterface touchInterface) {
        iTouchInterface = touchInterface;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (iTouchInterface != null) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }

        return false;
    }

    private void init() {
        setEGLContextClientVersion(2);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        setOnTouchListener(this);
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
            iTouchInterface.onRotate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityY > 5000.0f) {
                iTouchInterface.onDropDown();
            }
            return true;
        }
    };

    public interface ITouchInterface {
        void onRotate();
        void onDropDown();
        void onTranslate(boolean isLeft);
    }
}
