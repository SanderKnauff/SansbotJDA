package ooo.sansk.sansbot.module.pokedex;

import java.util.Optional;

public record Pokemon(int id,
                      String name,
                      PokemonType primaryType,
                      PokemonType secondaryType,
                      String genus,
                      String description,
                      String spriteUrl) {
    public Optional<PokemonType> getSecondaryType() {
        return Optional.ofNullable(secondaryType);
    }
}
