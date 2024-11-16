package ooo.sansk.sansbot.module.fontimage;

import ooo.sansk.vaccine.annotation.Component;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class TextToImageConverter {
    private final FontLoader fontLoader;

    public TextToImageConverter(FontLoader fontLoader) {
        this.fontLoader = fontLoader;
    }

    public TextToImageConversionResult convertText(String font, String text) {
        Optional<ImageFont> imageFont = fontLoader.getFont(font);
        if (imageFont.isEmpty()) {
            return new TextToImageConversionResult(false, null);
        }

        ImageCharacter[] characters = getImageCharactersFromText(imageFont.get(), text);
        BufferedImage image = createTextImage(imageFont.get(), characters);
        return new TextToImageConversionResult(true, image);
    }

    private ImageCharacter[] getImageCharactersFromText(ImageFont imageFont, String text) {
        ImageCharacter[] characters = new ImageCharacter[text.length()];
        char[] charArray = text.toUpperCase().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            characters[i] = imageFont.getImageCharacter(LatinAlphabetCharacter.fromCharacter(charArray[i]));
        }
        return characters;
    }

    private BufferedImage createTextImage(ImageFont imageFont, ImageCharacter[] characters) {
        int height = imageFont.getMaxCharacterHeight();
        int width = Stream.of(characters).mapToInt(ImageCharacter::getWidth).sum();

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        drawCharactersOnImage(output, characters);
        return output;
    }

    private void drawCharactersOnImage(BufferedImage image, ImageCharacter[] characters) {
        int horizontalStartPixel = 0;
        for (ImageCharacter character : characters) {
            for (int x = 0; x < character.getWidth(); x++) {
                for (int y = 0; y < character.getHeight(); y++) {
                    if (character.getImageData() != null) {
                        int rgb = character.getImageData().getRGB(x, y);
                        image.setRGB(x + horizontalStartPixel, y, rgb);
                    }
                }
            }
            horizontalStartPixel += character.getWidth();
        }
    }
}
