package com.megaknytes.ftc.knytevision;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Collections;

public class KnyteVision {
    Point centerPoint1, centerPoint2;
    Point leftPoint1, leftPoint2;
    Point rightPoint1, rightPoint2;
    Position position = Position.NOT_DETECTED;

    public KnyteVision(Line team) {
        if(team == Line.FAR){
            leftPoint1 = new Point(230, 155);
            leftPoint2 = new Point(280, 210);

            centerPoint1 = new Point(230, 295);
            centerPoint2 = new Point(280, 340);

            rightPoint1 = new Point(230, 425);
            rightPoint2 = new Point(280, 480);
        } else {
            leftPoint1 = new Point(230, 70);
            leftPoint2 = new Point(280, 120);

            centerPoint1 = new Point(230, 200);
            centerPoint2 = new Point(280, 245);

            rightPoint1 = new Point(230, 305);
            rightPoint2 = new Point(280, 355);
        }
    }

    public Mat updatePosition(Mat frame) {
        ArrayList<Double> values = new ArrayList<>();
        Mat left = frame.submat(leftPoint1.x, leftPoint2.x, leftPoint1.y, leftPoint2.y);
        Mat center = frame.submat(centerPoint1.x, centerPoint2.x, centerPoint1.y, centerPoint2.y);
        Mat right = frame.submat(rightPoint1.x, rightPoint2.x, rightPoint1.y, rightPoint2.y);

        double redRegionRatioSum = 0.0;
        double blueRegionRatioSum = 0.0;
        int pixelCount = 0;
        for (int y = 0; y < left.size().height/2; y++){
            for (int x = 0; x < left.size().width/2; x++){
                redRegionRatioSum = (left.get(y, x)[0] / (left.get(y,x)[0] + left.get(y,x)[1] + left.get(y,x)[2]));
                blueRegionRatioSum = (left.get(y, x)[2] / (left.get(y, x)[0] + left.get(y, x)[1] + left.get(y, x)[2]));
                pixelCount++;
            }
        }
        values.add(Math.abs(redRegionRatioSum-blueRegionRatioSum));

        redRegionRatioSum = 0.0;
        blueRegionRatioSum = 0.0;
        pixelCount = 0;
        for (int y = 0; y < right.size().height/2; y++){
            for (int x = 0; x < right.size().width/2; x++){
                redRegionRatioSum = (right.get(y, x)[0] / (right.get(y,x)[0] + right.get(y,x)[1] + right.get(y,x)[2]));
                blueRegionRatioSum = (right.get(y, x)[2] / (right.get(y, x)[0] + right.get(y, x)[1] + right.get(y, x)[2]));
                pixelCount++;
            }
        }
        values.add(Math.abs(redRegionRatioSum-blueRegionRatioSum));

        redRegionRatioSum = 0.0;
        blueRegionRatioSum = 0.0;
        pixelCount = 0;
        for (int y = 0; y < center.size().height/2; y++){
            for (int x = 0; x < center.size().width/2; x++){
                redRegionRatioSum = (center.get(y, x)[0] / (center.get(y,x)[0] + center.get(y,x)[1] + center.get(y,x)[2]));
                blueRegionRatioSum = (center.get(y, x)[2] / (center.get(y, x)[0] + center.get(y, x)[1] + center.get(y, x)[2]));
                pixelCount++;
            }
        }
        values.add(Math.abs(redRegionRatioSum-blueRegionRatioSum));


        if(values.get(values.indexOf(Collections.max(values))) - values.get(values.indexOf(Collections.min(values))) <= .1){
            position = Position.NOT_DETECTED;
        } else {
            position = Position.values()[values.indexOf(Collections.max(values))];
        }

        return frame;
    }

    public Position getPosition() {
        return position;
    }

    public enum Position{
        LEFT,
        RIGHT,
        CENTER,
        NOT_DETECTED,
    }

    public enum Line {
        FAR,
        CLOSE,
    }
}

class Point{
    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
