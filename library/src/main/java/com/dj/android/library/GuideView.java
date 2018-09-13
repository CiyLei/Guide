package com.dj.android.library;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

public abstract class GuideView {

    private int id;
    private View view;
    private String description;
    private GuideManager manager;

    public GuideView(int id, View view, String description) {
        this.id = id;
        this.view = view;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public View getView() {
        return view;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GuideManager getManager() {
        return manager;
    }

    public void setManager(GuideManager manager) {
        this.manager = manager;
    }

    /**
     * 获取蒙板颜色
     * @return
     */
    public abstract int getMaskColor(int id);

    /**
     * 绘制镂空
     */
    public abstract void drawHollow(int id, Canvas canvas, Paint paint);

    /**
     * 返回说明的view
     * @return
     */
    public abstract View descriptionView(int id);

    /**
     * 在蒙板绘制完毕之后的画板
     * @param canvas
     */
    public void onGuideDraw(int id, Canvas canvas, View descriptionView){};

    /**
     * 点击背景下一步
     * @return
     */
    public boolean onClickBackgroundNext() {
        return true;
    }
}
