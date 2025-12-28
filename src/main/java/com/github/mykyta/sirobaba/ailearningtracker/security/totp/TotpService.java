package com.github.mykyta.sirobaba.ailearningtracker.security.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Service for handling Time-based One-Time Password (TOTP) operations.
 * <p>
 * Provides methods for generating 2FA secrets, validating codes, and checking expiration.
 * Uses Google Authenticator library for TOTP generation and verification.
 * <p>
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
public class TotpService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Value("${app.security.two-factor-setup-ttl-minutes}")
    private long setupTtlMinutes;

    /**
     * Generates a new 2FA secret key for a user.
     *
     * @return a newly generated secret key as a String
     */
    public String generateNewSecret() {
        return gAuth.createCredentials().getKey();
    }

    /**
     * Checks if the 2FA setup has expired based on its creation timestamp.
     *
     * @param createdAt the timestamp when the 2FA secret was created
     * @return true if the setup has expired or creation timestamp is null
     */
    public boolean isSetupExpired(Instant createdAt) {
        if (createdAt == null) {
            return true;
        }
        Duration timeSinceCreation = Duration.between(createdAt, Instant.now());
        return timeSinceCreation.toMinutes() > setupTtlMinutes;
    }

    /**
     * Validates a provided 2FA code against the user's secret key.
     *
     * @param secret2fa the user's secret key stored in the database
     * @param code      the code entered by the user
     * @return true if the code is valid, false otherwise
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
