package com.example.quora_app.feature.auth.service;

import com.example.quora_app.feature.auth.entity.RefreshToken;
import com.example.quora_app.feature.auth.repository.RefreshTokenRepository;
import com.example.quora_app.feature.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.auth.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public IssuedRefreshToken createInitialToken(User user) {
        UUID familyId = UUID.randomUUID();
        return createToken(user, familyId);
    }

    @Override
    @Transactional
    public RotatedRefreshToken rotate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return invalidResult();
        }
        String oldHash = hashToken(rawToken);

        var optionalToken = refreshTokenRepository.findByTokenHashForUpdate(oldHash);

        if (optionalToken.isEmpty()) {
            return invalidResult();
        }

        RefreshToken existing = optionalToken.get();

        // A revoked token being presented again can indicate token reuse.
        if (existing.isRevoked()) {
            revokeAllInFamily(existing.getFamilyId());
            // Return normally so the transaction can commit the revocations.
            return invalidResult();
        }

        if (existing.isExpired()) {
            revokeAllInFamily(existing.getFamilyId());
            return invalidResult();
        }

        User user = existing.getUser();
        UUID familyId = existing.getFamilyId();

        // Generate a new random token and store only its hash.
        IssuedRefreshToken replacement = createToken(user, familyId);

        // Preserve the relationship between old and replacement tokens.
        existing.setReplacedByTokenHash(hashToken(replacement.rawToken()));

        // The old token must never be accepted again.
        existing.revoke();

        return new RotatedRefreshToken(
                true,
                replacement.rawToken(),
                user.getEmail(),
                replacement.maxAgeSeconds()
        );
    }

    @Override
    @Transactional
    public void revokeFamily(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        String tokenHash = hashToken(rawToken);
        refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .ifPresent(token -> revokeAllInFamily(token.getFamilyId()));
    }

    private IssuedRefreshToken createToken(User user, UUID familyId) {
        // Generate 256 bits of cryptographically secure random data.
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String tokenHash = hashToken(rawToken);

        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration));

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .familyId(familyId)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(token);

        return new IssuedRefreshToken(
                rawToken,
                refreshTokenExpiration / 1000
        );
    }

    private void revokeAllInFamily(UUID familyId) {
        refreshTokenRepository.findAllByFamilyId(familyId)
                .forEach(RefreshToken::revoke);
    }

    private RotatedRefreshToken invalidResult() {
        return new RotatedRefreshToken(
                false,
                null,
                null,
                0
        );
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
