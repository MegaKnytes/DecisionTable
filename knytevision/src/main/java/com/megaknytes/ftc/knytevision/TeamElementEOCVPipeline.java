package com.megaknytes.ftc.KnyteVision;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public class TeamElementEOCVPipeline extends OpenCvPipeline {
    public KnyteVision.Position position;
    public KnyteVision knyteVision;

    public TeamElementEOCVPipeline(KnyteVision.Line alliance){
        knyteVision = new KnyteVision(alliance);
    }

    @Override
    public Mat processFrame(Mat input) {
        return knyteVision.updatePosition(input);
    }
}
