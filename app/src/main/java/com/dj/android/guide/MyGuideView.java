package com.dj.android.guide;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

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
     * 返回说明的view
     * @param id
     * @return
     */
    @Override
    public View descriptionView(int id) {
        TextView vg = (TextView) View.inflate(getView().getContext(), R.layout.view_guide_description, null);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(GuideUtils.dip2px(getView().getContext(), 200),
                GuideUtils.dip2px(getView().getContext(), 60));
        vg.setText(getDescription());
        vg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getManager() != null) {
                    getManager().onNext();
                }
            }
        });
        if (getView() != null) {
            int[] location = new int[2];
            getView().getLocationOnScreen(location);
            //先测量大小
            vg.measure(
                    View.MeasureSpec.makeMeasureSpec(GuideUtils.dip2px(getView().getContext(), 200), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(GuideUtils.dip2px(getView().getContext(), 60), View.MeasureSpec.EXACTLY));
            Rect descriptionLocation = getDescriptionLocation(vg, location, 100, 100);
            flp.leftMargin = descriptionLocation.left;
            flp.topMargin = descriptionLocation.top;
            vg.setLayoutParams(flp);
        }
        return vg;
    }

    /**
     * 在镂空完毕之后的绘制
     * @param canvas
     */
    @Override
    public void onGuideDraw(int id, Canvas canvas, View descriptionView) {
        if (getView() != null) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(100);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);

            int[] location = new int[2];
            getView().getLocationOnScreen(location);
            Rect rect = new Rect(location[0], location[1], location[0] + getView().getWidth(), location[1] + getView().getHeight());

            canvas.drawBitmap(BitmapFactory.decodeResource(getView().getContext().getResources(), R.mipmap.back), null, rect, paint);
            //获取说明view的坐标
            Rect descriptionLocation = getDescriptionLocation(descriptionView, location, 100, 100);
            //连线
            if (descriptionLocation.top >= getView().getTop()) {
                canvas.drawLine(location[0] + (getView().getWidth() / 2),
                        location[1] + getView().getHeight(),
                        descriptionLocation.left + (descriptionLocation.width() / 2),
                        descriptionLocation.top,
                        paint);
            } else {
                canvas.drawLine(location[0] + (getView().getWidth() / 2),
                        location[1],
                        descriptionLocation.left + (descriptionLocation.width() / 2),
                        descriptionLocation.top + descriptionLocation.height(),
                        paint);
            }
        }
    }

    /**
     * 阻止点击背景下一步
     * @return
     */
    @Override
    public boolean onClickBackgroundNext() {
        return false;
    }
}
