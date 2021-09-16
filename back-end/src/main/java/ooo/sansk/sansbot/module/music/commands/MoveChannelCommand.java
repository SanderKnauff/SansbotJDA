package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class MoveChannelCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public MoveChannelCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("sansbotplz", "Summons the bot to your channel");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            event.reply(String.format("Sorry %s, ik ben even niet beschikbaar voor je feestje... :disappointed:", event.getMember().getAsMention())).queue();
            return;
        }
        if (event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel())) {
            event.reply(String.format("Uhh, volgensmij ben ik er al hoor, %s? :thinking:", event.getMember().getAsMention())).queue();
            return;
        }
        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            event.reply(String.format("Zeg %s, je zit helemaal niet in een kanaal. Hoe moet ik nu weten waar ik naartoe moet?! :confused:", event.getMember().getAsMention())).queue();
            return;
        }
        if (trackListManager.getCurrentTrack() != null) {
            event.reply(String.format("Sorry, anderen waren eerst en die zijn nog aan het genieten van de muziek. Probeer het anders later nog eens, %s? :upside_down:", event.getMember().getAsMention())).queue();
            return;
        }
        event.reply(String.format("Ik kom al! %s :yum:", event.getMember().getAsMention())).queue();
        event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
    }
}
