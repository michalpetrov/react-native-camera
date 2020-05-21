package org.reactnative.camera.tasks;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class FrameSaverAsyncTask extends android.os.AsyncTask<Void, Void, FrameDescr> {
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
        mRotation = 270;
        mCacheDir = cacheDir;
        mDelegate = delegate;
    }

    @Override
    protected FrameDescr doInBackground(Void... ignored) {
        Log.i("RNC", "Saving frame");
        if (isCancelled() || mDelegate == null) {
            return null;
        }
        try {
            File imageFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", mCacheDir);
            imageFile.createNewFile();

            byte[] rotatedImageData = rotateNV21(mImageData, mWidth, mHeight, mRotation);
            int rotatedImageWidth = mRotation == 90 || mRotation == 270 ? mHeight : mWidth;
            int rotatedImageHeight = mRotation == 90 || mRotation == 270 ? mWidth : mHeight;
            YuvImage image = new YuvImage(rotatedImageData, ImageFormat.NV21, rotatedImageWidth, rotatedImageHeight, null);
            Rect rectangle = new Rect();
            rectangle.bottom = rotatedImageHeight;
            rectangle.top = 0;
            rectangle.left = 0;
            rectangle.right = rotatedImageWidth;

            FileOutputStream fOut = new FileOutputStream(imageFile);
            image.compressToJpeg(rectangle, 90, fOut);
            fOut.flush();
            fOut.close();
            return new FrameDescr(Uri.fromFile(imageFile).toString(), rotatedImageWidth, rotatedImageHeight);
        } catch (Exception e) {
            Log.e("RNC", "Failed to save", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(FrameDescr frameDescr) {
        super.onPostExecute(frameDescr);

        if (frameDescr == null) {
        } else {
            mDelegate.onFrameSaved(frameDescr);
            mDelegate.onFrameSavingTaskCompleted();
        }
    }

    public static byte[] rotateNV21(final byte[] yuv,
                                    final int width,
                                    final int height,
                                    final int rotation)
    {
        if (rotation == 0) return yuv;
        if (rotation % 90 != 0 || rotation < 0 || rotation > 270) {
            throw new IllegalArgumentException("0 <= rotation < 360, rotation % 90 == 0");
        }

        final byte[]  output    = new byte[yuv.length];
        final int     frameSize = width * height;
        final boolean swap      = rotation % 180 != 0;
        final boolean xflip     = rotation % 270 != 0;
        final boolean yflip     = rotation >= 180;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                final int yIn = j * width + i;
                final int uIn = frameSize + (j >> 1) * width + (i & ~1);
                final int vIn = uIn       + 1;

                final int wOut     = swap  ? height              : width;
                final int hOut     = swap  ? width               : height;
                final int iSwapped = swap  ? j                   : i;
                final int jSwapped = swap  ? i                   : j;
                final int iOut     = xflip ? wOut - iSwapped - 1 : iSwapped;
                final int jOut     = yflip ? hOut - jSwapped - 1 : jSwapped;

                final int yOut = jOut * wOut + iOut;
                final int uOut = frameSize + (jOut >> 1) * wOut + (iOut & ~1);
                final int vOut = uOut + 1;

                output[yOut] = (byte)(0xff & yuv[yIn]);
                output[uOut] = (byte)(0xff & yuv[uIn]);
                output[vOut] = (byte)(0xff & yuv[vIn]);
            }
        }
        return output;
    }

}
