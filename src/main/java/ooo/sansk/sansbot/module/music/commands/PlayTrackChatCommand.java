package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class PlayTrackChatCommand extends AbstractMusicChatCommand {
    private final TrackListManager trackListManager;

    public PlayTrackChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        final var commandData = Commands.slash("play", "Play a video from Youtube (or other sources supported by Lavaplayer)");
        commandData.addOption(OptionType.STRING, "source", "Source to play from, for example a url to a Youtube video or mp3 file.", true);
        return commandData;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (trackListManager.getCurrentTrack() != null) {
            event.reply(String.format("Sorry, anderen waren eerst en die zijn nog aan het genieten van de muziek. Probeer het anders later nog eens, %s? :upside_down:", event.getMember().getAsMention())).queue();
            return;
        }

        if (!isInSameChannel(event.getMember(), event.getGuild())) {
            event.reply(String.format("Ik ga geen dingen voor je opzetten als je er toch niet zelf naar gaat luisteren, %s... :expressionless:", event.getMember().getAsMention())).queue();
            return;
        }

        var sourceOption = event.getOption("source");
        if (sourceOption == null) {
            event.reply(String.format("Zeg %s Ik kan niet dingen op de playlist zetten als je me niet zegt wat hè?! :shrug:", event.getMember().getAsMention())).queue();
            return;
        }

        var url = sourceOption.getAsString();
        if (!url.matches(".*")) {
            event.reply(String.format("Maarree %s, dit is niet een linkje waar ik iets mee kan hè? :rolling_eyes:", event.getMember().getAsMention())).queue();
            return;
        }

        event.deferReply().queue();
        trackListManager.loadTrack(url, event.getMember().getAsMention()).thenAccept(message -> event.getHook().editOriginal(message).queue());
    }
}
