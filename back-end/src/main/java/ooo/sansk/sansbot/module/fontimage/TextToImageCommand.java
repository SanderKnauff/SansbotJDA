package ooo.sansk.sansbot.module.fontimage;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class TextToImageCommand extends ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(TextToImageCommand.class);
    public static final String COMMAND_OPTION_FONT = "font";
    public static final String COMMAND_OPTION_TEXT = "text";

    private final TextToImageConverter textToImageConverter;

    public TextToImageCommand(ChatCommandHandler chatCommandHandler, TextToImageConverter textToImageConverter) {
        super(chatCommandHandler);
        this.textToImageConverter = textToImageConverter;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("tti", "Displays your text with an image font")
            .addOption(OptionType.STRING, COMMAND_OPTION_FONT, "The font to use", true)
            .addOption(OptionType.STRING, COMMAND_OPTION_TEXT, "The text to write with the font", true);
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var fontOption = event.getOption(COMMAND_OPTION_FONT);
        if (fontOption == null) {
            event.reply(String.format("Ik krijg nog een lettertype van je! Wil je soms met een blok beton aan je voeten het water in, %s? :angry:", event.getMember().getAsMention()))
                .setEphemeral(true)
                .queue();
            return;
        }

        var textOption = event.getOption(COMMAND_OPTION_TEXT);
        if (textOption == null) {
            event.reply(String.format("Ik krijg nog een stuk text van je! Wil je soms met een blok beton aan je voeten het water in, %s? :angry:", event.getMember().getAsMention()))
                .setEphemeral(true)
                .queue();
            return;
        }

        var font = fontOption.getAsString();
        var text = textOption.getAsString();
        var textToImageConversionResult = textToImageConverter.convertText(font, text);
        if (textToImageConversionResult.isSuccessful()) {
            event.deferReply().queue();
            var baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(textToImageConversionResult.getOutput(), "png", baos);
                event.getInteraction().getHook().editOriginal(baos.toByteArray(), "tti.png").queue();
            } catch (IOException e) {
                logger.error("An error occurred while writing the image to Discord ({}: {})", e.getClass().getSimpleName(), e.getMessage());
                event.getInteraction().getHook().editOriginal(String.format("Ik zal eerlijk zijn %s.... Je hebt het gesloopt! :upside_down:", event.getMember().getAsMention())).queue();
            }
        } else {
            event.reply(String.format("Dat letterype bestaat niet, %s! :confused:", event.getMember().getAsMention())).queue();
        }


    }
}
