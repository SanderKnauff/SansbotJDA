package ooo.sansk.sansbot.module.pokedex;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PokedexCli {
    public static void main(String[] args) {
        final var objectMapper = new ObjectMapper();
        final var pokedexApi = new PokedexAPI(objectMapper);

        final var pokemon = pokedexApi.getPokemon("25").join();
        System.out.println(pokemon);
    }
}
