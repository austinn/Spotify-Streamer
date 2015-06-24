package com.refect.spotifystreamer.models;

/**
 * Created by anelson on 6/17/15.
 */
public class NavigationModel {

    private String name;
    private int resId;

    public NavigationModel() {

    }

    public NavigationModel(String name, int resId) {
        setName(name);
        setResId(resId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
