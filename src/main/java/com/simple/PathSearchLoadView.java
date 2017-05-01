package com.simple;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by Adminis on 2017/5/1.
 */
public class PathSearchLoadView extends View implements View.OnClickListener {
    private static final String TAG = "DynamicSearchView2";
    private Paint paint;
    private int width;//view的宽度
    private int height;//view的高度
    private Path searchPath;
    private Path circlePath;
    private float BigCircleRectWidth;//搜索圆对应的外切正方形边长
    private PathMeasure pathMeasure;
    private float[] pos;
    private float animPercent;//
    private ValueAnimator serchStartAnim;
    private ValueAnimator bigCircleAnim;//外面大圆运动的动画
    private ValueAnimator startDrawSearchAnim;//最后一步绘制搜索框
    private long animDuration = 2000;//动画时间
    private int drawTag = 1;//区分是绘制搜索框还是外层圆
    public PathSearchLoadView(Context context) {
        this(context,null);
    }
    public PathSearchLoadView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public PathSearchLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        init();
    }
    private void init() {
        initPaint();
        initAnim();
        initAnimListener();
    }
    /**
     * 开始执行动画
     */
    private void startAnim() {
        drawTag = 1;
        serchStartAnim.start();
        invalidate();
    }
    /**
     * 开启大圆执行动画
     */
    public void startBigCirCleAnim(){
        serchStartAnim.removeAllUpdateListeners();//把上一个动画监听移除 以免总成诡异的bug
        bigCircleAnim.start();
        drawTag = 2;
    }
    /**
     * 最后绘制搜索框的动画
     */
    public void drawSearchAanim(){
        bigCircleAnim.removeAllUpdateListeners();//把上一个动画监听移除 以免总成诡异的bug
        startDrawSearchAnim.start();
        drawTag = 3;
    }
    /**
     * 动画监听
     */
    private void initAnimListener() {
        bigCircleAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                drawSearchAanim();
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        serchStartAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取动画在单位时间内,每次执行的值
                animPercent = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        serchStartAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startBigCirCleAnim();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        serchStartAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取动画在单位时间内,每次执行的值
                animPercent = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        bigCircleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取动画在单位时间内,每次执行的值
                animPercent = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        startDrawSearchAnim .addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取动画在单位时间内,每次执行的值
                animPercent = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }
    /**
     * 初始化动画
     */
    private void initAnim() {
        bigCircleAnim = ValueAnimator.ofFloat(0,1).setDuration(animDuration);
        serchStartAnim = ValueAnimator.ofFloat(0,1).setDuration(animDuration);
        startDrawSearchAnim = ValueAnimator.ofFloat(1,0).setDuration(animDuration);
    }
    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        initPath();
    }
    /**
     * 初始化path
     */
    private void initPath() {
        searchPath = new Path();
        circlePath = new Path();
        if(width>height){//长方形
            BigCircleRectWidth = height;
        }else if(width<height){
            BigCircleRectWidth = width;
        }else{
            BigCircleRectWidth = width;
        }
        float smallbordWidth =BigCircleRectWidth/8;
        RectF searchRect = new RectF(-smallbordWidth,-smallbordWidth,smallbordWidth,smallbordWidth);

        searchPath.addArc(searchRect,45,358);
        float bigBordWidth = smallbordWidth*2;
        RectF circleRect = new RectF(-bigBordWidth,-bigBordWidth,bigBordWidth,bigBordWidth);
        circlePath.addArc(circleRect,45,-358);
        pathMeasure = new PathMeasure(circlePath,false);
        pos = new float[2];
        pathMeasure.getPosTan(0,pos,null);
        searchPath.lineTo(pos[0],pos[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width/2,height/2);//平移画布把这个view的中心点当做原点
        drawSearch(canvas);
    }
    private void drawSearch(Canvas canvas) {
        if(drawTag==1){
            drawSearchGraph(canvas);
        }else if(drawTag==2){
            drawBigCircleGraph(canvas);
        }else if(drawTag==3){
            drawSearchBox(canvas);
        }
    }
    /**
     * 最后一步绘制搜索框 从终点到起点
     * @param canvas
     */
    private void drawSearchBox(Canvas canvas) {
        pathMeasure.setPath(searchPath, false);
        Path dst3 = new Path();
        pathMeasure.getSegment(pathMeasure.getLength() * animPercent, pathMeasure.getLength(), dst3, true);
        canvas.drawPath(dst3, paint);
    }
    /**
     * 绘制外层大圆
     * @param canvas
     */
    private void drawBigCircleGraph(Canvas canvas) {
        pathMeasure.setPath(circlePath, false);
        Path dst2 = new Path();
        float stop = pathMeasure.getLength() * animPercent;
        float start = (float) (stop - ((0.5 - Math.abs(animPercent - 0.5)) * 200f));
        pathMeasure.getSegment(start, stop, dst2, true);
        canvas.drawPath(dst2, paint);
    }
    /**
     * 绘制搜索框
     * @param canvas
     */
    private void drawSearchGraph(Canvas canvas) {
        pathMeasure.setPath(searchPath,false);
        Path dst = new Path();
        pathMeasure.getSegment(pathMeasure.getLength()*animPercent,pathMeasure.getLength(),dst,true);
        canvas.drawPath(dst,paint);
    }
    @Override
    public void onClick(View v) {
        startAnim();
    }
}
