package org.megaknytes.ftc.decisiontable.core.utilities.KnyteVision;

import static java.lang.Math.abs;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.FileOutputStream;
import java.io.IOException;

public class ImageClass {
    public static int width = 0, height = 0, searchWidth, searchHeight, channels, searchChannels, frameread = 0, templateBestX, templateBestY, templateBestIndex, edgeThreshold = 0, condenseRatio = 1;
    public static double templateBestScore = -1.0;
    public static double committedTemplateBestScore;
    public static int committedTemplateBestX, committedTemplateBestY, committedTemplateBestIndex;
    public static int[][][] pixels, searchPixels;
    public static byte[] bytearr, temppixels;
    public static int numTemplates;
    public static TemplateClass[] templates;
    public static VideoCapture capture;
    public static Mat frame;
    public static int timer = 0;
    public static int fileId = 0;

    public ImageClass(int numTemplates, String templateFilename, double templateScale, int condenseRatio, HardwareMap hwMap) {
        int i;
        double scale = 1.0;
        String outTemplateFilename;

        //System.out.println("=====> ImageClass() numTemplates =" + numTemplates);

        ImageClass.numTemplates = numTemplates;
        edgeThreshold = edgeThreshold;
        ImageClass.condenseRatio = condenseRatio;
        templates = new TemplateClass[numTemplates];
        templates[0] = new TemplateClass();
        templates[0].readTemplate(templateFilename, hwMap);

        for (i = 1; i < numTemplates; i++) {
            //System.out.println("=====> ImageClass() i =" + i);
            scale = scale - templateScale;
            templates[i] = new TemplateClass();
            templates[i].scaleTemplateFromSourceTemplate(templates[0], scale);
        }
        //System.out.println("=====> ImageClass() finished alloc templates");

        for (i = 0; i < numTemplates; i++) {
            for (int y = 0; y < templates[i].templateHeight; y++) {
                for (int x = 0; x < templates[i].templateWidth; x++) {
                    if ((templates[i].pixels[y][x][0] + templates[i].pixels[y][x][1] + templates[i].pixels[y][x][2]) > 0) {
                        templates[i].pixelsused[y][x] = 1;
                    } else {
                        templates[i].pixelsused[y][x] = 0;
                    }
                }
            }
        }
    }

