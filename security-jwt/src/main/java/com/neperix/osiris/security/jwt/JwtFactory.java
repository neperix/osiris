package com.neperix.osiris.security.jwt;

import java.util.Optional;

import com.neperix.osiris.security.AuthenticatedUser;

interface JwtFactory {

    String generate(AuthenticatedUser user);

    String refresh(String token);

    Optional<Token> decode(String token);
}
