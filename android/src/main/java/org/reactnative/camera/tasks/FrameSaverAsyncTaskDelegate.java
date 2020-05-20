package org.reactnative.camera.tasks;

public interface FrameSaverAsyncTaskDelegate {

    void onFrameSaved(String uri);

    void onFrameSavingTaskCompleted();
}
