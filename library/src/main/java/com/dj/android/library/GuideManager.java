package com.dj.android.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GuideManager {

    private static final String TAG = "GuideManager";

    private List<GuideView> mGuideViews = new ArrayList<>();
    private int guideViewIndex = 0;
    private AlertDialog mBackgroundDialog;
    private FrameLayout mBackgroundView;
    private GuideMaskView mGuideMaskView;
    private View mDescriptionView;

    private Listener listener;

    public List<GuideView> getGuideViews() {
        return mGuideViews;
    }

    public void addGuideViews(GuideView guideView) {
        this.mGuideViews.add(guideView);
    }

    public void start(Context context) {
        sortGuideViews();
        showBackgroundDialog(context);
        OnNext();
    }

    private void sortGuideViews() {
        Collections.sort(mGuideViews, new Comparator<GuideView>() {
            @Override
            public int compare(GuideView o1, GuideView o2) {
                return o1.getId() - o2.getId();
            }
        });
    }

    private void showBackgroundDialog(Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.GuideBackgroundDialog);
        mBackgroundDialog = alertBuilder.create();

        mBackgroundView = (FrameLayout) View.inflate(context, R.layout.guide_backgound_view, null);
        mGuideMaskView = mBackgroundView.findViewById(R.id.guide_view_mask);
        mGuideMaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnNext();
            }
        });
        mBackgroundDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null) {
                    listener.onFinsh();
                }
            }
        });

        mBackgroundDialog.setView(mBackgroundView);
        mBackgroundDialog.show();
        WindowManager.LayoutParams wlp = mBackgroundDialog.getWindow().getAttributes();
        wlp.width = GuideUtils.getScreenWidth(context);
        wlp.height = GuideUtils.getScreenHeight(context);
        //如果当前版本大于21,就会由投影效果,所以不需要屏幕变暗
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wlp.dimAmount = 0f;
        }
        mBackgroundDialog.getWindow().setAttributes(wlp);
    }

    private GuideView popGuideView() {
        if (mGuideViews.size() == 0 || guideViewIndex >= mGuideViews.size()) {
            return null;
        }
        GuideView gv = mGuideViews.get(guideViewIndex);
        guideViewIndex++;
        return gv;
    }

    private void OnNext() {
        GuideView gv = popGuideView();
        if (gv != null) {
            if (listener != null) {
                listener.onBefore(gv.getId());
            }
            mGuideMaskView.mask(gv);
            //镂空
            View descriptionView = gv.descriptionView(gv.getId());
            //添加说明view
            if (descriptionView != null) {
                if (mDescriptionView == null) {
                    mDescriptionView = descriptionView;
                    mBackgroundView.addView(mDescriptionView);
                } else {
                    //如果说明的view存在的话先移除，然后添加
                    mBackgroundView.removeView(mDescriptionView);
                    mDescriptionView = descriptionView;
                    mBackgroundView.addView(mDescriptionView);
                }
            }
        } else {
            if (mBackgroundDialog != null && mBackgroundDialog.isShowing()) {
                mBackgroundDialog.cancel();
                mBackgroundDialog = null;
            }
        }
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onBefore(int id);
        void onFinsh();
    }
}
