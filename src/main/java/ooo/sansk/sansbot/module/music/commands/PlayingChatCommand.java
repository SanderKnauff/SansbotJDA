package ooo.sansk.sansbot.module.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class PlayingChatCommand extends ChatCommand {
    private final TrackListManager trackListManager;

    public PlayingChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("playing", "Get the current playing track");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        AudioTrack track = trackListManager.getCurrentTrack();
        if(track == null) {
            event.reply(String.format("%s, volgensmij zie je ze vliegen want ik speel niks af hoor... :confused:", event.getMember().getAsMention())).queue();
        } else {
            event.reply(String.format("%s, als ik mij niet vergis is dit... deze! :musical_score: %n%s", event.getMember().getAsMention(), track.getInfo().uri)).queue();
        }
    }
}
