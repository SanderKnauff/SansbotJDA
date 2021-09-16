package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

@Component
public class SkipTrackChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public SkipTrackChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("skip", "Skip the current track");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        if (isInSameChannel(event.getMember(), event.getGuild())) {
            event.reply(String.format("Ik kon hier wel van genieten, %s alleen niet. ¯\\_(ツ)_/¯ Ander plaatje dan maar?", event.getMember().getAsMention())).queue();
            trackListManager.skip();
        } else {
            event.reply(String.format("Well %s, jij hebt hier toch geen last van. Laat ze lekker luisteren wat ze willen als je het toch niet kan horen... :rolling_eyes:", event.getMember().getAsMention())).queue();
        }
    }
}
