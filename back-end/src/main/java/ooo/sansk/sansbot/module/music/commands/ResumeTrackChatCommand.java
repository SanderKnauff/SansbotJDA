package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class ResumeTrackChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public ResumeTrackChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("resume", "Resume the song if it was paused");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        if(isInSameChannel(event.getMember(), event.getGuild())) {
            if (trackListManager.resume()) {
                event.reply(String.format("En volgens %s kan het feestje weer beginnen! :tada:", event.getMember().getAsMention())).queue();
            } else {
                event.reply(String.format("Zeg %s, ik kan niks resumen als er verder niks af te spelen valt! :angry:", event.getMember().getAsMention())).queue();
            }
        } else {
            event.reply(String.format("Yo %s, ik geloof niet dat jij kan weten of men alweer zin heeft in de muziek... :confused:", event.getMember().getAsMention())).queue();
        }
    }
}
