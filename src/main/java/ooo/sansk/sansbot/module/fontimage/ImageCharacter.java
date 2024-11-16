package ooo.sansk.sansbot.module.fontimage;

import java.awt.image.BufferedImage;

public class ImageCharacter {
    private final LatinAlphabetCharacter character;
    private final BufferedImage imageData;
    private final int width;
    private final int height;

    public ImageCharacter(LatinAlphabetCharacter character, BufferedImage imageData) {
        this.character = character;
        this.imageData = imageData;
        this.width = imageData.getWidth();
        this.height = imageData.getHeight();
    }

    public ImageCharacter(LatinAlphabetCharacter character, int width, int height) {
        this.character = character;
        this.imageData = null;
        this.width = width;
        this.height = height;
    }

    public LatinAlphabetCharacter getCharacter() {
        return character;
    }

    public BufferedImage getImageData() {
        return imageData;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
