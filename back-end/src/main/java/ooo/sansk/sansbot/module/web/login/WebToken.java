package ooo.sansk.sansbot.module.web.login;

import java.time.ZonedDateTime;

public record WebToken(ZonedDateTime expirationTime, String userId, String token) {}
