package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChatCommandTest {

    private static final String MESSAGE_REPLY = "Reply";

    @Mock
    private ChatCommandHandler mockChatCommandHandler;

    @InjectMocks
    private TestCommand subject;

    //In this test the @Before method has been replaced by Mockito's annotation based testing setup.
    //This class requires no additional setup before testing begins, therefor the method is missing.

    @Test
    public void afterCreationRegistersCommandInCommandHandler() {
        subject.afterCreation();

        verify(mockChatCommandHandler).registerCommand(eq(subject));
    }

    @Test
    public void deleteMessageIfPossibleShouldOnlyDeleteInTextChannels() {
        var mockChannel = mock(MessageChannel.class);
        var mockMessage = mock(Message.class);
        doReturn(mockChannel).when(mockMessage).getChannel();
        doReturn(ChannelType.VOICE).when(mockChannel).getType();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockMessage, never()).delete();
    }

    @Test
    public void deleteMessageIfPossibleShouldOnlyTryToDeleteWithPermission() {
        var mockMessageChannel = mock(MessageChannel.class);
        var mockTextChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockGuild = mock(GuildImpl.class);
        var mockSelfMember = mock(Member.class);
        var mockRole = mock(Role.class);
        doReturn(mockMessageChannel).when(mockMessage).getChannel();
        doReturn(mockTextChannel).when(mockMessage).getTextChannel();
        doReturn(ChannelType.TEXT).when(mockMessageChannel).getType();
        doReturn(mockGuild).when(mockTextChannel).getGuild();
        doReturn(mockGuild).when(mockMessage).getGuild();
        doReturn(mockGuild).when(mockSelfMember).getGuild();
        doReturn(mockSelfMember).when(mockGuild).getSelfMember();
        doReturn(mockRole).when(mockGuild).getPublicRole();
        doReturn(false).when(mockSelfMember).isOwner();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockMessage, never()).delete();
    }

    @Test
    public void deleteMessageIfPossibleSucceeds() {
        var mockMessageChannel = mock(MessageChannel.class);
        var mockTextChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockGuild = mock(GuildImpl.class);
        var mockSelfMember = mock(Member.class);
        var mockAuditableRestAction = mock(AuditableRestAction.class);
        doReturn(mockMessageChannel).when(mockMessage).getChannel();
        doReturn(mockTextChannel).when(mockMessage).getTextChannel();
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
            return new CommandData("", "");
        }

        @Override
        public void handle(SlashCommandEvent event) {
        }
    }
}
