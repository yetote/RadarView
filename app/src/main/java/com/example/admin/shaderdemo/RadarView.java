package com.example.admin.shaderdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * com.example.admin.shaderdemo
 *
 * @author Swg
 */
public class RadarView extends View {
    private static final String TAG = "RadarView";
    private Paint smallCirclePaint, arcPaint, circlePaint, linePaint, pointPaint;
    private Context context;
    //起始点X,Y点坐标
    private float startPointX, startPointY;
    //旋转角度
    private int rotate;
    //起始角度
    private int startRotate;
    //终点XY坐标
    private float endPointX, endPointY;
    //旋转半径
    private float radius;
    //控件的宽高
    private float width, height;
    //圆半径
    private float circleRadius;
    //圆变换半径
    private float circleRadiusChange, smallCircleRadiusChange;
    //圆半径是否发生变换
    private boolean isCircleChange;
    //圆是否会逐渐扩大
    private boolean isCircleLarge;
    //扇形扫描过的角度
    private float sweepRotate;
    //扇形起始的角度
    private int arcStartRotate = -90;
    int seed = 0;
    private PointTuple pointTuple;
    private static final int CIRCLE_RADIUS = 360;

    public RadarView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
        initAttrs(attrs);
    }

    private void init(Context context) {
        arcPaint = new Paint();
        arcPaint.setColor(ContextCompat.getColor(context, R.color.line));

        smallCirclePaint = new Paint();
        smallCirclePaint.setColor(ContextCompat.getColor(context, R.color.cadetblue));
        smallCirclePaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(context, R.color.cadetblue));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(10);

        pointPaint = new Paint();
        pointPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        pointPaint.setStrokeWidth(10);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadarView);

        //获取xml中定义的雷达中心点的X，Y坐标
        startPointX = ta.getFloat(R.styleable.RadarView_rv_pointX, 0);
        startPointY = ta.getFloat(R.styleable.RadarView_rv_pointY, 0);
        //获取xml中定义的大圆半径
        radius = ta.getFloat(R.styleable.RadarView_rv_radius, 200);
        //获取xml中定义的小圆半径
        circleRadius = ta.getFloat(R.styleable.RadarView_rv_smallCircleRadius, 0);
        //获取xml中定义的圆半径是否变化
        isCircleLarge = ta.getBoolean(R.styleable.RadarView_rv_isCircleChange, true);
//        Log.e(TAG, "RadarView: " + isCircleLarge);
        ta.recycle();
    }


    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
        //获取XML属性
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Shader shader = new LinearGradient(
                (float) (startPointX + radius / 2 * Math.sin((Math.PI / 180) * rotate)),
                (float) (startPointY - radius / 2 * Math.cos((Math.PI / 180) * rotate)),
                (float) (startPointX + radius / 2 * Math.sin(Math.PI / 180 * (rotate - 45))),
                (float) (startPointY - radius / 2 * Math.cos(Math.PI / 180 * (rotate - 45))),
                ContextCompat.getColor(context, R.color.cadetblue),
                ContextCompat.getColor(context, R.color.transparent),
                Shader.TileMode.CLAMP);
        arcPaint.setShader(shader);

        drawLine(canvas);

        drawCircle(canvas);

        drawSmallCircle(canvas);

        drawArc(canvas);
    }

    /**
     * 绘制扇形
     * @param canvas 画板
     */
    private void drawArc(Canvas canvas) {
        canvas.drawArc(startPointX - radius,
                startPointY - radius,
                startPointX + radius,
                startPointY + radius,
                arcStartRotate,
                -sweepRotate,
                true,
                arcPaint);

    }

    /**
     * 画圆心随机的小圆
     * @param canvas 画板
     */
    private void drawSmallCircle(Canvas canvas) {
        if (pointTuple != null) {
            canvas.drawPoint((float) pointTuple.getX(), (float) pointTuple.getY(), pointPaint);
            canvas.drawCircle((float) pointTuple.getX(), (float) pointTuple.getY(), smallCircleRadiusChange, smallCirclePaint);
        }
    }

    /**
     * 画圆
     * @param canvas 画板
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(startPointX, startPointY, circleRadius + circleRadiusChange, circlePaint);
   }


    /**
     * 画线
     * @param canvas 画板
     */
    private void drawLine(Canvas canvas) {
        endPointX = (float) (startPointX + Math.sin((Math.PI / 180) * rotate) * radius);
        endPointY = (float) (startPointY - Math.cos((Math.PI / 180) * rotate) * radius);
        canvas.drawLine(startPointX, startPointY, endPointX, endPointY, linePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = widthMeasureSpec - MeasureSpec.getMode(widthMeasureSpec);
        height = heightMeasureSpec - MeasureSpec.getMode(heightMeasureSpec);

        //当未设置起始点的X值时，X水平居中
        if (startPointX == 0) {
            startPointX = width / 2;
        }

        //当未设置起始点的Y值时，Y垂直居中
        if (startPointY == 0) {
            startPointY = height / 2;
        }
        Log.e(TAG, "onMeasure: " + radius + "     " + width);
        //当未设置大圆半径时，大圆半径为屏幕宽度的一半
        if (radius == 0) {
            radius = width / 2;
        }

        //当未设置小圆半径时，小圆半径为大圆半径的1/3
        if (circleRadius == 0) {
            circleRadius = width / 2 / 2;
        }

    }


    private PointTuple<Float, Float> addMarkerPoint() {
        float xPoint = (float) Math.random() * width;
        float yPoint = (float) Math.random() * height;
        smallCircleRadiusChange = 0;
        return new PointTuple<>(xPoint, yPoint);
    }

    public void startFindAnimation() {
        final ValueAnimator animator = ValueAnimator.ofInt(0, CIRCLE_RADIUS * 3);
        animator.setDuration(CIRCLE_RADIUS * 3 * 10);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                rotate = (int) animator.getAnimatedValue();
                if (isCircleLarge) {
                    circleRadiusChange++;
                    if (circleRadiusChange <= (radius + 1) && circleRadiusChange >= (radius - 1)) {
                        circleRadiusChange = 0;
                    }
                }
                if (rotate / 120 > 0 & rotate / 120 != seed) {
                    seed = rotate / 120;
                    pointTuple = addMarkerPoint();
                }

                arcStartRotate = rotate - 90;
                if (rotate <= 30) {
                    sweepRotate = rotate;
                } else if (rotate > CIRCLE_RADIUS * 3 - 30 + 1) {
                    sweepRotate = CIRCLE_RADIUS * 3 - rotate;
                } else {
                    sweepRotate = 30;
                }

                smallCircleRadiusChange += 2;
                invalidate();
            }
        });
        animator.start();

        if (rotate == 3 * CIRCLE_RADIUS) animator.end();
    }
}