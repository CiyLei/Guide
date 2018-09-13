package com.dj.android.annotation;

import javax.lang.model.element.VariableElement;

public class VariableInfo {
    int id;
    String description;
    String guideView;
    VariableElement variableElement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuideView() {
        return guideView;
    }

    public void setGuideView(String guideView) {
        this.guideView = guideView;
    }

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public void setVariableElement(VariableElement variableElement) {
        this.variableElement = variableElement;
    }
}
