package ooo.sansk.sansbot.module.fontimage;

import java.util.Map;

public class ImageFont {
    private final Map<LatinAlphabetCharacter, ImageCharacter> imageCharacters;
    private final int maxCharacterHeight;
    private final int maxCharacterWidth;

    public ImageFont(Map<LatinAlphabetCharacter, ImageCharacter> imageCharacters) {
        this.imageCharacters = imageCharacters;
        this.maxCharacterHeight = imageCharacters.values().stream().mapToInt(ImageCharacter::getHeight).max().orElse(0);
        this.maxCharacterWidth = imageCharacters.values().stream().mapToInt(ImageCharacter::getWidth).max().orElse(0);
    }

    public ImageCharacter getImageCharacter(LatinAlphabetCharacter latinAlphabetCharacter) {
        ImageCharacter imageCharacter = imageCharacters.get(latinAlphabetCharacter);
        if(imageCharacter == null) {
            imageCharacter = new ImageCharacter(LatinAlphabetCharacter.WHITESPACE, maxCharacterWidth, maxCharacterHeight);
        }
        return imageCharacter;
    }

    public int getMaxCharacterHeight() {
        return maxCharacterHeight;
    }

    public int getMaxCharacterWidth() {
        return maxCharacterWidth;
    }
}
