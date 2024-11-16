package ooo.sansk.sansbot.module.fontimage;

public enum LatinAlphabetCharacter {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, WHITESPACE;

    public static LatinAlphabetCharacter fromCharacter(char character) {
        if(character < 65 || character > 90) {
            return WHITESPACE;
        }
        return LatinAlphabetCharacter.values()[character - 65];
    }
}
