package org.reactnative.camera.tasks;

public final class FrameDescr {
    private String uri;
    private int width;
    private int height;

    public FrameDescr(String uri, int width, int height) {
        this.uri = uri;
        this.width = width;
        this.height = height;
    }

    public String getUri() {
        return uri;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
