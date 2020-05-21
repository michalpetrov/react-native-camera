package org.reactnative.camera.tasks;

public interface FrameSaverAsyncTaskDelegate {

    void onFrameSaved(FrameDescr frameDescr);

    void onFrameSavingTaskCompleted();
}
