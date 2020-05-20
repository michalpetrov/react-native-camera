package org.reactnative.camera.tasks;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class FrameSaverAsyncTask extends android.os.AsyncTask<Void, Void, String> {
    private byte[] mImageData;
    private File mCacheDir;
    private FrameSaverAsyncTaskDelegate mDelegate;

    public FrameSaverAsyncTask(FrameSaverAsyncTaskDelegate delegate, byte[] imageData, File cacheDir) {
        mImageData = imageData;
        mCacheDir = cacheDir;
        mDelegate = delegate;
    }

    @Override
    protected String doInBackground(Void... ignored) {
        Log.i("RNC", "Saving frame");
        if (isCancelled() || mDelegate == null) {
            return null;
        }
        File imageFile;
        try {
            imageFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", mCacheDir);
            imageFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(imageFile);
            fOut.write(mImageData);
            fOut.flush();
            fOut.close();
            return Uri.fromFile(imageFile).toString();
        } catch (Exception e) {
            Log.e("RNC", "Failed to save", e);
            return null;
        }
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
