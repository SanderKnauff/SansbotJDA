package ooo.sansk.sansbot.module.image.filter;

import ooo.sansk.sansbot.module.image.ImageResult;
import ooo.sansk.sansbot.module.image.ImageResultReason;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GaussianImageFilter implements ImageFilter {
    private static final String RESULT_TYPE = "png";

    public ImageResult doFilter(BufferedImage originalImage) {
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        for (int i = 0; i < 10; i++) {
            originalImage = doBlur(originalImage);
        }
        return getResult(originalImage);
    }

    public BufferedImage doBlur(BufferedImage originalImage) {
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        double[][] kernel = createKernel(5, 10);
        for (int x = 0; x < resultImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                resultImage.setRGB(x, y, gaussianBlend(originalImage, kernel, x, y));
            }
        }
        return resultImage;
    }

    private int gaussianBlend(BufferedImage originalImage, double[][] kernel, int originX, int originY) {
        int newRed = 0;
        int newGreen = 0;
        int newBlue = 0;
        for (int x = 0; x < kernel.length; x++) {
            double[] kernelRow = kernel[x];
            int xOffset = x - (kernel.length / 2);
            int targetX = originX + xOffset;
            for (int y = 0; y < kernelRow.length; y++) {
                int yOffset = y - (kernel.length / 2);
                int targetY = originY + yOffset;
                if (isInImageBounds(originalImage, originX, originY, xOffset, yOffset)) {
                    newRed += getMultipliedRedChannelValue(originalImage, kernel[x][y], targetX, targetY);
                    newGreen += getMultipliedGreenChannelValue(originalImage, kernel[x][y], targetX, targetY);
                    newBlue += getMultipliedBlueChannelValue(originalImage, kernel[x][y], targetX, targetY);
                }
            }
        }
        return new Color(
                Math.min(newRed / ((kernel.length * kernel.length) + 1), 255),
                Math.min(newGreen / ((kernel.length * kernel.length) + 1), 255),
                Math.min(newBlue / ((kernel.length * kernel.length) + 1), 255),
                new Color(originalImage.getRGB(originX, originY), true).getAlpha()).getRGB();
    }

    private int getMultipliedRedChannelValue(BufferedImage originalImage, double multiplier, int targetX, int targetY) {
        return (int) (new Color(originalImage.getRGB(targetX, targetY), true).getRed() * multiplier);
    }

    private int getMultipliedGreenChannelValue(BufferedImage originalImage, double multiplier, int targetX, int targetY) {
        return (int) (new Color(originalImage.getRGB(targetX, targetY), true).getGreen() * multiplier);
    }

    private int getMultipliedBlueChannelValue(BufferedImage originalImage, double multiplier, int targetX, int targetY) {
        return (int) (new Color(originalImage.getRGB(targetX, targetY), true).getBlue() * multiplier);
    }

    private boolean isInImageBounds(BufferedImage originalImage, int originX, int originY, int xOffset, int yOffset) {
        int targetX = originX + xOffset;
        int targetY = originY + yOffset;
        if (targetX < 0 || targetY < 0) {
            return false;
        }
        return targetX < originalImage.getWidth() && targetY < originalImage.getHeight();
    }

    private double[][] createKernel(int radius, double standardDeviation) {
        double[][] kernel = new double[radius][radius];
        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                kernel[x][y] = gaussianFormula(returnCounting(x, radius), returnCounting(y, radius), standardDeviation);
            }
        }
        return kernel;
    }

    private double gaussianFormula(double horizontalOriginDistance,
                                   double verticalOriginDistance,
                                   double standardDeviation) {
        double baseNumber = calculateBaseNumber(standardDeviation);
        double exponent = calculateExponent(horizontalOriginDistance, verticalOriginDistance, standardDeviation);

        return Math.pow(baseNumber, exponent);
    }

    private double calculateBaseNumber(double standardDeviation) {
        double divisor = 2 * Math.PI * (standardDeviation * standardDeviation);
        return 1 / divisor;
    }

    private double calculateExponent(double horizontalOriginDistance,
                                     double verticalOriginDistance,
                                     double standardDeviation) {
        double divisor = (horizontalOriginDistance * horizontalOriginDistance) + (verticalOriginDistance * verticalOriginDistance);
        double denominator = 2d * (standardDeviation * standardDeviation);
        return -1 * (divisor / denominator);
    }

    private int returnCounting(int count, int max) {
        boolean isOverHalf = (int) Math.round(count / (double) max) > 0;
        if (isOverHalf) {
            return Math.abs(count - (max - 1));
        }
        return count;
    }

    private ImageResult getResult(BufferedImage result) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(result, RESULT_TYPE, byteArrayOutputStream);
            return new ImageResult(ImageResultReason.SUCCESS, RESULT_TYPE, byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            return new ImageResult(ImageResultReason.FAILED_CONVERTING_IMAGE_TO_BYTES, null, null);
        }
    }
}
