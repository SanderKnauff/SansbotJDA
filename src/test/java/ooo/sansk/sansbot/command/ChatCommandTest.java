package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatCommandTest {
    private static final String MESSAGE_REPLY = "Reply";

    @Mock
    private ChatCommandHandler mockChatCommandHandler;

    @InjectMocks
    private TestCommand subject;

    //In this test the @Before method has been replaced by Mockito's annotation based testing setup.
    //This class requires no additional setup before testing begins, therefor the method is missing.

    @Test
    void afterCreationRegistersCommandInCommandHandler() {
        subject.afterCreation();

        verify(mockChatCommandHandler).registerCommand(eq(subject));
    }

    @Test
    void deleteMessageIfPossibleShouldOnlyDeleteInTextChannels() {
        var mockChannel = mock(TextChannelImpl.class);
        var mockMessage = mock(Message.class);
        doReturn(mockChannel).when(mockMessage).getChannel();
        doReturn(ChannelType.VOICE).when(mockChannel).getType();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockMessage, never()).delete();
    }

    @Disabled
    @Test
    void deleteMessageIfPossibleShouldOnlyTryToDeleteWithPermission() {
        final var mockTextChannel = mock(TextChannelImpl.class);
        final var mockMessage = mock(Message.class);
        final var mockGuild = mock(GuildImpl.class);
        final var mockSelfMember = mock(Member.class);
        final var mockRole = mock(Role.class);
        doReturn(mockTextChannel).when(mockMessage).getChannel();
        when(mockTextChannel.asTextChannel()).thenReturn(mockTextChannel);
        when(mockTextChannel.getType()).thenReturn(ChannelType.TEXT);
        doReturn(mockGuild).when(mockMessage).getGuild();
        doReturn(mockGuild).when(mockSelfMember).getGuild();
        doReturn(mockGuild).when(mockTextChannel).getGuild();
        doReturn(mockSelfMember).when(mockGuild).getSelfMember();
        doReturn(mockRole).when(mockGuild).getPublicRole();
        doReturn(false).when(mockSelfMember).isOwner();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockMessage, never()).delete();
    }

    @Disabled
    @Test
    void deleteMessageIfPossibleSucceeds() {
        final var mockMessageChannel = mock(TextChannelImpl.class);
        final var mockTextChannel = mock(TextChannelImpl.class);
        final var mockMessage = mock(Message.class);
        final var mockGuild = mock(GuildImpl.class);
        final var mockSelfMember = mock(Member.class);
        final var mockAuditableRestAction = mock(AuditableRestAction.class);
        doReturn(mockMessageChannel).when(mockMessage).getChannel();
        doReturn(mockTextChannel).when(mockMessage).getChannel();
        doReturn(ChannelType.TEXT).when(mockMessageChannel).getType();
        doReturn(mockGuild).when(mockTextChannel).getGuild();
        doReturn(mockGuild).when(mockMessage).getGuild();
        doReturn(mockGuild).when(mockSelfMember).getGuild();
        doReturn(mockSelfMember).when(mockGuild).getSelfMember();
        doReturn(mockAuditableRestAction).when(mockMessage).delete();
        doReturn(true).when(mockSelfMember).isOwner();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockAuditableRestAction).queue();
    }

    private static class TestCommand extends ChatCommand {

        public TestCommand(ChatCommandHandler chatCommandHandler) {
            super(chatCommandHandler);
        }

        @Override
        public CommandData getCommandData() {
            return Commands.slash("", "");
        }

        @Override
        public void handle(SlashCommandInteractionEvent event) {
        }
    }
}
