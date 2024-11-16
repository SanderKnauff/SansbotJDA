package ooo.sansk.sansbot.module.fontimage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LatinAlphabetCharacterTest {
    @Test
    void testFromCharacter_Valid() {
        assertEquals(LatinAlphabetCharacter.A, LatinAlphabetCharacter.fromCharacter('A'));
        assertEquals(LatinAlphabetCharacter.Z, LatinAlphabetCharacter.fromCharacter('Z'));
    }

    @Test
    void testFromCharacter_IndexTooLow() {
        assertEquals(LatinAlphabetCharacter.WHITESPACE, LatinAlphabetCharacter.fromCharacter((char) ('A' - 1)));
    }

    @Test
    void testFromCharacter_IndexTooHigh() {
        assertEquals(LatinAlphabetCharacter.WHITESPACE, LatinAlphabetCharacter.fromCharacter((char) ('Z' + 1)));
    }
}
