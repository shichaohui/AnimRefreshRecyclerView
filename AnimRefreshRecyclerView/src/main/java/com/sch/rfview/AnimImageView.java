package com.sch.rfview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
    private int maxHeight; // 上次View的高度
    private int centerX; // 当前View的中心点的X坐标

    private String text = "下拉刷新";
    private float textWidth; // 文本一半宽度

    private int colorBg = Color.WHITE; // 背景

    private Paint mPaint;
    private Paint mPaintText;

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

            canvas.drawColor(colorBg);

            canvas.drawArc(new RectF(-screenWidth / 3, -height * 3, screenWidth * 1.3f, height),
                    0, 180, false, mPaint);

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
