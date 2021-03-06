package com.dj.android.library;

import android.content.Context;

public class Guide {

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private GuideManager guideManager;

        public Builder() {
            guideManager = new GuideManager();
        }

        public Builder add(GuideView guideView) {
            guideManager.addGuideViews(guideView);
            return this;
        }

        public void show(Context context) {
            guideManager.show(context);
        }

        public void show(Context context, GuideManager.Listener listener) {
            guideManager.setListener(listener);
            guideManager.show(context);
        }
    }

}
