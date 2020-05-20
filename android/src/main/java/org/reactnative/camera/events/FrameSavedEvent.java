package org.reactnative.camera.events;

import androidx.core.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.reactnative.camera.CameraViewManager;

public class FrameSavedEvent extends Event<FrameSavedEvent> {
  private static final Pools.SynchronizedPool<FrameSavedEvent> EVENTS_POOL =
      new Pools.SynchronizedPool<>(3);

  private String mUri;

  private FrameSavedEvent() {}

  public static FrameSavedEvent obtain(int viewTag, String uri) {
    FrameSavedEvent event = EVENTS_POOL.acquire();
    if (event == null) {
      event = new FrameSavedEvent();
    }
    event.init(viewTag, uri);
    return event;
  }

  private void init(int viewTag, String uri) {
    super.init(viewTag);
    mUri = uri;
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
    return event;
  }
}
