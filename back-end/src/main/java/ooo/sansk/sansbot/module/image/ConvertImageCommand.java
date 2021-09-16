package ooo.sansk.sansbot.module.image;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.image.filter.FilterType;
import ooo.sansk.sansbot.module.image.filter.ImageFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ConvertImageCommand extends ChatCommand {

    private static final int MAX_FILE_SIZE_BYTES = 8388608;
    public static final String OPTION_NAME_FILTER = "filter";

    public ConvertImageCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
    }

    @Override
    public CommandData getCommandData() {
        final var commandData = new CommandData("convertimage", "Applies a filter to an image");
        commandData.addOption(OptionType.STRING, OPTION_NAME_FILTER, "The name of the filter to apply", true);
        return commandData;
    }

    @Override
    public void handle(SlashCommandEvent event) {
        var filterOption = event.getOption(OPTION_NAME_FILTER);

        var optionalFilter = FilterType.getFilter(filterOption.getAsString()).map(FilterType::getFilter);
        if (optionalFilter.isEmpty()) {
            event.reply("Ik kan een hoop, maar dat nou net weer niet.").queue();
            return;
        }

        event.deferReply().queue();

        event.getChannel().getHistory().retrievePast(10).queue(messages -> {
            var optionalBufferedImage = getFirstLoadedMessageWithImageAttachment(messages);
            if (optionalBufferedImage.isEmpty()) {
                event.getHook().editOriginal(String.format("Ge snappe het volgensmij niet helemaal hè, %s. Ge motten wel een plaatje erbij uploaden hè? :angry:", event.getMember().getAsMention())).queue();
            }
            var imageResult = optionalFilter.get().doFilter(optionalBufferedImage.get());

            if (imageResult.imageData().length < MAX_FILE_SIZE_BYTES) {
                var messageAction = event.getChannel().sendFile(imageResult.imageData(), "output." + imageResult.imageType());
                if (event.getChannel().getType().isGuild()) {
                    messageAction = messageAction.content(event.getMember().getAsMention());
                }
                messageAction.submit();
            } else {
                event.getHook().editOriginal(String.format("Oh nee! deze is te groot voor mij, %s :confounded:", event.getMember().getAsMention())).queue();
            }
        });
    }

    private Optional<BufferedImage> getFirstLoadedMessageWithImageAttachment(List<Message> messages) {
        return messages
                .stream()
                .flatMap(message -> message.getAttachments().stream())
                .filter(Message.Attachment::isImage)
                .findFirst()
                .map(this::downloadImageFromAttachment);
    }

    private BufferedImage downloadImageFromAttachment(Message.Attachment attachment) {
        try {
            return ImageIO.read(new URL(attachment.getUrl()).openStream());
        } catch (IOException e) {
            return null;
        }
    }
}
