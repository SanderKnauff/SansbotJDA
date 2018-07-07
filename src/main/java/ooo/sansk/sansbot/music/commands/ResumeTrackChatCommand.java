package ooo.sansk.sansbot.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class ResumeTrackChatCommand extends AbstractMusicChatCommand {

    private final VoiceHandler voiceHandler;

    public ResumeTrackChatCommand(ChatCommandHandler chatCommandHandler, VoiceHandler voiceHandler) {
        super(chatCommandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("resume", "DraaiMaarDoor", "WasTochNietZoBelangrijk");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            if (voiceHandler.resume()) {
                chatCommandHandler.getDefaultOutputChannel().sendMessage(String.format("En volgens %s kan het feestje weer beginnen! :tada:", messageReceivedEvent.getAuthor().getAsMention())).queue();
            } else {
                reply(messageReceivedEvent.getChannel(), String.format("Zeg %s, ik kan niks resumen als er verder niks af te spelen valt! :angry:", messageReceivedEvent.getAuthor().getAsMention()));
            }
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Yo %s, ik geloof niet dat jij kan weten of men alweer zin heeft in de muziek... :confused:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}