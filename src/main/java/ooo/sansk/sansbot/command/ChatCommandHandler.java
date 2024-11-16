package ooo.sansk.sansbot.command;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (!(event instanceof SlashCommandInteractionEvent slashCommandEvent)) {
            return;
        }

        chatCommands.stream()
            .filter(command -> command.getCommandData().getName().equalsIgnoreCase(slashCommandEvent.getName()))
            .findFirst()
            .ifPresentOrElse(command -> {
                logger.atInfo()
                        .addArgument(() -> slashCommandEvent.getUser().getName())
                        .addArgument(() -> slashCommandEvent.getUser().getId())
                        .addArgument(slashCommandEvent::getName)
                        .addArgument(() -> slashCommandEvent.getOptions().stream().map(optionMapping -> optionMapping.getName() + "=" + optionMapping.getAsString()).collect(Collectors.joining(", ")))
                        .log("{} ({}) ran command: /{} {}");
                command.handle(slashCommandEvent);
            }, () -> {
                logger.info("{} ({}) attempted to run unknown command {}", slashCommandEvent.getUser().getName(), slashCommandEvent.getUser().getId(), slashCommandEvent.getName());
                slashCommandEvent.reply(String.format("%s, uuhhh... Nee ik denk niet dat ik begrijp wat je van mij wilt...", slashCommandEvent.getUser().getAsMention())).queue();
            });
    }

    public void registerCommand(ChatCommand chatCommand) {
        jda.getGuilds().getFirst().upsertCommand(chatCommand.getCommandData()).queue();
        chatCommands.add(chatCommand);
    }
}
