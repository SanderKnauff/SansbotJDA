package ooo.sansk.sansbot.module.pokedex;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PokemonCommand extends ChatCommand {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final PokedexAPI pokedexAPI;

    public PokemonCommand(ChatCommandHandler chatCommandHandler, PokedexAPI pokedexAPI) {
        super(chatCommandHandler);
        this.pokedexAPI = pokedexAPI;
    }

    @Override
    public CommandData getCommandData() {
        final var commandData = new CommandData("pokédex", "Get Pokédex data for a Pokémon");
        commandData.addOption(OptionType.STRING, "pokémon", "The Pokémon to get the dex data for", true);
        return commandData;
    }

    @Override
    public void handle(SlashCommandEvent event) {
        final var pokemonOption = event.getOption("pokémon");
        if (pokemonOption == null) {
            event.reply("Je wat wil je nou, er zijn meer pokémon dan alleen Pikachu hè?!").queue();
            return;
        }

        event.reply("Reading Pokédex...").queue();

        executorService.submit(() -> {
            Optional<Pokemon> oPokemon = pokedexAPI.getPokemon(event.getOptions().get(0).getAsString().toLowerCase());
            if (oPokemon.isPresent()) {
                var pokemon = oPokemon.get();
                var embedBuilder = new EmbedBuilder().setAuthor("Pokédex", PokedexAPI.BASE_URL, PokedexAPI.POKEDEX_ICON)
                    .setTitle(String.format("#%s: %s", pokemon.id(), capitalize(pokemon.name())))
                    .addField("Primary Type", capitalize(pokemon.primaryType().name()), true);
                pokemon.getSecondaryType().ifPresent(type -> embedBuilder.addField("Secondary Type", capitalize(type.name()), true));
                embedBuilder.addField("Description", pokemon.description(), false)
                    .setThumbnail(pokemon.spriteUrl())
                    .setColor(pokemon.primaryType().getColor())
                    .setFooter("Pokédex data retrieved from " + PokedexAPI.BASE_URL, PokedexAPI.POKEDEX_ICON);
                event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
            } else {
                event.getInteraction().getHook().editOriginal(new MessageBuilder("Pokemon not found").build()).queue();
            }
        });
    }

    private String capitalize(String string) {
        return string.toLowerCase().substring(0, 1).toUpperCase() + string.substring(1);
    }

}
