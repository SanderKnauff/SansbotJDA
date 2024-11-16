package ooo.sansk.sansbot.module.image.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public enum FilterType {
    INVERT(new InvertColorImageFilter(), Arrays.asList("inverted", "invert", "i")),
    BW(new BlackWhiteColorImageFilter(), Arrays.asList("blackwhite", "bw")),
    GAUSSIAN_BLUR(new GaussianImageFilter(), Arrays.asList("blur", "gauss")),
    DEEP_FRY(new DeepFryImageFilter(), Arrays.asList("df", "fry"));

    private final ImageFilter filter;
    private final List<String> aliases;

    FilterType(ImageFilter filter, List<String> aliases) {
        this.filter = filter;
        this.aliases = aliases;
    }

    public ImageFilter getFilter() {
        return filter;
    }

    public boolean hasAlias(String alias) {
        return aliases.stream().anyMatch(alias::equalsIgnoreCase);
    }

    public static Optional<FilterType> getFilter(String name) {
        return Stream.of(FilterType.values())
                .filter(filterType -> filterType.hasAlias(name))
                .findFirst();
    }

}
