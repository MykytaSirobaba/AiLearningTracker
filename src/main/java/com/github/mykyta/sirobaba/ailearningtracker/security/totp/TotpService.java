package com.github.mykyta.sirobaba.ailearningtracker.security.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;


/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
public class TotpService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
    @Value("${app.security.two-factor-setup-ttl-minutes}")
    private long setupTtlMinutes;

    /**
     * Генерує новий секретний ключ 2FA для користувача.
     */
    public String generateNewSecret() {
        return gAuth.createCredentials().getKey();
    }

    public boolean isSetupExpired(Instant createdAt) {
        if (createdAt == null) {
            return true;
        }
        Duration timeSinceCreation = Duration.between(createdAt, Instant.now());

        return timeSinceCreation.toMinutes() > setupTtlMinutes;
    }

    /**
     * Перевіряє наданий користувачем код 2FA.
     * @param secret2fa Секретний ключ користувача з бази даних.
     * @param code Код, введений користувачем.
     * @return true, якщо код дійсний.
     */
    public boolean validateCode(String secret2fa, String code) {
        try {
            int intCode = Integer.parseInt(code);
            return gAuth.authorize(secret2fa, intCode);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}