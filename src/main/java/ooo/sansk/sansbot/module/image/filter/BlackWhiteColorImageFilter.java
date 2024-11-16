package ooo.sansk.sansbot.module.image.filter;

import ooo.sansk.sansbot.module.image.ImageResult;
import ooo.sansk.sansbot.module.image.ImageResultReason;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BlackWhiteColorImageFilter implements ImageFilter {
    private static final String RESULT_TYPE = "png";

    @Override
    public ImageResult doFilter(BufferedImage originalImage) {
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        for (int x = 0; x < resultImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                resultImage.setRGB(x, y, averageColorsInt(originalImage.getRGB(x, y)));
            }
        }
        return getResult(resultImage);
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

    private int averageColorsInt(int rgb) {
        Color oldColor = new Color(rgb, true);
        int colorAverage = (oldColor.getRed() + oldColor.getGreen() + oldColor.getBlue()) / 3;
        return new Color(colorAverage, colorAverage, colorAverage, oldColor.getAlpha()).getRGB();
    }
}
