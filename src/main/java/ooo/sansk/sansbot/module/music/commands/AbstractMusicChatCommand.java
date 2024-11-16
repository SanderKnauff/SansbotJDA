package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nullable;

public abstract class AbstractMusicChatCommand extends ChatCommand {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMusicChatCommand.class);

    protected AbstractMusicChatCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
    }

    public boolean isInSameChannel(@Nullable Member member, @Nullable Guild guild) {
        if(member != null && guild != null) {
            final var voiceChannel = member.getVoiceState().getChannel();
            final var botChannel = guild.getSelfMember().getVoiceState().getChannel();
            return voiceChannel != null && botChannel != null && member.getVoiceState().getChannel().equals(guild.getSelfMember().getVoiceState().getChannel());
        } else {
            //Even though Sansbot only runs in one guild at the moment, we should not use that fact to make assumptions. This probably needs some other handling as well
            logger.warn("Not known which guild this command needs to be ran for (nor which member this would be)");
            return false;
        }
    }
}
