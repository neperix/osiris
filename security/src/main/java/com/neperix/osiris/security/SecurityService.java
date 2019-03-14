package com.neperix.osiris.security;

import java.util.Optional;

public interface SecurityService {

    Optional<AuthenticatedUser> getAuthenticatedUser();

    Optional<AuthenticatedUser> find(String username);

    Optional<AuthenticatedUser> authenticate(String username, String password);

    void loadToContext(AuthenticatedUser authUser);
}
