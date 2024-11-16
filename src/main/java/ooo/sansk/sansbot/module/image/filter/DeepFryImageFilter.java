package ooo.sansk.sansbot.module.image.filter;

import ooo.sansk.sansbot.module.image.ImageResult;
import ooo.sansk.sansbot.module.image.ImageResultReason;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DeepFryImageFilter implements ImageFilter {
    private static final String RESULT_TYPE = "jpg";

    @Override
    public ImageResult doFilter(BufferedImage originalImage) {
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        for (int x = 0; x < resultImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                Color originalColor = new Color(originalImage.getRGB(x, y));

                if(x != 0 && y != 0) {
                    int sharpR = 0;
                    int sharpG = 0;
                    int sharpB = 0;
                    for(int i = -1; i < 1; i++) {
                        for (int j = -1; j < 1; j++) {
                            if(i == 0 && j == 0) {
                                sharpR += originalColor.getRed() * 3;
                                sharpG += originalColor.getGreen() * 3;
                                sharpB += originalColor.getBlue() * 3;
                            } else {
                                Color edgeColor = new Color(originalImage.getRGB(x + i, y + j));
                                sharpR += edgeColor.getRed() * -1;
                                sharpG += edgeColor.getGreen() * -1;
                                sharpB += edgeColor.getBlue() * -1;
                            }
                        }
                    }
                    sharpR = Math.min(Math.max(0, sharpR), 255);
                    sharpG = Math.min(Math.max(0, sharpG), 255);
                    sharpB = Math.min(Math.max(0, sharpB), 255);
                    originalColor = new Color(sharpR, sharpG, sharpB, originalColor.getAlpha());
                }
                resultImage.setRGB(x, y, originalColor.getRGB());
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
}
