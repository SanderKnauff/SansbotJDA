package ooo.sansk.sansbot.module.pokedex;

import nl.imine.vaccine.annotation.Component;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class PokedexAPI {

    public static final String POKEDEX_ICON = "https://cdn0.iconfinder.com/data/icons/geek-2/24/Pokedex_video_game-128.png";
    public static final String BASE_URL = "https://pokeapi.co/";
    private static final String API_URL = BASE_URL + "api/v2/";
    private static final String POKEMON_ENDPOINT = API_URL + "pokemon/";
    private static final String SPECIES_ENDPOINT = API_URL + "pokemon-species/";

    private static final Random random = new Random();

    public Optional<Pokemon> getPokemon(String pokemonId) {
        try {
            var url = new URL(POKEMON_ENDPOINT + pokemonId);
            var jsonObject = new JSONObject(readStringFromURL(url));
            PokemonType primaryType = null;
            PokemonType secondaryType = null;
            for (Object typeObject : jsonObject.getJSONArray("types")) {
                JSONObject typeJson = ((JSONObject) typeObject);
                var slot = typeJson.getInt("slot");
                if (slot == 1) {
                    primaryType = PokemonType.valueOf(typeJson.getJSONObject("type").getString("name").toUpperCase());
                } else {
                    secondaryType = PokemonType.valueOf(typeJson.getJSONObject("type").getString("name").toUpperCase());
                }
            }
            PokemonSpecies pokemonDexDescription = getPokemonDexDescription(pokemonId);
            return Optional.of(new Pokemon(jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    primaryType,
                    secondaryType,
                    pokemonDexDescription.getGenus(),
                    pokemonDexDescription.getDescription(),
                    jsonObject.getJSONObject("sprites").getString("front_default")));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private PokemonSpecies getPokemonDexDescription(String pokemonId) throws IOException {
        var url = new URL(SPECIES_ENDPOINT + pokemonId);
        var jsonObject = new JSONObject(readStringFromURL(url));

        return new PokemonSpecies(getGenus(jsonObject), getRandomFlavorText(jsonObject));
    }

    private String getRandomFlavorText(JSONObject jsonObject) {
        List<PokemonFlavorText> flavorTextList = new ArrayList<>();
        var flavorTextJsonArray = jsonObject.getJSONArray("flavor_text_entries");
        for (var i = 0; i < flavorTextJsonArray.length(); i++) {
            var flavorTextObject = flavorTextJsonArray.getJSONObject(i);
            if(flavorTextObject.getJSONObject("language").getString("name").equals("en")) {
                flavorTextList.add(new PokemonFlavorText(flavorTextObject.getJSONObject("version").getString("name"),
                        flavorTextObject.getJSONObject("language").getString("name"),
                        flavorTextObject.getString("flavor_text")
                ));
            }
        }
        return flavorTextList.get(random.nextInt(flavorTextList.size())).text();
    }

    private String getGenus(JSONObject jsonObject) {
        var genusArray = jsonObject.getJSONArray("genera");
        for (var i = 0; i < genusArray.length(); i++) {
            var genusObject = genusArray.getJSONObject(i);
            if(genusObject.getJSONObject("language").getString("name").equals("en")) {
                return genusObject.getString("genus");
            }
        }
        return null;
    }

    private String readStringFromURL(URL url) throws IOException {
        try (var scanner = new Scanner(url.openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
