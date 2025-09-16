package org.megaknytes.ftc.decisiontable.core.utilities.KnyteVision;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TemplateClass {
    int templateWidth = 0, templateHeight = 0, usedPixelCount = 0;

    int[][][] pixels;
    int[][] pixelsused;

    public void readTemplate(String filename, HardwareMap hwMap) {
        int h, w, c;

        BufferedReader reader = null;
        //Initialize the scanner
        Scanner scan = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(hwMap.appContext.getAssets().open(filename), StandardCharsets.UTF_8));
            scan = new Scanner(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.templateHeight = scan.nextInt();
        this.templateWidth = scan.nextInt();
        this.pixels = new int[this.templateHeight][this.templateWidth][3];
        this.pixelsused = new int[this.templateHeight][this.templateWidth];
        for (h = 0; h < templateHeight; h++) {
            for (w = 0; w < templateWidth; w++) {
                this.pixels[h][w][0] = scan.nextInt();
                this.pixels[h][w][1] = scan.nextInt();
                this.pixels[h][w][2] = scan.nextInt();
                if ((this.pixels[h][w][0] > 0) || (this.pixels[h][w][1] > 0) || (this.pixels[h][w][2] > 0)) {
                    this.usedPixelCount++;
                }
            }
        }
    }

    public void setThresholdEdges(int edgeThreshold) {
        int x;
        int y;
        int neighbors;
        int[][] edgegrid;

        edgegrid = new int[templateHeight][templateWidth];

        for (y = 0; y < templateHeight; y++) {
            for (x = 0; x < templateWidth; x++) {
                edgegrid[y][x] = 0;
            }
        }

        for (y = 1; y < (templateHeight - 1); y++) {
            for (x = 1; x < (templateWidth - 1); x++) {
                neighbors = (pixels[y - 1][x - 1][0] + pixels[y - 1][x][0] + pixels[y - 1][x + 1][0] + pixels[y][x - 1][0] + pixels[y][x + 1][0] + pixels[y + 1][x - 1][0] + pixels[y + 1][x][0] + pixels[y + 1][x + 1][0]);
                neighbors += (pixels[y - 1][x - 1][1] + pixels[y - 1][x][1] + pixels[y - 1][x + 1][1] + pixels[y][x - 1][1] + pixels[y][x + 1][1] + pixels[y + 1][x - 1][1] + pixels[y + 1][x][1] + pixels[y + 1][x + 1][1]);
                neighbors += (pixels[y - 1][x - 1][2] + pixels[y - 1][x][2] + pixels[y - 1][x + 1][2] + pixels[y][x - 1][2] + pixels[y][x + 1][2] + pixels[y + 1][x - 1][2] + pixels[y + 1][x][2] + pixels[y + 1][x + 1][2]);

                if (((pixels[y][x][0] + pixels[y][x][1] + pixels[y][x][2]) == 0) && (neighbors > 0)) {
                    edgegrid[y][x] = 1;
                }
            }
        }
        for (y = 0; y < templateHeight; y++) {
            for (x = 0; x < templateWidth; x++) {
                if (edgegrid[y][x] == 1) {
                    pixels[y][x][0] = 255;
                    pixels[y][x][1] = 255;
                    pixels[y][x][2] = 255;
                } else {
                    pixels[y][x][0] = 0;
                    pixels[y][x][1] = 0;
                    pixels[y][x][2] = 0;
                }
            }
        }
    }

    public void scaleTemplateFromSourceTemplate(TemplateClass sourceTemplate, double scale) {
        int x, y, destX, destY, i;
        double xRatio;
        double yRatio;
        double part1;
        double part2;
        double[] xDestPercents;
        double[] yDestPercents;
        double[][][] tempPixels;
        double[][][] tempPixelWeights;

        this.templateHeight = (int) (((double) sourceTemplate.templateHeight * scale) + 0.5);
        this.templateWidth = (int) (((double) sourceTemplate.templateWidth * scale) + 0.5);
        this.pixels = new int[this.templateHeight][this.templateWidth][3];
        this.pixelsused = new int[this.templateHeight][this.templateWidth];
        tempPixels = new double[this.templateHeight + 1][this.templateWidth + 1][3];
        tempPixelWeights = new double[this.templateHeight + 1][this.templateWidth + 1][3];
        yRatio = ((double) this.templateHeight) / ((double) sourceTemplate.templateHeight);
        xRatio = ((double) this.templateWidth) / ((double) sourceTemplate.templateWidth);
        yDestPercents = new double[sourceTemplate.templateHeight + 1];
        xDestPercents = new double[sourceTemplate.templateWidth + 1];

        yDestPercents[sourceTemplate.templateHeight] = 0.0;   // saves the if statement when testing for end of array
        xDestPercents[sourceTemplate.templateWidth] = 0.0;

        for (y = 0; y < this.templateHeight; y++) {
            for (x = 0; x < this.templateWidth; x++) {
                tempPixels[y][x][0] = 0;
                tempPixels[y][x][1] = 0;
                tempPixels[y][x][2] = 0;
                tempPixelWeights[y][x][0] = 0;
                tempPixelWeights[y][x][1] = 0;
                tempPixelWeights[y][x][2] = 0;
            }
        }

        for (y = 0; y <= this.templateHeight; y++) {
            if ((int) ((double) y * yRatio) == (int) ((double) (y + 1) * yRatio)) {
                yDestPercents[y] = 1.0;
            } else {
                part1 = (int) ((double) (y + 1) * yRatio);
                part2 = ((double) y * yRatio);
                yDestPercents[y] = (part1 - part2) / yRatio;
            }
        }

        for (x = 0; x <= this.templateWidth; x++) {
            if ((int) ((double) x * xRatio) == (int) ((double) (x + 1) * xRatio)) {
                xDestPercents[x] = 1.0;
            } else {
                part1 = (int) ((double) (x + 1) * xRatio);
                part2 = ((double) x * xRatio);
                xDestPercents[x] = (part1 - part2) / xRatio;
            }
        }

        for (y = 0; y < sourceTemplate.templateHeight; y++) {
            destY = (int) (y * yRatio);

            for (x = 0; x < sourceTemplate.templateWidth; x++) {
                destX = (int) (x * xRatio);

                if ((sourceTemplate.pixels[y][x][0]) > 0) {
                    for (i = 0; i < 3; i++) {
                        tempPixels[destY][destX][i] += (((double) sourceTemplate.pixels[y][x][i]) * yDestPercents[y] * xDestPercents[x]);
                        tempPixels[destY + 1][destX][i] += (((double) sourceTemplate.pixels[y][x][i]) * (1.0 - yDestPercents[y]) * xDestPercents[x]);
                        tempPixels[destY][destX + 1][i] += (((double) sourceTemplate.pixels[y][x][i]) * yDestPercents[y] * (1.0 - xDestPercents[x]));
                        tempPixels[destY + 1][destX + 1][i] += (((double) sourceTemplate.pixels[y][x][i]) * (1.0 - yDestPercents[y]) * (1.0 - xDestPercents[x]));

                        tempPixelWeights[destY][destX][i] += (yDestPercents[y] * xDestPercents[x]);
                        tempPixelWeights[destY + 1][destX][i] += ((1.0 - yDestPercents[y]) * xDestPercents[x]);
                        tempPixelWeights[destY][destX + 1][i] += (yDestPercents[y] * (1.0 - xDestPercents[x]));
                        tempPixelWeights[destY + 1][destX + 1][i] += ((1.0 - yDestPercents[y]) * (1.0 - xDestPercents[x]));
                    }
                }
            }
        }

        for (destY = 0; destY < this.templateHeight; destY++) {
            for (destX = 0; destX < this.templateWidth; destX++) {
                for (i = 0; i < 3; i++) {
                    pixels[destY][destX][i] = (int) (0.5 + (((double) tempPixels[destY][destX][i]) / tempPixelWeights[destY][destX][i]));
                }
                if ((this.pixels[destY][destX][0] > 0) || (this.pixels[destY][destX][1] > 0) || (this.pixels[destY][destX][2] > 0)) {
                    this.usedPixelCount++;
                }
            }
        }
    }
}