package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@Component
public class ChatCommandHandler implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(ChatCommandHandler.class);

    private final JDA jda;
    private final Set<ChatCommand> chatCommands;

    public ChatCommandHandler(JDA jda) {
        this.chatCommands = new HashSet<>();
        this.jda = jda;
    }

    @AfterCreate
    public void postConstruct() {
        logger.info("Registering {}", this.getClass().getSimpleName());
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof SlashCommandEvent slashCommandEvent) {
            for (ChatCommand chatCommand : chatCommands) {
                if (chatCommand.getCommandData().getName().equalsIgnoreCase(slashCommandEvent.getName())) {
                    chatCommand.handle(slashCommandEvent);
                    return;
                }
            }
            slashCommandEvent.reply(
                String.format("%s, uuhhh... Nee ik denk niet dat ik begrijp wat je van mij wilt...",
                slashCommandEvent.getMember().getAsMention())
            ).queue();
        }
    }

    public void registerCommand(ChatCommand chatCommand) {
        jda.upsertCommand(chatCommand.getCommandData()).queue();
        chatCommands.add(chatCommand);
    }
}
