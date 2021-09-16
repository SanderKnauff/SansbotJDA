package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import nl.imine.vaccine.annotation.AfterCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChatCommand.class);

    protected final ChatCommandHandler chatCommandHandler;

    public ChatCommand(ChatCommandHandler chatCommandHandler) {
        this.chatCommandHandler = chatCommandHandler;
    }

    @AfterCreate
    public void afterCreation(){
        logger.trace("Registering chat command: {}", getClass().getSimpleName());
        chatCommandHandler.registerCommand(this);
    }

    public abstract CommandData getCommandData();

    public abstract void handle(SlashCommandEvent slashCommandEvent);

    public void deleteMessageIfPossible(Message message) {
        if (!message.getChannel().getType().equals(ChannelType.TEXT)) {
            logger.trace("Message could not be deleted due to not being in a Guild Text Channel");
            return;
        }
        if (!PermissionUtil.checkPermission(message.getTextChannel(), message.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
            logger.trace("Message could not be deleted due to not having permission for this channel ()");
            return;
        }
        message.delete().queue();
    }
}
