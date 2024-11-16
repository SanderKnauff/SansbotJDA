package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ooo.sansk.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class MoveChannelCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public MoveChannelCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("summon", "Summons the bot to your audio channel");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        final var requester = event.getMember();
        if (requester == null) {
            event.reply("Maar jij bestaat helemaal niet! :confused:").queue();
            return;
        }

        final var requesterVoiceState = requester.getVoiceState();
        if (requesterVoiceState == null || !requesterVoiceState.inAudioChannel()) {
            event.reply(String.format("Zeg %s, je zit helemaal niet in een kanaal. Hoe moet ik nu weten waar ik naartoe moet?! :confused:", event.getMember().getAsMention())).queue();
            return;
        }

        final var requesterChannel = requesterVoiceState.getChannel();
        if (requesterChannel == null) {
            event.reply(String.format("Uhh %s, Ik kan je kanaal niet vinden... :confused:", event.getMember().getAsMention())).queue();
            return;
        }

        if (event.getGuild() == null) {
            event.reply(String.format("Één klein probleempje, %s. Ik heb geen idee waar ik zelf ben... :confused:", requester.getAsMention())).queue();
            return;
        }
        final var bot = event.getGuild().getSelfMember();

        final var voiceState = bot.getVoiceState();
        if (voiceState == null) {
            event.reply(String.format("Ik kom al! %s :yum:", requester.getAsMention())).queue();
            event.getGuild().getAudioManager().openAudioConnection(requesterChannel);
            return;
        }

        final var currentChannel = bot.getVoiceState().getChannel();
        if (currentChannel != null && bot.getVoiceState().getChannel().equals(requester.getVoiceState().getChannel())) {
            event.reply(String.format("Uhh, volgensmij ben ik er al hoor, %s? :thinking:", event.getMember().getAsMention())).queue();
            return;
        }
        if (trackListManager.getCurrentTrack() != null) {
            event.reply(String.format("Sorry, anderen waren eerst en die zijn nog aan het genieten van de muziek. Probeer het anders later nog eens, %s? :upside_down:", event.getMember().getAsMention())).queue();
            return;
        }
        event.reply(String.format("Ik kom al! %s :yum:", event.getMember().getAsMention())).queue();
        event.getGuild().getAudioManager().openAudioConnection(requesterChannel);
    }
}
