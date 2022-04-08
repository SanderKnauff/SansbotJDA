package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class ShuffleChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public ShuffleChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("shuffle", "Shuffle the queue");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        if(isInSameChannel(event.getMember(), event.getGuild())) {
            event.reply("Je luistert zelf niet eens. Je kan het schudden.").queue();
            return;
        }

        if (trackListManager.getQueue().size() <= 1) {
            event.reply("Ik kan wel mijn best doen, maar er valt niet veel te shuffelen...").queue();
            return;
        }

        event.reply("En we gooien de queue even door elkaar!").queue();
        trackListManager.shuffle();
    }
}
