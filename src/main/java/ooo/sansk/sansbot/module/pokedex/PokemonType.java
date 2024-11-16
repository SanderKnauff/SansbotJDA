package ooo.sansk.sansbot.module.pokedex;

import java.awt.*;

public enum PokemonType {
    NORMAL(hex2Rgb("A8A77A")),
    FIRE(hex2Rgb("EE8130")),
    WATER(hex2Rgb("6390F0")),
    ELECTRIC(hex2Rgb("F7D02C")),
    GRASS(hex2Rgb("7AC74C")),
    ICE(hex2Rgb("96D9D6")),
    FIGHTING(hex2Rgb("C22E28")),
    POISON(hex2Rgb("A33EA1")),
    GROUND(hex2Rgb("E2BF65")),
    FLYING(hex2Rgb("A98FF3")),
    PSYCHIC(hex2Rgb("F95587")),
    BUG(hex2Rgb("A6B91A")),
    ROCK(hex2Rgb("B6A136")),
    GHOST(hex2Rgb("735797")),
    DRAGON(hex2Rgb("6F35FC")),
    DARK(hex2Rgb("705746")),
    STEEL(hex2Rgb("B7B7CE")),
    FAIRY(hex2Rgb("D685AD"));

    private final Color color;

    PokemonType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16));
    }
}
