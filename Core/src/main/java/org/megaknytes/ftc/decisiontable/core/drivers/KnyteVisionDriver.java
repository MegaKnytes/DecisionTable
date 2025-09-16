package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.megaknytes.ftc.decisiontable.core.utilities.KnyteVision.ImageClass;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class KnyteVisionDriver {

    static final int STREAM_WIDTH = 1920; // modify for your camera
    static final int STREAM_HEIGHT = 1080; // modify for your camera
    static ImageClass sampleImage;
    static int numTemplates = 5;
    static double templateScale = 0.1;
    static OpenCvWebcam webcam;
    static OpenCvPipeline pipeline;
    static boolean isInitialized = false;
    static int capturedBestX;
    static int capturedBestY;
    static int capturedBestIndex;


    public KnyteVisionDriver(int numKnyteVision) {

    }


    public static void init(String IOName, int channel, double initVal, String deviceName, HardwareMap hwMap) {
        String templateFilename = "BallTemplate-Small.txt";
        int condenseRatio = 2;

        if (!isInitialized) {
            //System.out.println("======> " + "initializing KnyteVision");

            sampleImage = new ImageClass(numTemplates, templateFilename, templateScale, condenseRatio, hwMap);

            int cameraMonitorViewId = hwMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hwMap.appContext.getPackageName());
            webcam = OpenCvCameraFactory.getInstance().createWebcam(hwMap.get(WebcamName.class, deviceName), cameraMonitorViewId);

            webcam.setPipeline(new ImagePipeline());

            webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    webcam.startStreaming(160, 120, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {
                    /*
                     * This will be called if the camera could not be opened
                     */
                }
            });
            isInitialized = true;
        }
    }

    public static double get(int channel)   // Channel 0: Camera read and get error;  1: Return most recent X;  2: Return most recent Y;  3: Return most recent Z
    {
        double ballFoundMaxScore = 60.0;
        switch (channel) {
            case 0:
                System.out.println("knytevision.get : sampleImage.templateBestScore = " + ImageClass.committedTemplateBestScore);
                capturedBestX = ImageClass.committedTemplateBestX;
                capturedBestY = ImageClass.committedTemplateBestY;
                capturedBestIndex = ImageClass.committedTemplateBestIndex;
                if (ImageClass.committedTemplateBestScore > ballFoundMaxScore) {
                    return (0);
                } else {
                    return (1);
                }
            case 1:
                return (capturedBestX);
            case 2:
                return (capturedBestY);
            case 3:
                return (capturedBestIndex);
        }
        return (-1);
    }

    public static void set(int channel, double value) {
    }


}

class ImagePipeline extends OpenCvPipeline {
    boolean viewportPaused = false;

    /*
     * NOTE: if you wish to use additional Mat objects in your processing pipeline, it is
     * highly recommended to declare them here as instance variables and re-use them for
     * each invocation of processFrame(), rather than declaring them as new local variables
     * each time through processFrame(). This removes the danger of causing a memory leak
     * by forgetting to call mat.release(), and it also reduces memory pressure by not
     * constantly allocating and freeing large chunks of memory.
     */

    @Override
    public Mat processFrame(Mat input) {
        ImageClass.getNextImage(input);
        ImageClass.createSearchableImage();
        ImageClass.findTemplateInBMP();
        return input;
    }

    @Override
    public void onViewportTapped() {
        /*
         * The viewport (if one was specified in the constructor) can also be dynamically "paused"
         * and "resumed". The primary use case of this is to reduce CPU, memory, and power load
         * when you need your vision pipeline running, but do not require a live preview on the
         * robot controller screen. For instance, this could be useful if you wish to see the live
         * camera preview as you are initializing your robot, but you no longer require the live
         * preview after you have finished your initialization process; pausing the viewport does
         * not stop running your pipeline.
         *
         * Here we demonstrate dynamically pausing/resuming the viewport when the user taps it
         */

        viewportPaused = !viewportPaused;

        if (viewportPaused) {
            KnyteVisionDriver.webcam.pauseViewport();
        } else {
            KnyteVisionDriver.webcam.resumeViewport();
        }
    }
}