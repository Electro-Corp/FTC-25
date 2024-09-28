package org.firstinspires.ftc.teamcode.pipelines;

import android.graphics.MaskFilter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.Locale;

public class CalibratePipeline extends OpenCvPipeline {

    // Notice this is declared as an instance variable (and re-used), not a local variable
    Mat hsvMat = new Mat(), cutOff = new Mat();

    double[] values = new double[3];

    @Override
    public Mat processFrame(Mat input)
    {
        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

        int size = 30;
        Rect rect = new Rect(((input.width() - size) / 2), (input.height() - size) / 2, size, size);
        Imgproc.rectangle(input, rect, new Scalar(0, 255, 0), 1, 8, 0);
        values = hsvMat.get(input.height() / 2, input.width() / 2);
        Imgproc.putText(
                input,
                String.format(
                        Locale.US,
                        "HSV, (%d, %d, %d)",
                        (int) values[0], (int) values[1], (int) values[2]
                ),
                new Point(0, 20),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                .75,
                new Scalar(0, 0, 0),
                2
        );

        return input;
    }

    public double[] getHSVValues()
    {
        return values;
    }

    public Mat getMat()
    {
        return hsvMat;
    }
}