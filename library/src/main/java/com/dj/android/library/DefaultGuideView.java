package com.dj.android.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DefaultGuideView extends GuideView {

    private static final String TAG = "DefaultGuideView";

    public DefaultGuideView(int id, View view, String description) {
        super(id, view, description);
    }

    @Override
    public int getMaskColor(int id) {
        return Color.parseColor("#a0000000");
    }

    /**
     * 绘制镂空
     * @param id
     * @param canvas
     * @param paint
     */
    @Override
    public void drawHollow(int id, final Canvas canvas, final Paint paint) {
        if (getView() != null) {
            int[] location = new int[2];
            getView().getLocationOnScreen(location);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(location[0], location[1], location[0] + getView().getWidth(), location[1] + getView().getHeight(), 10, 10, paint);
            } else {
                canvas.drawRect(location[0], location[1], location[0] + getView().getWidth(), location[1] + getView().getHeight(), paint);
            }
            Log.d(TAG, "drawHollow: " + location[0] + "," + location[1] + "," + (location[0] + getView().getWidth()) + "," + (location[1] + getView().getHeight()));
        }
    }

    /**
     * 返回说明的view
     * @param id
     * @return
     */
    @Override
    public View descriptionView(int id) {
        ViewGroup vg = null;
        if (getView() != null) {
            vg = (ViewGroup) View.inflate(getView().getContext(), R.layout.guide_description, null);
            TextView tv = vg.findViewById(R.id.guide_description_tv);
            tv.setText(getDescription());

            int[] location = new int[2];
            getView().getLocationOnScreen(location);

            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            //先测量大小
            vg.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Rect descriptionLocation = getDescriptionLocation(vg, location);
            flp.leftMargin = descriptionLocation.left;
            flp.topMargin = descriptionLocation.top;
            vg.setLayoutParams(flp);
        }

        return vg;
    }

    /**
     * 根据镂空的view自动计算说明的view该放哪里
     * @param descriptionView
     * @param viewLocation
     * @return
     */
    public Rect getDescriptionLocation(View descriptionView, int[] viewLocation) {
        return getDescriptionLocation(descriptionView, viewLocation, 0, 0);
    }

    public Rect getDescriptionLocation(View descriptionView, int[] viewLocation, int dx, int dy) {
        int descriptionViewLeft = 0;
        int descriptionViewTop = 0;
        int tl = (viewLocation[0] + getView().getWidth()) * viewLocation[1];
        int tr = (GuideUtils.getScreenWidth(descriptionView.getContext()) - viewLocation[0]) * viewLocation[1];
        int bl = (viewLocation[0] + getView().getWidth()) * (GuideUtils.getScreenHeight(descriptionView.getContext()) - viewLocation[1] - getView().getHeight());
        int br = (GuideUtils.getScreenWidth(descriptionView.getContext()) - viewLocation[0]) * (GuideUtils.getScreenHeight(descriptionView.getContext()) - viewLocation[1] - getView().getHeight());
        //取上下左右最大的面积大小
        int maxArea = Math.max(Math.max(Math.max(tl, tr), bl), br);
        Log.d(TAG, "onGlobalLayout: " + descriptionView.getMeasuredWidth() + "," + descriptionView.getMeasuredHeight() + "," +
                (descriptionView.getMeasuredWidth() * descriptionView.getMeasuredHeight()) + "," + maxArea );
        if (descriptionView.getMeasuredWidth() * descriptionView.getMeasuredHeight() <= maxArea) {
            if (tl == maxArea) {
                descriptionViewLeft = viewLocation[0] + getView().getWidth() - descriptionView.getMeasuredWidth() - dx;
                descriptionViewTop = viewLocation[1] - descriptionView.getMeasuredHeight() - dy;
            } else if (tr == maxArea) {
                descriptionViewLeft = viewLocation[0] + dx;
                descriptionViewTop = viewLocation[1] - descriptionView.getMeasuredHeight() - dy;
            } else if (bl == maxArea) {
                descriptionViewLeft = viewLocation[0] + getView().getWidth() - descriptionView.getMeasuredWidth() - dx;
                descriptionViewTop = viewLocation[1] + getView().getHeight() + dy;
            } else if (br == maxArea) {
                descriptionViewLeft = viewLocation[0] + dx;
                descriptionViewTop = viewLocation[1] + getView().getHeight() + dy;
            }
        } else {
            //居中显示
            descriptionViewLeft = viewLocation[0] + (getView().getWidth() / 2) - (descriptionView.getMeasuredWidth() / 2) + dx;
            descriptionViewTop = viewLocation[1] + (getView().getHeight() / 2) - (descriptionView.getMeasuredHeight() / 2) + dy;
        }
        Log.d(TAG, "getDescriptionLocation: " + descriptionViewLeft + "," + descriptionViewTop);
        return new Rect(descriptionViewLeft, descriptionViewTop, descriptionViewLeft + descriptionView.getMeasuredWidth(),
                descriptionViewTop + descriptionView.getMeasuredHeight());
    }
}
