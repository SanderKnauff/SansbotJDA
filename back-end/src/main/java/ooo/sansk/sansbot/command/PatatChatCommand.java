package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import nl.imine.vaccine.annotation.Component;

import java.io.InputStream;

@Component
public class PatatChatCommand extends ChatCommand {

    public PatatChatCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("wevliegenerin", "Lap zeg!");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        final InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("Patat.jpg");
        final ReplyAction reply = event.reply("**PATAT!**");
        if (systemResourceAsStream != null) {
            reply.addFile(systemResourceAsStream, "Patat.jpg").submit();
        }
        reply.queue();
    }
}
