package ooo.sansk.sansbot.module.movielines;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.RestAction;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

@Component
public class MovieGameListener implements EventListener {

    private final JDA jda;
    private final MovieGameManager movieGameManager;

    public MovieGameListener(JDA jda, MovieGameManager movieGameManager) {
        this.jda = jda;
        this.movieGameManager = movieGameManager;
    }

    @AfterCreate
    public void postConstruct() {
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ButtonClickEvent buttonClickEvent) {
            movieGameManager.getGame(buttonClickEvent.getMessageId()).ifPresent(game -> {
                if (game.getReactedUsers().contains(buttonClickEvent.getUser().getId())) {
                    buttonClickEvent.deferEdit().queue();
                    return;
                }
                game.getReactedUsers().add(buttonClickEvent.getUser().getId());

                var replacementMessageBuilder = new MessageBuilder();
                if (buttonClickEvent.getButton() != null && game.getWinningOption().getTitle().equals(buttonClickEvent.getButton().getId())) {
                    final var text = String.join("\n", game.getLines().stream().map(Line::getText).collect(Collectors.toSet()));

                    replacementMessageBuilder.setEmbeds(new EmbedBuilder().setTitle(buttonClickEvent.getMember().getEffectiveName() + " is de beste! :sunglasses:")
                        .setThumbnail(buttonClickEvent.getUser().getAvatarUrl())
                        .addField(game.getWinningOption().getTitle() + ", " + game.getLines().peek().getTime(), text, false)
                        .build());
                } else {
                    final var text = String.join("\n", game.getLines().stream().map(Line::getText).collect(Collectors.toSet()));

                    replacementMessageBuilder.setEmbeds(new EmbedBuilder().setTitle("Ha! " + buttonClickEvent.getMember().getEffectiveName() + " you suck! :-1:")
                        .setThumbnail("https://twemoji.maxcdn.com/v/13.0.1/72x72/274c.png")
                        .addField(game.getWinningOption().getTitle() + ", " + game.getLines().peek().getTime(), text, false)
                        .build());
                }
                buttonClickEvent.getInteraction().editMessage(replacementMessageBuilder.build()).queue();
                movieGameManager.removeGame(game.getMessageId());
            });
        }
    }
}
