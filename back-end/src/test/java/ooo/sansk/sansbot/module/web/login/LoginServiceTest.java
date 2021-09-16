package ooo.sansk.sansbot.module.web.login;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import ooo.sansk.sansbot.module.web.session.SessionFilter;
import ooo.sansk.sansbot.module.web.session.SessionInformation;
import ooo.sansk.sansbot.util.ChangeableFixedClock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.Request;
import spark.Session;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static ooo.sansk.sansbot.util.HasSize.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoginServiceTest {

    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_AVATAR_URL = "USER_AVATAR";
    private static final String USER_IP = "USER_IP";
    private static final Instant CURRENT_TIME = Instant.EPOCH;

    private ChangeableFixedClock fixedClock;
    private JDA mockJDA = mock(JDA.class);
    private Request mockRequest = mock(Request.class);

    private LoginService subject;

    @Before
    public void setUp() {
        fixedClock = new ChangeableFixedClock(CURRENT_TIME, ZoneId.systemDefault());
        subject = new LoginService(mockJDA, fixedClock);
    }

    @Test
    public void createWebTokenAddsToRequestedTokenList() {
        subject.createWebToken(USER_ID);

        assertThat(subject.getRequestedTokens(), hasSize(1));
        assertThat(subject.getRequestedTokens().get(0).userId(), equalTo(USER_ID));
        assertEquals(Instant.EPOCH.plusSeconds(60 * 15), subject.getRequestedTokens().get(0).expirationTime().toInstant());
    }

    @Test
    public void createWebTokenShouldOverrideOldToken() {
        subject.getRequestedTokens().add(new WebToken(ZonedDateTime.now(fixedClock), USER_ID, "000000"));
        subject.getRequestedTokens().add(new WebToken(ZonedDateTime.now(fixedClock), "OTHER_USER_ID", "000001"));
        assertThat(subject.getRequestedTokens(), hasSize(2));

        subject.createWebToken(USER_ID);

        assertThat(subject.getRequestedTokens(), hasSize(2));
        assertThat(subject.getRequestedTokens().get(1).userId(), equalTo(USER_ID));
        assertEquals(Instant.EPOCH.plusSeconds(60 * 15), subject.getRequestedTokens().get(1).expirationTime().toInstant());
    }

    @Test
    public void attemptLoginShouldReturnEmptyIfTokenDoesNotExist() {
        subject.getRequestedTokens().add(new WebToken(ZonedDateTime.now(fixedClock), USER_ID, "000001"));

        Optional<WebUserDetails> result = subject.attemptLogin(mockRequest, "000000");

        assertTrue(result.isEmpty());
    }

    @Test
    public void attemptLoginShouldReturnEmptyIfTokenIsExpired() {
        subject.getRequestedTokens().add(new WebToken(ZonedDateTime.now(fixedClock), USER_ID, "000000"));
        fixedClock.setInstant(fixedClock.instant().plusSeconds(60 * 16));

        Optional<WebUserDetails> result = subject.attemptLogin(mockRequest, "000000");

        assertTrue(result.isEmpty());
    }

    @Test
    public void attemptLoginShouldReturnWebUserDetailsAndUpdateSessionDetails() {
        var sessionDataCaptor = ArgumentCaptor.forClass(SessionInformation.class);
        var mockUser = mock(User.class);
        var mockSession = mock(Session.class);
        doReturn(mockUser).when(mockJDA).getUserById(USER_ID);
        doReturn(USER_NAME).when(mockUser).getName();
        doReturn(USER_AVATAR_URL).when(mockUser).getAvatarUrl();
        doReturn(mockSession).when(mockRequest).session();
        doReturn(USER_IP).when(mockRequest).ip();
        doNothing().when(mockSession).attribute(eq(SessionFilter.SESSION_INFORMATION), sessionDataCaptor.capture());

        subject.getRequestedTokens().add(new WebToken(ZonedDateTime.now(fixedClock).plusMinutes(15), USER_ID, "000000"));

        Optional<WebUserDetails> result = subject.attemptLogin(mockRequest, "000000");

        assertTrue(result.isPresent());
        WebUserDetails details = result.get();
        SessionInformation sessionInformation = sessionDataCaptor.getValue();
        assertSame(USER_NAME, details.username());
        assertEquals(USER_AVATAR_URL, details.avatarUrl());
        //I would have used assertEquals, but this is a nice example what you can do but just shouldn't
        assertFalse(!USER_IP.equals(sessionInformation.getIp()));
    }
}
