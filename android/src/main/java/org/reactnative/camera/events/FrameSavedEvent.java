package org.reactnative.camera.events;

import androidx.core.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.reactnative.camera.CameraViewManager;
import org.reactnative.camera.tasks.FrameDescr;

public class FrameSavedEvent extends Event<FrameSavedEvent> {
  private static final Pools.SynchronizedPool<FrameSavedEvent> EVENTS_POOL =
      new Pools.SynchronizedPool<>(3);

  private String mUri;
  private int mWidth;
  private int mHeight;

  private FrameSavedEvent() {}

  public static FrameSavedEvent obtain(int viewTag, FrameDescr frameDescr) {
    FrameSavedEvent event = EVENTS_POOL.acquire();
    if (event == null) {
      event = new FrameSavedEvent();
    }
    event.init(viewTag, frameDescr);
    return event;
  }

  private void init(int viewTag, FrameDescr frameDescr) {
    super.init(viewTag);
    if (frameDescr != null) {
      mUri = frameDescr.getUri();
      mWidth = frameDescr.getWidth();
      mHeight = frameDescr.getHeight();
    }
  }

  @Override
  public short getCoalescingKey() {
    int hashCode = mUri.hashCode() % Short.MAX_VALUE;
    return (short) hashCode;
  }

  @Override
  public String getEventName() {
    return CameraViewManager.Events.EVENT_ON_FRAME_SAVED.toString();
  }

  @Override
  public void dispatch(RCTEventEmitter rctEventEmitter) {
    rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
  }

  private WritableMap serializeEventData() {
    WritableMap event = Arguments.createMap();
    event.putInt("target", getViewTag());
    event.putString("uri", mUri);
    event.putInt("width", mWidth);
    event.putInt("height", mHeight);
    return event;
  }
}
