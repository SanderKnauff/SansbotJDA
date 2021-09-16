package ooo.sansk.sansbot.module.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class PlayListChatCommand extends ChatCommand {

    private final TrackListManager trackListManager;

    public PlayListChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("playlist",  "Show the tracks in the queue");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        if(!trackListManager.getQueue().isEmpty()) {
            var embedBuilder = new EmbedBuilder().setTitle(":cd: PlayList");
            trackListManager.getQueue().stream()
                    .limit(25)
                    .map(AudioTrack::getInfo)
                    .forEach(track -> embedBuilder.addField(track.title + " | " + track.length, track.author + " (" + track.uri + ")", false));
            event.reply(new MessageBuilder(String.format("Hier %s, dit zijn nummers die er nog aan zullen komen!", event.getMember().getAsMention())).setEmbeds(embedBuilder.build()).build()).queue();
        } else {
            event.reply(String.format("Sorry %s, maar er staat nog niks op de lijst. Misschien kan je zelf wat toevoegen!", event.getMember().getAsMention())).queue();
        }
    }
}
