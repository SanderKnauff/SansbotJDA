package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class PauseTrackChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public PauseTrackChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("pause", "Pauses the track");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        if(isInSameChannel(event.getMember(), event.getGuild())) {
            if (trackListManager.pause()) {
                event.reply(String.format("Stop de plaat! %s heeft iets belangrijks te melden!", event.getMember().getAsMention())).queue();
            } else {
                event.reply(String.format("Er valt helemaal niks te pauzeren, %s!", event.getMember().getAsMention())).queue();
            }
        } else {
            event.reply(String.format("Zeg %s, we gaan niet de lol van andere verzieken als je er toch geen last van hebt... :angry:", event.getMember().getAsMention())).queue();
        }
    }
}
