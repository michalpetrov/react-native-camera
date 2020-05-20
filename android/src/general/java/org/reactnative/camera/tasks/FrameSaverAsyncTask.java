package org.reactnative.camera.tasks;

public class FrameSaverAsyncTask extends android.os.AsyncTask<Void, Void, String> {
  private byte[] mImageData;
  private FrameSaverAsyncTaskDelegate mDelegate;

  public FrameSaverAsyncTask(FrameSaverAsyncTaskDelegate delegate, byte[] imageData) {
    mImageData = imageData;
    mDelegate = delegate;
  }

  @Override
  protected String doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null) {
      return null;
    }
    //todo return uri
    return "tralala";
//    RNFrame frame = RNFrameFactory.buildFrame(mImageData, mWidth, mHeight, mRotation);
//    return mBarcodeDetector.detect(frame);
  }

  @Override
  protected void onPostExecute(String uri) {
    super.onPostExecute(uri);

    if (uri == null) {
//      mDelegate.onBarcodeDetectionError(mBarcodeDetector);
    } else {
      mDelegate.onFrameSaved(uri);
      mDelegate.onFrameSavingTaskCompleted();
    }
  }

}
