package com.sch.rfview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by shichaohui on 2015/8/7 0007.
 * <br/>
 * 自定义动画ImageView
 */
public class AnimImageView extends ImageView {

    private int dp1; // 1dp
    private int screenWidth; // 屏幕宽度
    private int height; // 当前View的高度
    private int maxHeight; // View的最大高度
    private int centerX; // 当前View的中心点的X坐标

    private String text = "下拉刷新";
    private float textWidth; // 文本一半宽度

    private int colorBg = Color.WHITE; // 背景

    private Paint mPaint;
    private Paint mPaintText;

    private Path mPath;

    public AnimImageView(Context context) {
        this(context, null);
    }

    public AnimImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dp1 = AnimView.dip2px(context, 1);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        centerX = screenWidth / 2;

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);

        mPaintText = new Paint();
        mPaintText.setColor(Color.WHITE);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(28);

        mPath = new Path();

        textWidth = mPaint.measureText(text);

    }

    /**
     * @param color1
     * @param color2
     */
    public void setColor(int color1, int color2) {
        mPaint.setColor(color2);
        mPaintText.setColor(color1);
        colorBg = color1;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();

        invalidate();

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (getLayoutParams().height > dp1) {

            height = getLayoutParams().height;

            // 背景
            canvas.drawColor(colorBg);
            // 矩形
            canvas.drawRect(0, 0, screenWidth, 130, mPaint);
            // 曲线
            if (height > 130) {
                mPath.reset();
                mPath.moveTo(0, 130);
                mPath.cubicTo(centerX, height, centerX, height, screenWidth, 130);
                canvas.drawPath(mPath, mPaint);
            }
            // 文本
            if (height >= maxHeight) {
                canvas.drawText("松手刷新", centerX - textWidth, 50, mPaintText);
                canvas.drawText("松手刷新", centerX - textWidth, 50, mPaintText);
            } else {
                canvas.drawText("下拉刷新", centerX - textWidth, 50, mPaintText);
                canvas.drawText("下拉刷新", centerX - textWidth, 50, mPaintText);
            }

        }

    }

}
