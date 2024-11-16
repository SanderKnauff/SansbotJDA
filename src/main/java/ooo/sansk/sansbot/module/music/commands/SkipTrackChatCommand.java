package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ooo.sansk.vaccine.annotation.Component;
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
        final var data = Commands.slash("skip", "Skip the current track");
        data.addOption(OptionType.NUMBER, "amount", "Amount of numbers to skip (default: 1)", false);
        return data;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        var toSkip = 1d;
        var option = event.getOption("amount");
        if (option != null && option.getType().equals(OptionType.NUMBER) && option.getAsDouble() > 1) {
            toSkip = option.getAsDouble();
        }

        if (isInSameChannel(event.getMember(), event.getGuild())) {
            event.reply(String.format("Ik kon hier wel van genieten, %s alleen niet. ¯\\_(ツ)_/¯ Ander plaatje dan maar?", event.getMember().getAsMention())).queue();
            trackListManager.skip((int) toSkip);
        } else {
            event.reply(String.format("Well %s, jij hebt hier toch geen last van. Laat ze lekker luisteren wat ze willen als je het toch niet kan horen... :rolling_eyes:", event.getMember().getAsMention())).queue();
        }
    }
}
