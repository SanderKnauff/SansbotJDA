package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class ResetSoundChatCommand extends AbstractMusicChatCommand {
    private final TrackListManager trackListManager;

    public ResetSoundChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("resetsound", "Restart Sansbot's sound engine");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.reply(String.format("Nou %s, Ik kan niks beloven, maar ik zal mijn best doen! :tools:", event.getMember().getAsMention())).queue();
        trackListManager.afterCreation();
    }
}
