package ooo.sansk.sansbot.module.web.login;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;

@Component
public class TokenCommand extends ChatCommand {

    private final LoginService loginService;

    public TokenCommand(ChatCommandHandler chatCommandHandler, LoginService loginService) {
        super(chatCommandHandler);
        this.loginService = loginService;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("auth", "Get a token for the web interface");
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var token = loginService.createWebToken(event.getUser().getId());
        event.reply("Your WebToken is:" + token.token()).setEphemeral(true).queue();
    }
}
