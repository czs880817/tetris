package cn.cz.tetris.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.cz.tetris.BuildConfig;
import cn.cz.tetris.R;
import cn.cz.tetris.game.GameConstants;
import cn.cz.tetris.game.GameEngine;
import cn.cz.tetris.utils.DebugLog;

public class GameRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GameRenderer";

    private static final float[] SQUARE_COORDINATES = {
            1.0f, -1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, 1.0f,
    };
    private static final float[] TEXTURE_COORDINATES = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };

    private Context mContext;
    private GameEngine mGameEngine;
    private int mCount = 0;
    private float mScoreTime = 0.0f;
    private int[] mIndexArray;
    private boolean mClearMode = false;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureCoordinateBuffer;
    private IntBuffer mDataBuffer;
    private IntBuffer mIndexArrayBuffer;

    private int mPositionLocation;
    private int mTextureCoordinateLocation;
    private int mDataLocation;
    private int mScoreTimeLocation;
    private int mIndexArrayLocation;
    private int mClearModeLocation;

    private int mFPS = 0;

    public GameRenderer(Context context, GameEngine gameEngine) {
        mContext = context;
        mGameEngine = gameEngine;
        mIndexArray = new int[GameConstants.PIECE_SIZE];
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mVertexBuffer = ByteBuffer.allocateDirect(SQUARE_COORDINATES.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(SQUARE_COORDINATES).position(0);
        mTextureCoordinateBuffer = ByteBuffer.allocateDirect(TEXTURE_COORDINATES.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureCoordinateBuffer.put(TEXTURE_COORDINATES).position(0);
        mDataBuffer = ByteBuffer.allocateDirect(GameConstants.LAND_SIZE * GameConstants.PORT_SIZE * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        mIndexArrayBuffer = ByteBuffer.allocateDirect(GameConstants.PIECE_SIZE * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

        int program = buildProgram();
        GLES20.glUseProgram(program);

        mPositionLocation = GLES20.glGetAttribLocation(program, "vPosition");
        mTextureCoordinateLocation = GLES20.glGetAttribLocation(program, "vTexCoord");
        mDataLocation = GLES20.glGetUniformLocation(program, "data");
        mScoreTimeLocation = GLES20.glGetUniformLocation(program, "scoreTime");
        mIndexArrayLocation = GLES20.glGetUniformLocation(program, "indexArray");
        mClearModeLocation = GLES20.glGetUniformLocation(program, "clearMode");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glEnableVertexAttribArray(mPositionLocation);
        GLES20.glVertexAttribPointer(mPositionLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateLocation);
        GLES20.glVertexAttribPointer(mTextureCoordinateLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, mTextureCoordinateBuffer);

        if (mClearMode) {
            if (mScoreTime > Math.PI / 2) {
                mClearMode = false;
                mScoreTime = 0.0f;
                mGameEngine.clearLines();
            } else {
                mScoreTime += 0.05f;
            }
        } else {
            if (mGameEngine.isFastMode()) {
                mGameEngine.run();
            } else if (mCount >= mGameEngine.getSpeed()) {
                mCount = 0;
                mGameEngine.run();
            } else {
                mCount++;
            }
        }

        int[] data = mGameEngine.getRendererData();
        mDataBuffer.put(data).position(0);
        GLES20.glUniform1iv(mDataLocation, data.length, mDataBuffer);
        mIndexArrayBuffer.put(mIndexArray).position(0);
        GLES20.glUniform1iv(mIndexArrayLocation, mIndexArray.length, mIndexArrayBuffer);

        GLES20.glUniform1f(mScoreTimeLocation, mScoreTime);
        GLES20.glUniform1i(mClearModeLocation, mClearMode ? 1 : 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateLocation);

        GLES20.glFlush();

        if (BuildConfig.DEBUG) {
            mFPS++;
        }
    }

    public void startClear(int[] indexArray) {
        mClearMode = true;
        mIndexArray = indexArray;
    }

    public int getFPS() {
        int res = mFPS;
        mFPS = 0;
        return res;
    }

    private int buildProgram() {
        int vertexShader = buildShader(GLES20.GL_VERTEX_SHADER, getStringFromRaw(R.raw.vertex));
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = buildShader(GLES20.GL_FRAGMENT_SHADER, getStringFromRaw(R.raw.fragment));
        if (fragmentShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program == 0) {
            return 0;
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        return program;
    }

    private int buildShader(int type, String shaderSource) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            return 0;
        }

        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);

        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            DebugLog.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    private String getStringFromRaw(@RawRes int id) {
        String res;
        try {
            InputStream inputStream = mContext.getResources().openRawResource(id);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int i = inputStream.read();
            while (i != -1) {
                outputStream.write(i);
                i = inputStream.read();
            }

            res = outputStream.toString();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            res = "";
        }

        return res;
    }
}
