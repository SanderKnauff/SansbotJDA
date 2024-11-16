package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import ooo.sansk.vaccine.annotation.AfterCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChatCommand {
    private static final Logger logger = LoggerFactory.getLogger(ChatCommand.class);

    protected final ChatCommandHandler chatCommandHandler;

    protected ChatCommand(ChatCommandHandler chatCommandHandler) {
        this.chatCommandHandler = chatCommandHandler;
    }

    @AfterCreate
    public void afterCreation(){
        logger.trace("Registering chat command: {}", getClass().getSimpleName());
        chatCommandHandler.registerCommand(this);
    }

    public abstract CommandData getCommandData();

    public abstract void handle(SlashCommandInteractionEvent slashCommandEvent);

    public void deleteMessageIfPossible(Message message) {
        if (!message.getChannel().getType().equals(ChannelType.TEXT)) {
            logger.trace("Message could not be deleted due to not being in a Guild Text Channel");
            return;
        }
        if (!PermissionUtil.checkPermission(message.getChannel().asTextChannel(), message.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
            logger.trace("Message could not be deleted due to not having permission for this channel ()");
            return;
        }
        message.delete().queue();
    }
}
