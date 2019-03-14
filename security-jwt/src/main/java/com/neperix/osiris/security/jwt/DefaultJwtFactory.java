package com.neperix.osiris.security.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.neperix.osiris.security.AuthenticatedUser;

class DefaultJwtFactory implements JwtFactory {

    private final String issuer;
    private final Algorithm algorithm;
    private final long timeToLiveInHours;

    DefaultJwtFactory(JwtProperties jwtProperties) {
        this.issuer = jwtProperties.getIssuer();
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        this.timeToLiveInHours = jwtProperties.getTimeToLiveInHours();

    }

    @Override
    public String generate(AuthenticatedUser user) {
        return JWT.create()
                .withClaim("username", user.username())
                .withIssuer(this.issuer)
                .withExpiresAt(expirationDate())
                .sign(this.algorithm);
    }

    @Override
    public String refresh(String token) {

        Token decodedToken = decode(token)
                .orElseThrow(() -> new JwtException("Unable to decode token: " + token));

        return JWT.create()
                .withClaim("username", decodedToken.getUsername())
                .withIssuer(this.issuer)
                .withExpiresAt(expirationDate())
                .sign(this.algorithm);
    }

    @Override
    public Optional<Token> decode(String token) {

        try {
            return Optional.ofNullable(token)

                    .map(t -> t.substring(7))
                    .map(JWT::decode)
                    .map(decoded ->
                            Token.builder()
                                    .username(decoded.getClaim("username").asString())
                                    .expiresAt(LocalDateTime.ofInstant(decoded.getExpiresAt().toInstant(), ZoneId.systemDefault()))
                                    .build()
                    );
        } catch (Exception ex) {
            throw new JwtException("Unable to decode token: " + token, ex);
        }
    }

    private Date expirationDate() {
        return Date.from(LocalDateTime.now().plusHours(timeToLiveInHours).atZone(ZoneId.systemDefault()).toInstant());
    }
}
