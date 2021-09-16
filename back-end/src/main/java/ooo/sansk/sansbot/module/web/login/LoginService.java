package ooo.sansk.sansbot.module.web.login;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.web.session.SessionFilter;
import ooo.sansk.sansbot.module.web.session.SessionInformation;
import spark.Request;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LoginService {

    private final JDA jda;
    private final List<WebToken> requestedTokens;
    private final SecureRandom secureRandom;
    private final Clock clock;

    public LoginService(JDA jda, Clock clock) {
        this.jda = jda;
        this.clock = clock;
        this.requestedTokens = new ArrayList<>();
        this.secureRandom = new SecureRandom();
    }

    public Optional<WebUserDetails> attemptLogin(Request request, String loginToken) {
        Optional<WebUserDetails> userDetails = requestedTokens.stream()
                .filter(webToken -> webToken.token().equals(loginToken))
                .filter(webToken -> webToken.expirationTime().isAfter(ZonedDateTime.now(clock)))
                .map(this::createUserDetails)
                .findFirst();
        userDetails.ifPresent(webUserDetails -> request.session().attribute(SessionFilter.SESSION_INFORMATION, createSessionInformation(request.ip())));
        return userDetails;
    }

    public WebToken createWebToken(String id) {
        requestedTokens.removeIf(webToken -> webToken.userId().equals(id));
        WebToken newToken = new WebToken(createLoginTokenExpirationTime(), id, generateLoginToken());
        requestedTokens.add(newToken);
        return newToken;
    }

    public List<WebToken> getRequestedTokens() {
        return requestedTokens;
    }

    private WebUserDetails createUserDetails(WebToken webToken) {
        User userDetails = jda.getUserById(webToken.userId());
        return new WebUserDetails(userDetails.getName(), userDetails.getAvatarUrl());
    }

    private String generateLoginToken() {
        return String.format("%6d", (secureRandom.nextInt(999999) + 1)).replace(' ', '0');
    }

    private ZonedDateTime createLoginTokenExpirationTime() {
        return ZonedDateTime.now(clock).plusMinutes(15);
    }

    private SessionInformation createSessionInformation(String ip) {
        return new SessionInformation(ip, createSessionTokenExpirationTime(), clock);
    }

    private ZonedDateTime createSessionTokenExpirationTime() {
        return ZonedDateTime.now(clock).plusHours(2);
    }
}
