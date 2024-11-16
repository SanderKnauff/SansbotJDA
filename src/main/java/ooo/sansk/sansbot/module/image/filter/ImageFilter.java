package ooo.sansk.sansbot.module.image.filter;

import ooo.sansk.sansbot.module.image.ImageResult;

import java.awt.image.BufferedImage;

public interface ImageFilter {
    ImageResult doFilter(BufferedImage originalImage);
}
