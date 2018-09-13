package com.dj.android.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.Nullable;

public class GuideMaskView extends View {

    private static final String TAG = "GuideMaskView";

    private GuideView mGuideView;
    private Paint mPaint;
    private RectF mRectF;
    private Xfermode mXfermode_DST_OUT;
    private boolean guideDrawFlag = false;
    private View descriptionView;

    public GuideMaskView(Context context) {
        this(context, null);
    }

    public GuideMaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void mask(GuideView guideView) {
        this.mGuideView = guideView;
        this.invalidate();
    }

    public void guideDraw(GuideView guideView, View descriptionView) {
        this.mGuideView = guideView;
        this.descriptionView = descriptionView;
        guideDrawFlag = true;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: " + mGuideView);
        if (mGuideView != null) {
            initRect();

//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            //新建一个图层
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer(mRectF, mPaint);
            } else {
                canvas.saveLayer(mRectF, mPaint, Canvas.ALL_SAVE_FLAG);
            }

            mPaint.setColor(mGuideView.getMaskColor(mGuideView.getId()));
            mPaint.setXfermode(null);
            canvas.drawRect(mRectF, mPaint);
            Log.d(TAG, "initRect: " + mRectF);
            mPaint.setColor(Color.WHITE);
            mPaint.setXfermode(mXfermode_DST_OUT);
            mGuideView.drawHollow(mGuideView.getId(), canvas, mPaint);
            mPaint.setXfermode(null);
            if (guideDrawFlag) {
                mGuideView.onGuideDraw(mGuideView.getId(), canvas, descriptionView);
                guideDrawFlag = false;
            }
        }
    }

    private void initRect() {
        if (mRectF == null) {
            mRectF = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mXfermode_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }
}
