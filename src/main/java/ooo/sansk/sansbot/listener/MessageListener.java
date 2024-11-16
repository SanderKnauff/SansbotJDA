package ooo.sansk.sansbot.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import ooo.sansk.vaccine.annotation.AfterCreate;
import ooo.sansk.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class MessageListener implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    private final JDA jda;

    public MessageListener(JDA jda) {
        this.jda = jda;
    }

    @AfterCreate
    public void postConstruct() {
        logger.info("Registering {}", this.getClass().getSimpleName());
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof MessageReceivedEvent messageReceivedEvent)) {
            return;
        }

        int attachmentCount = ((MessageReceivedEvent) event).getMessage().getAttachments().size();
        logger.info("{} sent with {} attachments: \"{}\"", messageReceivedEvent.getAuthor().getName(), attachmentCount, messageReceivedEvent.getMessage().getContentDisplay());
    }

}
