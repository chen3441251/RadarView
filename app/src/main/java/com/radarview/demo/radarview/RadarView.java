package com.radarview.demo.radarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @ Creator     :     chenchao
 * @ CreateDate  :     2018/3/26 0026 11:11
 * @ Description :     雷达图
 */

public class RadarView extends View {
    private final Context  mContext;
    private       int      mSumCount;
    private       float    mAngle;
    private       double[] mData;
    private       String[] mTitle;
    private       float    mMaxValue;
    private       float    mRadius;
    private float mCenterX;
    private float mCenterY;
    private Paint mPaint1;
    private float mMinDistance;
    private Paint mPaint2;
    private Paint mPaint3;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        //总共的数据是6
        mSumCount = 6;
        //计算出每一个夹角
        mAngle = (float) (Math.PI * 2 / mSumCount);
        //模拟数据分布
        mData = new double[]{100, 60, 80, 30, 100, 60};
        //六边形顶点的name
        mTitle = new String[]{"a", "b", "c", "d", "e", "f"};
        //数据中最大值
        mMaxValue = 100;
        //创建五边形的画笔
        mPaint1 = new Paint();
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setColor(Color.GRAY);
        mPaint1.setAntiAlias(true);
        mPaint1.setStrokeWidth(2);
        //顶点文字距离顶点的最小距离
        mMinDistance = 10;
        //创建绘制顶点文字的画笔
        mPaint2 = new Paint();
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setColor(Color.RED);
        mPaint2.setAntiAlias(true);
        mPaint2.setStrokeWidth(2);
        mPaint2.setTextSize(50);
        //创建绘制覆盖区的画笔
        mPaint3 = new Paint();
//        mPaint3.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint3.setColor(Color.BLUE);
        mPaint3.setAntiAlias(true);
        mPaint3.setStrokeWidth(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //确定六边形中心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        //初始化雷达图的半径
        mRadius = (float) (Math.min(w, h) * 0.4);
        postInvalidate();
    }
    //绘制

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制六边形
        drawPolygon(canvas);
        //绘制中心到顶点的直线
        drawLines(canvas);
        //绘制顶点文字
        drawTexts(canvas);
        //绘制覆盖区域
        drawArea(canvas);

    }

    private void drawArea(Canvas canvas) {
        Path path = new Path();
        mPaint3.setAlpha(255);
        for (int i = 0; i < mSumCount; i++) {
            //计算各个覆盖点
            float x= (float) (mCenterX+(mRadius*mData[i]/mMaxValue)*Math.cos(mAngle*i));
            float y= (float) (mCenterY+(mRadius*mData[i]/mMaxValue)*Math.sin(mAngle*i));
            if(i==0){
                path.moveTo(x,mCenterY);
            }else {

                path.lineTo(x,y);
            }
            //绘制小圆点
            canvas.drawCircle(x,y,10,mPaint3);
        }
        //绘制覆盖区域的边线
        mPaint3.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path,mPaint3);
        //回去填充区域
        mPaint3.setAlpha(127);
        mPaint3.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path,mPaint3);
    }

    private void drawTexts(Canvas canvas) {
        Rect rect = new Rect();
        mPaint2.getTextBounds(mTitle[0],0,mTitle[0].length(),rect);
        float textWidth=rect.width();
        float textHeight=rect.height();
        for (int i = 0; i < mSumCount; i++) {
            switch (i) {
                case 0:
                    canvas.drawText(mTitle[i],mCenterX+mRadius+mMinDistance,mCenterY+textHeight/2,mPaint2);
                    break;
                case 1:
                    canvas.drawText(mTitle[i], (float) (mCenterX+mRadius*Math.cos(mAngle)-textWidth/2), (float) (mCenterY+mRadius*Math.sin(mAngle)+mMinDistance+textHeight),mPaint2);
                    break;
                case 2:
                    canvas.drawText(mTitle[i],(float) (mCenterX-mRadius*Math.cos(mAngle)-textWidth/2),(float) (mCenterY+mRadius*Math.sin(mAngle)+mMinDistance+textHeight),mPaint2);
                    break;
                case 3:
                    canvas.drawText(mTitle[i],mCenterX-mRadius-mMinDistance-textWidth,mCenterY+textHeight/2,mPaint2);
                    break;
                case 4:
                    canvas.drawText(mTitle[i],(float) (mCenterX-mRadius*Math.cos(mAngle)-textWidth/2),(float) (mCenterY-mRadius*Math.sin(mAngle)-mMinDistance),mPaint2);
                    break;
                case 5:
                    canvas.drawText(mTitle[i],(float) (mCenterX+mRadius*Math.cos(mAngle)-textWidth/2),(float) (mCenterY-mRadius*Math.sin(mAngle)-mMinDistance),mPaint2);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < mSumCount; i++) {
            //每完成一条线后就重置
            path.reset();
            path.moveTo(mCenterX,mCenterY);
            path.lineTo((float) (mCenterX+mRadius*Math.cos(mAngle*i)),(float) (mCenterY+mRadius*Math.sin(mAngle*i)));
            canvas.drawPath(path,mPaint1);
        }
    }

    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        //计算出网格每一段的半径
        float r=mRadius/(mSumCount-1);
        //由于中心点不需要绘制
        for (int i = 1; i < mSumCount; i++) {
            path.reset();
            //获取当前内圈的半径
            float currentR=r*i;
            //确定每一圈的顶点坐标
            for (int j = 0; j < mSumCount; j++) {
                //从第一个点开始绘制
                if (j==0) {
                    path.moveTo(mCenterX+currentR,mCenterY);
                }else {
                    float x= (float) (mCenterX+currentR*Math.cos(mAngle*j));
                    float y= (float) (mCenterY+currentR*Math.sin(mAngle*j));
                    path.lineTo(x,y);
                }
            }
            path.close();
            canvas.drawPath(path,mPaint1);
        }

    }
}
