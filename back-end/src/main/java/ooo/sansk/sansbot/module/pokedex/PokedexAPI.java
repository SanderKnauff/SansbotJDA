package ooo.sansk.sansbot.module.pokedex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.imine.vaccine.annotation.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component
public class PokedexAPI {

    public static final String POKEDEX_ICON = "https://cdn0.iconfinder.com/data/icons/geek-2/24/Pokedex_video_game-128.png";
    public static final String BASE_URL = "https://pokeapi.co/";
    private static final String API_URL = BASE_URL + "api/v2/";
    private static final String POKEMON_ENDPOINT = API_URL + "pokemon/";
    private static final String SPECIES_ENDPOINT = API_URL + "pokemon-species/";

    private static final Random random = new Random();

    private final ObjectMapper objectMapper;

    public PokedexAPI(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<Pokemon> getPokemon(String pokemonId) {
        CompletableFuture<JsonNode> baseDataFuture = readJsonFromUrl(POKEMON_ENDPOINT + pokemonId);
        CompletableFuture<PokemonSpecies> speciesFuture = getPokedexDescription(pokemonId);
        return CompletableFuture.allOf(baseDataFuture, speciesFuture).thenApply(unused -> {
            var baseData = baseDataFuture.join();
            var species = speciesFuture.join();

            PokemonType primaryType = null;
            PokemonType secondaryType = null;
            for (JsonNode type : baseData.get("types")) {
                var slot = type.get("slot").asInt();
                if (slot == 1) {
                    primaryType = PokemonType.valueOf(type.path("type.name").asText().toUpperCase());
                } else {
                    secondaryType = PokemonType.valueOf(type.path("type.name").asText().toUpperCase());
                }
            }
            return new Pokemon(
                baseData.get("id").asInt(),
                baseData.get("name").asText(),
                primaryType,
                secondaryType,
                species.getGenus(),
                species.getDescription(),
                baseData.path("sprites.front_default").asText());
        });
    }

    private CompletableFuture<PokemonSpecies> getPokedexDescription(String pokemonId) {
        return readJsonFromUrl(SPECIES_ENDPOINT + pokemonId)
            .thenApply(jsonNode -> new PokemonSpecies(getGenus(jsonNode), getRandomFlavorText(jsonNode)));
    }

    private String getRandomFlavorText(JsonNode jsonNode) {
        List<PokemonFlavorText> flavorTextList = new ArrayList<>();
        var flavorTextJsonArray = jsonNode.get("flavor_text_entries");
        for (var i = 0; i < flavorTextJsonArray.size(); i++) {
            var flavorTextObject = flavorTextJsonArray.get(i);
            if (flavorTextObject.path("language.name").asText().equals("en")) {
                flavorTextList.add(new PokemonFlavorText(flavorTextObject.path("version.name").asText(),
                    flavorTextObject.path("language.name").asText(),
                    flavorTextObject.get("flavor_text").asText()
                ));
            }
        }
        return flavorTextList.get(random.nextInt(flavorTextList.size())).text();
    }

    private String getGenus(JsonNode jsonNode) {
        var genusArray = jsonNode.get("genera");
        for (var i = 0; i < genusArray.size(); i++) {
            var genusObject = genusArray.get(i);
            if (genusObject.path("language.name").asText().equals("en")) {
                return genusObject.get("genus").asText();
            }
        }
        return null;
    }

    private CompletableFuture<JsonNode> readJsonFromUrl(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return objectMapper.readTree(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
