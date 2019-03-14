package com.neperix.osiris.security.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.neperix.osiris.security.AuthenticatedUser;
import com.neperix.osiris.security.SecurityService;

public class MockSecurityService implements SecurityService {

    private Set<String> allowedUsernames;

    MockSecurityService(Collection<String> allowedUsernames) {
        this.allowedUsernames = new HashSet<>(allowedUsernames);
    }

    @Override
    public Optional<AuthenticatedUser> getAuthenticatedUser() {
        return null;
    }

    @Override
    public Optional<AuthenticatedUser> find(String username) {
        if (this.allowedUsernames.contains(username)) {
            return Optional.of(new AuthenticatedUser.Simple(username, Collections.emptySet()));
        }

        return Optional.empty();
    }

    @Override
    public Optional<AuthenticatedUser> authenticate(String username, String password) {
        return find(username);
    }

    @Override
    public void loadToContext(AuthenticatedUser authUser) {

    }
}
