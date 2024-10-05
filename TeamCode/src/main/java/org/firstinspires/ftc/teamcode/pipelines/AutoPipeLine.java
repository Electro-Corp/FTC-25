package org.firstinspires.ftc.teamcode.pipelines;

import android.media.audiofx.AcousticEchoCanceler;

import org.apache.commons.math3.geometry.hull.ConvexHull;
import org.firstinspires.ftc.teamcode.subsystems.Marker;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AutoPipeLine extends OpenCvPipeline {
    private Marker marker;
    private Point targetLoc;
    Mat hsvMat = new Mat(), hierarchyMat = new Mat(), hsvThresholdMat = new Mat(), erosionElement = new Mat(), dilationElement = new Mat(), cutOff = new Mat();

    //configurations
    int erosionKernelSize = 2;
    int dilationKernelSize = 8;
    int elementType = Imgproc.CV_SHAPE_RECT;

    int x = 0, y = 0, width = 0, height = 0;

    public AutoPipeLine(Marker marker, Point targetLoc){
        this.marker = marker;
        this.targetLoc = targetLoc;
    }

    @Override
    public Mat processFrame(Mat inputRaw) {
        Imgproc.cvtColor(inputRaw, hsvMat, Imgproc.COLOR_RGB2HSV);

        //Rect roi = new Rect(0, (inputRaw.rows() / 3), inputRaw.cols(), inputRaw.rows() - ((inputRaw.rows() / 3)));
        Mat input = inputRaw;

        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

        Scalar lowHSV = new Scalar(marker.getHueMin(), 120, 80); // FILL IN WITH VALS
        Scalar highHSV = new Scalar(marker.getHueMax(), 255, 255);

        Core.inRange(hsvMat, lowHSV, highHSV, hsvThresholdMat);

        if(marker.getHueWrapAroundMin() > 1) {
            lowHSV = new Scalar(marker.getHueWrapAroundMin(), 120, 100); // FILL IN WITH VALS
            highHSV = new Scalar(marker.getHueWrapAroundMax(), 255, 255);

            Core.inRange(hsvMat, lowHSV, highHSV, hsvThresholdMat);
        }

        //erode then dilate the image
        erosionElement = Imgproc.getStructuringElement(elementType, new Size(2 * erosionKernelSize + 1, 2 * erosionKernelSize + 1), new Point(erosionKernelSize, erosionKernelSize));
        Imgproc.erode(hsvThresholdMat, hsvThresholdMat, erosionElement);
        dilationElement = Imgproc.getStructuringElement(elementType, new Size(2 * dilationKernelSize + 1, 2 * dilationKernelSize + 1), new Point(dilationKernelSize, dilationKernelSize));
        Imgproc.dilate(hsvThresholdMat, hsvThresholdMat, dilationElement);

        List<MatOfPoint> contourPoints = new ArrayList<>();
        Imgproc.findContours(hsvThresholdMat, contourPoints, hierarchyMat, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint closest = null;
        if(contourPoints.size() > 0)
            closest = contourPoints.get(getClosestToPoint(contourPoints));

        if(closest != null){
            // It exist so get its position

            Moments moment = Imgproc.moments(closest);


            Rect boundingBox = Imgproc.boundingRect(closest);

            x = (int) boundingBox.x;//(moment.get_m10() / moment.get_m00());
            y = (int) boundingBox.y;//(moment.get_m01() / moment.get_m00());


            width = (int) boundingBox.width;
            height = (int) boundingBox.height;


            Imgproc.putText(
                    inputRaw,
                    String.format(
                            Locale.US,
                            "Position (%d, %d) | Size (%d %d)",
                            (int) x,
                            (int) y,
                            (int) width,
                            (int) height
                    ),
                    new Point(0, 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    .75,
                    new Scalar(120.0,100.0, 100.0),
                    2
            );

            Imgproc.rectangle(
                    inputRaw,
                    boundingBox,
                    new Scalar(0, 255, 0),
                    2
            );

            // approx
            MatOfPoint2f contour2f = new MatOfPoint2f(closest.toArray());
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);
            MatOfPoint approx = new MatOfPoint(approxCurve.toArray());

            List<MatOfPoint> approxList = new ArrayList<>();
            approxList.add(approx);

            Imgproc.drawContours(inputRaw, approxList, -1, new Scalar(235, 52, 189), 2);

            Imgproc.line(
                    inputRaw,
                    new Point(x, 0),
                    new Point(x, y),
                    new Scalar(0, 0, 255),
                    4
            );

            Imgproc.line(
                    inputRaw,
                    new Point(0, y),
                    new Point(x , y),
                    new Scalar(0, 0, 255),
                    4
            );

            // Center of box
            Imgproc.line(
                    inputRaw,
                    new Point(x, 0),
                    new Point(x + (width / 2), y + (height / 2)),
                    new Scalar(0, 255, 255),
                    4
            );

            Imgproc.line(
                    inputRaw,
                    new Point(0, y),
                    new Point(x + (width / 2), y + (height / 2)),
                    new Scalar(0, 255, 255),
                    4
            );


            return inputRaw;
        }


        return hsvThresholdMat;
    }

    private int getLargestContourSize(List<MatOfPoint> contours) {
        int index = 0;
        double large = 0.0f;
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) > large){
                large = Imgproc.contourArea(contours.get(i));
                index = i;
            }
        }
        return index;
    }

    private int getClosestToPoint(List<MatOfPoint> contours){
        int tarIndex = 0;
        double dist = Float.MAX_VALUE;

        for(int i = 0; i < contours.size(); i++){
            Rect boundingBox = Imgproc.boundingRect(contours.get(i));
            if(getDist(new Point(boundingBox.x, boundingBox.y), targetLoc) < dist){
                tarIndex = i;
                dist = getDist(new Point(boundingBox.x, boundingBox.y), targetLoc);
            }
        }


        return tarIndex;
    }

    private double getDist(Point p1, Point p2){
        return Math.sqrt(
                Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)
        );
    }

    public int getX() {
        return x;
    }
    public int getY(){return y;}
}