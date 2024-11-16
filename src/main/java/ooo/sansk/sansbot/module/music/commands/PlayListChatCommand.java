package ooo.sansk.sansbot.module.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import ooo.sansk.vaccine.annotation.Component;
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
        return Commands.slash("playlist",  "Show the tracks in the queue");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if(!trackListManager.getQueue().isEmpty()) {
            final var embedBuilder = new EmbedBuilder().setTitle(":cd: PlayList");
            trackListManager.getQueue().stream()
                    .limit(25)
                    .map(AudioTrack::getInfo)
                    .forEach(track -> embedBuilder.addField(track.title + " | " + track.length, track.author + " (" + track.uri + ")", false));
            final var messageBuilder = new MessageCreateBuilder()
                    .addContent("Hier %s, dit zijn nummers die er nog aan zullen komen!".formatted(event.getMember().getAsMention()))
                    .addEmbeds(embedBuilder.build());
            event.reply(messageBuilder.build()).queue();
        } else {
            event.reply(String.format("Sorry %s, maar er staat nog niks op de lijst. Misschien kan je zelf wat toevoegen!", event.getMember().getAsMention())).queue();
        }
    }
}
