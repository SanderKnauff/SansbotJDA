package ooo.sansk.sansbot.module.movielines;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;

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
    public void onEvent(GenericEvent event) {
        if (event instanceof ButtonInteractionEvent buttonClickEvent) {
            movieGameManager.getGame(buttonClickEvent.getMessageId()).ifPresent(game -> {
                if (game.getReactedUsers().contains(buttonClickEvent.getUser().getId())) {
                    buttonClickEvent.deferReply().queue();
                    return;
                }
                game.getReactedUsers().add(buttonClickEvent.getUser().getId());

                var replacementMessageBuilder = new MessageEditBuilder();
                if (buttonClickEvent.getButton() != null && game.getWinningOption().title().equals(buttonClickEvent.getButton().getId())) {
                    final var text = String.join("\n", game.getLines().stream().map(Line::text).collect(Collectors.toSet()));

                    replacementMessageBuilder.setEmbeds(new EmbedBuilder().setTitle(buttonClickEvent.getMember().getEffectiveName() + " is de beste! :sunglasses:")
                        .setThumbnail(buttonClickEvent.getUser().getAvatarUrl())
                        .addField(game.getWinningOption().title() + ", " + game.getLines().peek().time(), text, false)
                        .build());
                } else {
                    final var text = String.join("\n", game.getLines().stream().map(Line::text).collect(Collectors.toSet()));

                    replacementMessageBuilder.setEmbeds(new EmbedBuilder().setTitle("Ha! " + buttonClickEvent.getMember().getEffectiveName() + " you suck! :-1:")
                        .setThumbnail("https://twemoji.maxcdn.com/v/13.0.1/72x72/274c.png")
                        .addField(game.getWinningOption().title() + ", " + game.getLines().peek().time(), text, false)
                        .build());
                }
                buttonClickEvent.getInteraction().editMessage(replacementMessageBuilder.build()).queue();
                movieGameManager.removeGame(game.getMessageId());
            });
        }
    }
}
