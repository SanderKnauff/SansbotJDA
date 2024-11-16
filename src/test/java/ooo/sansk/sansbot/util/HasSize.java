package ooo.sansk.sansbot.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;

public class HasSize extends TypeSafeMatcher<Collection<?>> {
    private final int expectedSize;

    public HasSize(int expectedSize) {
        this.expectedSize = expectedSize;
    }

    @Override
    protected boolean matchesSafely(Collection item) {
        return item.size() == expectedSize;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("The collection was not of the expected size. (Expected " + expectedSize + ")");
    }

    public static Matcher<Collection<?>> hasSize(int expectedSize) {
        return new HasSize(expectedSize);
    }
}
