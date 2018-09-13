package com.dj.android.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
        ViewGroup vg = (ViewGroup) View.inflate(getView().getContext(), R.layout.guide_description, null);
        TextView tv = vg.findViewById(R.id.guide_description_tv);
        tv.setText(getDescription());

        if (getView() != null) {
            int[] location = new int[2];
            getView().getLocationOnScreen(location);
            vg.getViewTreeObserver().addOnGlobalLayoutListener(new DescriptionViewTreeObserver(vg, location));
        }

        return vg;
    }

    public class DescriptionViewTreeObserver implements ViewTreeObserver.OnGlobalLayoutListener {

        int flag = 0;
        View vg;
        int[] location;
        FrameLayout.LayoutParams lp;

        public DescriptionViewTreeObserver(View vg, int[] location) {
            this.vg = vg;
            this.location = location;
        }

        public DescriptionViewTreeObserver(View vg, int[] location, FrameLayout.LayoutParams lp) {
            this.vg = vg;
            this.location = location;
            this.lp = lp;
        }

        @Override
        public void onGlobalLayout() {
            if (flag < 5) {
                FrameLayout.LayoutParams flp = (lp == null ? new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT) : lp);
                int tl = (location[0] + getView().getWidth()) * location[1];
                int tr = (GuideUtils.getScreenWidth(vg.getContext()) - location[0]) * location[1];
                int bl = (location[0] + getView().getWidth()) * (GuideUtils.getScreenHeight(vg.getContext()) - location[1] - getView().getHeight());
                int br = (GuideUtils.getScreenWidth(vg.getContext()) - location[0]) * (GuideUtils.getScreenHeight(vg.getContext()) - location[1] - getView().getHeight());
                //取上下左右最大的面积大小
                int maxArea = Math.max(Math.max(Math.max(tl, tr), bl), br);
                Log.d(TAG, "onGlobalLayout: " + vg.getMeasuredWidth() + "," + vg.getMeasuredHeight() + "," +
                        (vg.getMeasuredWidth() * vg.getMeasuredHeight()) + "," + maxArea );
                if (vg.getMeasuredWidth() * vg.getMeasuredHeight() <= maxArea) {
                    if (tl == maxArea) {
                        flp.leftMargin = location[0] + getView().getWidth() - vg.getMeasuredWidth();
                        flp.topMargin = location[1] - vg.getMeasuredHeight();
                    } else if (tr == maxArea) {
                        flp.leftMargin = location[0];
                        flp.topMargin = location[1] - vg.getMeasuredHeight();
                    } else if (bl == maxArea) {
                        flp.leftMargin = location[0] + getView().getWidth() - vg.getMeasuredWidth();
                        flp.topMargin = location[1] + getView().getHeight();
                    } else if (br == maxArea) {
                        flp.leftMargin = location[0];
                        flp.topMargin = location[1] + getView().getHeight();
                    }
                } else {
                    //因为第一次返回的位置不正常，所以不操作
                    if (flag != 0) {
                        flp.leftMargin = location[0];
                        flp.topMargin = location[1];
                    }
                }
                vg.setLayoutParams(flp);
                vg.setVisibility(flag < 4 ? View.INVISIBLE : View.VISIBLE);
            } else {
                vg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            flag++;
        }
    }
}
