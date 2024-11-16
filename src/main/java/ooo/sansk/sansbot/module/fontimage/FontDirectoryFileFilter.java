package ooo.sansk.sansbot.module.fontimage;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class FontDirectoryFileFilter implements FileFilter {
    private static final Pattern ACCEPTED_FILE_NAME_PATTERN = Pattern.compile("[\\w\\-]*");

    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory() && isAllowedFileName(pathname.getName());
    }

    private boolean isAllowedFileName(String name) {
        return ACCEPTED_FILE_NAME_PATTERN.matcher(name).matches();
    }
}
