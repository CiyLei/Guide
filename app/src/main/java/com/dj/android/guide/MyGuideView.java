package com.dj.android.guide;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

import com.dj.android.library.DefaultGuideView;
import com.dj.android.library.GuideUtils;

public class MyGuideView extends DefaultGuideView {

    private static final String TAG = "MyGuideView";

    public MyGuideView(int id, View view, String description) {
        super(id, view, description);
    }

    /**
     * 绘制镂空
     * @param id
     * @param canvas
     * @param paint
     */
    @Override
    public void drawHollow(int id, Canvas canvas, Paint paint) {
        if (getView() != null) {
            int[] location = new int[2];
            getView().getLocationOnScreen(location);
            Rect rect = new Rect(location[0], location[1], location[0] + getView().getWidth(), location[1] + getView().getHeight());
            canvas.drawBitmap(BitmapFactory.decodeResource(getView().getContext().getResources(), R.mipmap.mask), null, rect, paint);
        }
    }

    /**
     * 在镂空完毕之后的绘制
     * @param canvas
     */
    @Override
    public void OnGuideDraw(int id, Canvas canvas) {
        if (getView() != null) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(100);
            paint.setAntiAlias(true);

            int[] location = new int[2];
            getView().getLocationOnScreen(location);
            Rect rect = new Rect(location[0], location[1], location[0] + getView().getWidth(), location[1] + getView().getHeight());

            canvas.drawBitmap(BitmapFactory.decodeResource(getView().getContext().getResources(), R.mipmap.back), null, rect, paint);
        }
    }

    /**
     * 返回说明的view
     * @param id
     * @return
     */
    @Override
    public View descriptionView(int id) {
        View vg = View.inflate(getView().getContext(), R.layout.view_guide_description, null);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(GuideUtils.dip2px(getView().getContext(), 200),
                GuideUtils.dip2px(getView().getContext(), 60));
        if (getView() != null) {
            int[] location = new int[2];
            getView().getLocationOnScreen(location);
            vg.getViewTreeObserver().addOnGlobalLayoutListener(new DescriptionViewTreeObserver(vg, location, flp));
        }
        return vg;
    }
}
