package com.sch.rfview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shichaohui on 2015/8/5 0005.
 * <br/>
 * 自定义刷新的动画
 */
public class AnimView extends View {

    private Canvas mCanvas;
    private Paint paint;
    private Paint hollowPaint; // 空心画笔
    private int paintWidth = 3;
    private int paintWidthPx;

    private int radius;
    private float animRadius = 0;
    private int centerPointX;
    private int centerPointY;

    private int margin;
    private int dp2;

    private RectF oval;
    private final int START_ANGLE = 0;
    private int startAngle = START_ANGLE;
    private int sweepAngle = START_ANGLE;

    private final int SPEED = 10; // 速度

    private boolean isShow;
    private boolean isFirst = true;

    public AnimView(Context context) {
        this(context, null);
    }

    public AnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        radius = dip2px(context, 10);
        margin = dip2px(context, 1);
        dp2 = dip2px(context, 2);
        paintWidthPx = dip2px(context, paintWidth);

        centerPointX = centerPointY = radius + margin + paintWidthPx * 2;
        oval = new RectF(paintWidthPx, paintWidthPx, centerPointX * 2 - paintWidthPx,
                centerPointY * 2 - paintWidthPx);

        paint = new Paint();
        paint.setAntiAlias(true);

        hollowPaint = new Paint();
        hollowPaint.setStyle(Paint.Style.STROKE);
        hollowPaint.setStrokeWidth(paintWidth + 1);
        hollowPaint.setAntiAlias(true);

        isShow = getVisibility() == VISIBLE;

    }

    /**
     * 设置颜色
     *
     * @param progressColor 进度颜色
     * @param bgColor       背景颜色
     */
    public void setColor(int progressColor, int bgColor) {
        hollowPaint.setColor(progressColor);
        paint.setColor(bgColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        if (isShow) {
            // 画圆
            if (animRadius < radius) {
                start();
            } else {
                canvas.drawCircle(centerPointX, centerPointY, radius, paint);
                sweepAngle += 2;
                startAngle = sweepAngle >= 359 ? START_ANGLE : startAngle + SPEED;
                sweepAngle = sweepAngle >= 359 ? START_ANGLE : sweepAngle;
                // 画弧
                canvas.drawArc(oval, startAngle, sweepAngle, false, hollowPaint);
            }
        } else {
            end();
        }
        // 重绘
        invalidate();
    }

    private void start() {
        super.setVisibility(VISIBLE);
        if (getBottom() < ((View) getParent()).getBottom() / 2) {
            centerPointY += dp2;
            oval.top += dp2;
            oval.bottom += dp2;
            if (isFirst) {
                getLayoutParams().height += dp2;
            }
        } else {
            isFirst = false;
        }
        mCanvas.drawCircle(centerPointX, centerPointY, ++animRadius, paint);
    }

    private void end() {
        if (animRadius > 0) {
            if (centerPointY > centerPointX) {
                centerPointY -= dp2;
                // getLayoutParams().height -= dp2;
            } else {
                centerPointY = centerPointX;
            }
            mCanvas.drawCircle(centerPointX, centerPointY, --animRadius, paint);
        } else {
            super.setVisibility(GONE);
            // 恢复变量值
            sweepAngle = startAngle = START_ANGLE;
            oval = new RectF(paintWidthPx, paintWidthPx, centerPointX * 2 - paintWidthPx,
                    centerPointY * 2 - paintWidthPx);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        isShow = visibility == VISIBLE;
        draw(mCanvas);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue 要转换的dp值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
