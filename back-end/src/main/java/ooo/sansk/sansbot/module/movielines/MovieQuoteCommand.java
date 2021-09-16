package ooo.sansk.sansbot.module.movielines;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import nl.imine.vaccine.annotation.Component;
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
        return new CommandData("moviequote", "Guess which movie the quotes came from");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        movieGameManager.createGame().ifPresentOrElse(game -> {
            List<Movie> movies = new ArrayList<>();
            movies.add(game.getWinningOption());
            movies.add(game.getFirstAlternativeOption());
            movies.add(game.getSecondAlternativeOption());
            Collections.shuffle(movies);

            final var text = String.join("\n", game.getLines().stream().map(Line::getText).collect(Collectors.toSet()));
            event.reply(
                new MessageBuilder()
                    .setEmbeds(
                        new EmbedBuilder()
                            .setTitle("Filmquote spelletje ding!")
                            .setThumbnail("https://fonts.gstatic.com/s/i/materialicons/movie_filter/v6/24px.svg")
                            .addField("Uit welk van deze films komen deze lines?", text, false)
                            .build()
                    )
                    .setActionRows(ActionRow.of(
                        Button.secondary(movies.get(0).getTitle(), movies.get(0).getTitle()),
                        Button.secondary(movies.get(1).getTitle(), movies.get(1).getTitle()),
                        Button.secondary(movies.get(2).getTitle(), movies.get(2).getTitle()))
                    )
                    .build()
            ).submit()
                .thenCompose(interactionHook -> interactionHook.retrieveOriginal().submit())
                .thenAccept(message -> game.setMessageId(message.getId()));
        }, () -> event.reply("Dit spelleke werkt op het moment niet. Helaas, duo penotti").submit());
    }
}
