package fer.zesoi.bodyapp;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

public class BackgroundSubtractor {

    private Mat backgroundMat;

    // Initialize the background subtractor with the background frame
    public void initBackground(Bitmap backgroundBitmap) {
        backgroundMat = new Mat();
        Utils.bitmapToMat(
                backgroundBitmap,
                backgroundMat
        );
    }

    // Subtract the background from the foreground frame
    public Bitmap subtract(Bitmap foregroundBitmap) {
        Mat foregroundMat = new Mat();
        Utils.bitmapToMat(
                foregroundBitmap,
                foregroundMat
        );
        Mat difference = new Mat();
        Core.absdiff(foregroundMat, backgroundMat, difference);
        Imgproc.threshold(difference, difference, 25, 255, Imgproc.THRESH_BINARY);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.dilate(difference, difference, kernel);

        Bitmap output = Bitmap.createBitmap(difference.cols(), difference.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(difference, output);
        return output;
    }
}