    public static void openCamera() {
        capture = new VideoCapture(2);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 160);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 120);
    }

    public static int getNextImage(Mat frame) {
        int totalpixelcount, targetpixel;

        totalpixelcount = (int) frame.total();
        channels = frame.channels();
        //System.out.println("======> getNextImage: channels = " + channels);
        width = frame.width();
        height = frame.height();
        temppixels = new byte[totalpixelcount * channels];
        frame.get(0, 0, temppixels);
        pixels = new int[height][width][channels];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int c = 0; c < channels; c++) {
                    targetpixel = (height - y - 1) * width * channels + x * channels + c;
                    pixels[y][x][c] = temppixels[targetpixel];
                    if (pixels[y][x][c] < 0) {
                        pixels[y][x][c] += 256;
                    }
                }
            }
        }
        frameread = 1;
        //frame.release();
        return (0);
    }


    public static int wordToInt(byte byte1, byte byte2) {
        int intermediate, retval;

        intermediate = (int) byte2;
        if (intermediate < 0) {
            intermediate = 256 + intermediate;
        }
        retval = intermediate * 256;
        intermediate = (int) byte1;
        if (intermediate < 0) {
            intermediate = 256 + intermediate;
        }
        retval += intermediate;
        return (retval);
    }


    public static void createSearchableImage() {
        int c;
        int x;
        int y;
        int[][] edgegrid;
        int[] delta;

        searchPixels = new int[height][width][channels];
        edgegrid = new int[height][width];
        delta = new int[4];

        searchChannels = 3;   //channels;
        //System.out.println("======> createSearchableImage: searchChannels = " + searchChannels);
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                edgegrid[y][x] = -1;
            }
        }
        if (edgeThreshold > 0) {
            searchChannels = 1;
            for (y = 1; y < (height - 1); y++) {
                for (x = 1; x < (width - 1); x++) {
                    delta[0] = abs(pixels[y - 1][x - 1][0] - pixels[y + 1][x + 1][0]) + abs(pixels[y - 1][x - 1][1] - pixels[y + 1][x + 1][1]) + abs(pixels[y - 1][x - 1][2] - pixels[y + 1][x + 1][2]);
                    delta[1] = abs(pixels[y + 1][x - 1][0] - pixels[y - 1][x + 1][0]) + abs(pixels[y + 1][x - 1][1] - pixels[y - 1][x + 1][1]) + abs(pixels[y + 1][x - 1][2] - pixels[y - 1][x + 1][2]);
                    delta[2] = abs(pixels[y - 1][x][0] - pixels[y + 1][x][0]) + abs(pixels[y - 1][x][1] - pixels[y + 1][x][1]) + abs(pixels[y - 1][x][2] - pixels[y + 1][x][2]);
                    delta[3] = abs(pixels[y][x - 1][0] - pixels[y][x + 1][0]) + abs(pixels[y][x - 1][1] - pixels[y][x + 1][1]) + abs(pixels[y][x - 1][2] - pixels[y][x + 1][2]);

                    if ((delta[0] >= edgeThreshold) || (delta[1] >= edgeThreshold) || (delta[2] >= edgeThreshold) || (delta[3] >= edgeThreshold)) {
                        edgegrid[y][x] = 1;
                    } else {
                        edgegrid[y][x] = 0;
                    }
                }
            }
        }
        for (y = 0; y < height / condenseRatio; y++) {
            for (x = 0; x < width / condenseRatio; x++) {
                int sum0 = 0, sum1 = 0, sum2 = 0, edgefound = 0;
                for (int cx = 0; cx < condenseRatio; cx++) {
                    for (int cy = 0; cy < condenseRatio; cy++) {
                        sum0 += pixels[y * condenseRatio + cy][x * condenseRatio + cx][0];
                        sum1 += pixels[y * condenseRatio + cy][x * condenseRatio + cx][1];
                        sum2 += pixels[y * condenseRatio + cy][x * condenseRatio + cx][2];
                        if (edgegrid[y][x] == 1) {
                            edgefound = 1;
                        }
                    }
                }
                if (edgefound == 1) {
                    searchPixels[y][x][0] = 255;
                    searchPixels[y][x][1] = 255;
                    searchPixels[y][x][2] = 255;
                } else {
                    searchPixels[y][x][0] = sum0 / (condenseRatio * condenseRatio);
                    searchPixels[y][x][1] = sum1 / (condenseRatio * condenseRatio);
                    searchPixels[y][x][2] = sum2 / (condenseRatio * condenseRatio);
                }
            }
        }
        searchWidth = width / condenseRatio;
        searchHeight = height / condenseRatio;
    }

    public static void writeBMPImage(String filename) {
        int dst, h, w;
        byte[] outputArr = new byte[55 + width * height * 4];

        for (dst = 0; dst < 54; dst++) {
            outputArr[dst] = 0;
        }
        outputArr[0] = (byte) 66;
        outputArr[1] = (byte) 77;
        outputArr[2] = (byte) 54;
        outputArr[3] = (byte) 64;
        outputArr[4] = (byte) 56;
        outputArr[10] = (byte) 54;
        outputArr[14] = (byte) 40;
        outputArr[26] = (byte) 1;
        outputArr[28] = (byte) 32;
        outputArr[35] = (byte) 64;
        outputArr[36] = (byte) 56;

        outputArr[19] = (byte) ((int) width / 256);
        outputArr[18] = (byte) ((int) width % 256);
        outputArr[23] = (byte) ((int) height / 256);
        outputArr[22] = (byte) ((int) height % 256);

        for (dst = 54, h = 0; h < height; h++) {
            for (w = 0; w < width; w++) {
                outputArr[dst] = (byte) pixels[h][w][0];
                outputArr[dst + 1] = (byte) pixels[h][w][1];
                outputArr[dst + 2] = (byte) pixels[h][w][2];
                outputArr[dst + 3] = (byte) 255;
                dst += 4;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(filename, false)) {
            fos.write(outputArr);
            //System.out.println("======> " + filename);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    public static void findTemplateInBMP() {
        int templateNum, BMPx, BMPy, xDelta, yDelta, score, bestScore = 0, bestTemplate = -1, bestBMPx = 999, bestBMPy = 999;
        double templateScore;
        int t, y, x, minX, maxX, minY, maxY;

        templateBestScore = -1.0;

        for (templateNum = numTemplates - 1, minX = 0, maxX = searchWidth - templates[numTemplates - 1].templateWidth, minY = 0, maxY = searchHeight - templates[numTemplates - 1].templateHeight; templateNum >= 0; ) {
            bestScore = templates[templateNum].templateHeight * templates[templateNum].templateWidth * 255 * searchChannels;
            for (BMPy = minY; BMPy <= maxY; BMPy++) {
                for (BMPx = minX; BMPx <= maxX; BMPx++) {
                    score = 0;
                    for (yDelta = 0; (yDelta < templates[templateNum].templateHeight); yDelta++) { //&& (score < bestScore)
                        for (xDelta = 0; xDelta < templates[templateNum].templateWidth; xDelta++) {
                            if (templates[templateNum].pixelsused[yDelta][xDelta] == 1) {
                                for (int c = 0; c < searchChannels; c++) {
                                    score = score + abs(searchPixels[BMPy + yDelta][BMPx + xDelta][c] - templates[templateNum].pixels[yDelta][xDelta][c]);
                                }
                            }
                        }
                    }
                    if (score < bestScore) {
                        bestScore = score;
                        bestTemplate = templateNum;
                        bestBMPx = BMPx;
                        bestBMPy = BMPy;
                    }
                }
            }
            templateScore = ((double) bestScore) / ((double) (templates[templateNum].usedPixelCount * searchChannels));

            if ((templateScore < templateBestScore) || (templateBestScore == -1.0)) {
                templateBestScore = templateScore;
                templateBestX = bestBMPx;
                templateBestY = bestBMPy;
                templateBestIndex = templateNum;
            }
            templateNum--;

            if (templateNum >= 0) {
                minX = Math.max(0, templateBestX - (templates[templateNum].templateWidth - templates[templateBestIndex].templateWidth));
                maxX = Math.min(searchWidth - templates[templateNum].templateWidth, templateBestX + (templates[templateNum].templateWidth - templates[templateBestIndex].templateWidth));

                minY = Math.max(0, templateBestY - (templates[templateNum].templateWidth - templates[templateBestIndex].templateWidth));
                maxY = Math.min(searchHeight - templates[templateNum].templateHeight, templateBestY + (templates[templateNum].templateHeight - templates[templateBestIndex].templateHeight));
            }
        }
        System.out.println("======> templateBestScore = " + templateBestScore + " " + templateBestX + " " + templateBestY + " " + templateBestIndex);

        String filename = String.format("%s/FIRST/data/", Environment.getExternalStorageDirectory().getAbsolutePath()) +
                fileId + "-" + (int) templateBestScore + "-" + templateBestX + "-" + templateBestY + "-" + templateBestIndex + ".bmp";
        //writeBMPImage(filename);

        committedTemplateBestScore = templateBestScore;
        committedTemplateBestX = templateBestX;
        committedTemplateBestY = templateBestY;
        committedTemplateBestIndex = templateBestIndex;
    }

}