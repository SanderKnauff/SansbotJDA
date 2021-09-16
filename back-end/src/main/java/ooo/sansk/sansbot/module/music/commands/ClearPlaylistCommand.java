package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class ClearPlaylistCommand extends ChatCommand {

    private final TrackListManager trackListManager;

    public ClearPlaylistCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("clearplaylist", "Clear all queued tracks");
    }

    @Override
    public void handle(SlashCommandEvent slashCommandEvent) {
        trackListManager.getQueue().clear();
        slashCommandEvent.reply("Weetje, het waren ook niet hele leuke dingen die in de queue stonden... :bucket: :broom:").queue();
    }
}
