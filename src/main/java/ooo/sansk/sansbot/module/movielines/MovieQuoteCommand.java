package ooo.sansk.sansbot.module.movielines;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieQuoteCommand extends ChatCommand {
    private final MovieGameManager movieGameManager;

    public MovieQuoteCommand(ChatCommandHandler chatCommandHandler, MovieGameManager movieGameManager) {
        super(chatCommandHandler);
        this.movieGameManager = movieGameManager;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("moviequote", "Guess which movie the quotes came from");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        movieGameManager.createGame().ifPresentOrElse(game -> {
            List<Movie> movies = new ArrayList<>();
            movies.add(game.getWinningOption());
            movies.add(game.getFirstAlternativeOption());
            movies.add(game.getSecondAlternativeOption());
            Collections.shuffle(movies);

            final var text = String.join("\n", game.getLines().stream().map(Line::text).collect(Collectors.toSet()));
            event.reply(
                            MessageCreateBuilder.from(
                                    MessageCreateData.fromEmbeds(
                                            new EmbedBuilder()
                                                    .setTitle("Filmquote spelletje ding!")
                                                    .setThumbnail("https://fonts.gstatic.com/s/i/materialicons/movie_filter/v6/24px.svg")
                                                    .addField("Uit welk van deze films komen deze lines?", text, false)
                                                    .build()
                                    )
                            ).addActionRow(
                                    List.of(
                                            Button.secondary(movies.get(0).title(), movies.get(0).title()),
                                            Button.secondary(movies.get(1).title(), movies.get(1).title()),
                                            Button.secondary(movies.get(2).title(), movies.get(2).title())
                                    )
                            ).build()
                    )
                    .submit()
                    .thenCompose(interactionHook -> interactionHook.retrieveOriginal().submit())
                    .thenAccept(message -> game.setMessageId(message.getId()));
        }, () -> event.reply("Dit spelleke werkt op het moment niet. Helaas, duo penotti").submit());
    }
}
