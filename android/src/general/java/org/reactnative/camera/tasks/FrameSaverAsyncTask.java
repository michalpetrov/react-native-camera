package org.reactnative.camera.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.vision.Frame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FrameSaverAsyncTask extends android.os.AsyncTask<Void, Void, String> {
    private byte[] mImageData;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private File mCacheDir;
    private FrameSaverAsyncTaskDelegate mDelegate;

    public FrameSaverAsyncTask(FrameSaverAsyncTaskDelegate delegate, byte[] imageData, int width, int height, int rotation, File cacheDir) {
        mImageData = imageData;
        mWidth = width;
        mHeight = height;
        mRotation = rotation;
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
            Frame.Builder builder = new Frame.Builder();
            ByteBuffer byteBuffer = ByteBuffer.wrap(mImageData);
            builder.setImageData(byteBuffer, mWidth, mHeight, ImageFormat.NV21);
            switch (mRotation) {
                case 90:
                    builder.setRotation(Frame.ROTATION_90);
                    break;
                case 180:
                    builder.setRotation(Frame.ROTATION_180);
                    break;
                case 270:
                    builder.setRotation(Frame.ROTATION_270);
                    break;
                default:
                    builder.setRotation(Frame.ROTATION_0);
            }


//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeByteArray(mImageData, 0, mImageData.length, options);

            imageFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", mCacheDir);
            imageFile.createNewFile();

//            YuvImage image = new YuvImage(mImageData, ImageFormat.NV21, mWidth, mHeight, null);
//            Rect rectangle = new Rect();
//            rectangle.bottom = mHeight;
//            rectangle.top = 0;
//            rectangle.left = 0;
//            rectangle.right = mWidth;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            image.compressToJpeg(rectangle, 100, baos);

            FileOutputStream fOut = new FileOutputStream(imageFile);
            Frame frame = builder.build();
            Bitmap bitmap = frame.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
//            fOut.write(baos.toByteArray());
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
