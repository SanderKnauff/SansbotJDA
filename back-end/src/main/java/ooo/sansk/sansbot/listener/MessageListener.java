package ooo.sansk.sansbot.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
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
        if (!(event instanceof MessageReceivedEvent)) {
            return;
        }
        int attachmentCount = ((MessageReceivedEvent) event).getMessage().getAttachments().size();
        var messageReceivedEvent = (MessageReceivedEvent) event;
        logger.info("{} sent with {} attachments: \"{}\"", messageReceivedEvent.getAuthor().getName(), attachmentCount, messageReceivedEvent.getMessage().getContentDisplay());
        if(((MessageReceivedEvent) event).getMessage().getContentStripped().equals("Zeg kan jij een koprol voor ons doen?")) {
            ((MessageReceivedEvent) event).getChannel().sendMessage("Wat denk je dat ik ben, een verdraaide acrobaat?!").submit();
        }
    }

}
