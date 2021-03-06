package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class ResetSoundChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public ResetSoundChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("ResetSound", "ResetSounds", "RestartSound", "RestartSounds");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        reply(messageReceivedEvent.getChannel(), String.format("Nou %s, Ik kan niks beloven, maar ik zal mijn best doen! :tools:", messageReceivedEvent.getAuthor().getAsMention()));
        trackListManager.afterCreation();
    }
}
