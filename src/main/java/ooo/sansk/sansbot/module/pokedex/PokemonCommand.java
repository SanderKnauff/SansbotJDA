package ooo.sansk.sansbot.module.pokedex;

import jakarta.annotation.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PokemonCommand extends ChatCommand {
    private static final Logger logger = LoggerFactory.getLogger(PokemonCommand.class);

    private final PokedexAPI pokedexAPI;

    public PokemonCommand(ChatCommandHandler chatCommandHandler, PokedexAPI pokedexAPI) {
        super(chatCommandHandler);
        this.pokedexAPI = pokedexAPI;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("pokedex", "Get Pokédex data for a Pokémon")
                .addOption(OptionType.STRING, "pokémon", "The Pokémon to get the dex data for", true);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        @Nullable final var pokemonOption = event.getOption("pokémon");
        if (pokemonOption == null) {
            event.reply("Je wat wil je nou, er zijn meer pokémon dan alleen Pikachu hè?!").queue();
            return;
        }

        event.reply("Reading Pokédex...").queue();

        pokedexAPI.getPokemon(event.getOptions().getFirst().getAsString().toLowerCase()).thenAccept(pokemon -> {
            final var embedBuilder = new EmbedBuilder().setAuthor("Pokédex", PokedexAPI.BASE_URL, PokedexAPI.POKEDEX_ICON)
                    .setTitle(String.format("#%s: %s", pokemon.id(), capitalize(pokemon.name())))
                    .addField("Primary Type", capitalize(pokemon.primaryType().name()), true)
                    .addField("Description", pokemon.description(), false)
                    .setThumbnail(pokemon.spriteUrl())
                    .setColor(pokemon.primaryType().getColor())
                    .setFooter("Pokédex data retrieved from " + PokedexAPI.BASE_URL, PokedexAPI.POKEDEX_ICON);

            pokemon.getSecondaryType().ifPresent(type -> embedBuilder.addField("Secondary Type", capitalize(type.name()), true));

            event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        }).exceptionally(throwable -> {
            logger.warn("Failed getting Pokémon from API", throwable);
            event.getInteraction().getHook().editOriginal(MessageEditData.fromContent("Pokémon not found")).queue();
            return null;
        });
    }

    private String capitalize(String string) {
        return string.toLowerCase().substring(0, 1).toUpperCase() + string.substring(1);
    }

}
