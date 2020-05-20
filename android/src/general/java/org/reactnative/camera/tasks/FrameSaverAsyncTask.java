package org.reactnative.camera.tasks;

import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(mImageData, 0, mImageData.length, options);

            imageFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", mCacheDir);
            imageFile.createNewFile();

            YuvImage image = new YuvImage(mImageData, ImageFormat.NV21, options.outWidth, options.outHeight, null);
            Rect rectangle = new Rect();
            rectangle.bottom = options.outHeight;
            rectangle.top = 0;
            rectangle.left = 0;
            rectangle.right = options.outWidth;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compressToJpeg(rectangle, 100, baos);

            FileOutputStream fOut = new FileOutputStream(imageFile);
            fOut.write(baos.toByteArray());
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
