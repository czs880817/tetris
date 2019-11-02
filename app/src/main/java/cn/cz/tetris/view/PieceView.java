package cn.cz.tetris.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.cz.tetris.game.Piece;

public class PieceView extends View {
    private Piece mPiece;
    private Paint mPaint;

    public PieceView(Context context) {
        super(context);
        init();
    }

    public PieceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPiece(Piece piece) {
        mPiece = piece;
        invalidate();
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
