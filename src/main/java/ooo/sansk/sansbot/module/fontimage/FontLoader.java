package ooo.sansk.sansbot.module.fontimage;

import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class FontLoader {
    private static final Logger logger = LoggerFactory.getLogger(FontLoader.class);

    private Map<String, ImageFont> fonts;

    @AfterCreate
    public void postConstruct() {
        fonts = loadFonts();
    }

    private Map<String, ImageFont> loadFonts() {
        Map<String, ImageFont> loadedFonts;
        File fontFolder = new File("img-fonts");
        loadedFonts = loadFontsFromResourceFolder(fontFolder);
        return loadedFonts;
    }

    private Map<String, ImageFont> loadFontsFromResourceFolder(File fontFolder) {
        if (!fontFolder.isDirectory()) {
            logger.info("Failed loading fonts. {} is not a directory", fontFolder);
            return Collections.emptyMap();
        }
        File[] files = fontFolder.listFiles(new FontDirectoryFileFilter());
        if (files == null) {
            return Collections.emptyMap();
        }
        Map<String, ImageFont> loadedFonts = new HashMap<>();
        for (File file : files) {
            ImageFont imageFont = loadImageFontFromFolder(file);
            if (imageFont != null) {
                loadedFonts.put(file.getName(), imageFont);
            }
        }
        return loadedFonts;
    }

    private ImageFont loadImageFontFromFolder(File folder) {
        File[] files = folder.listFiles((file, fileName) -> fileName.matches("[A-Z]\\.png"));
        if (files == null) {
            return null;
        }
        Map<LatinAlphabetCharacter, ImageCharacter> characters = new EnumMap<>(LatinAlphabetCharacter.class);
        for (File imageFile : files) {
            LatinAlphabetCharacter key = LatinAlphabetCharacter.fromCharacter(imageFile.getName().charAt(0));
            ImageCharacter imageCharacter = getImageCharacter(imageFile, key);
            if (imageCharacter != null) {
                characters.put(key, imageCharacter);
            }
        }
        return new ImageFont(characters);
    }

    private ImageCharacter getImageCharacter(File imageFile, LatinAlphabetCharacter key) {
        try {
            return new ImageCharacter(key, ImageIO.read(imageFile));
        } catch (IOException e) {
            logger.info("Failed loading character image from {}. ({}: {})", imageFile, e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    public Optional<ImageFont> getFont(String fontName) {
        return Optional.ofNullable(fonts.get(fontName));
    }

}
