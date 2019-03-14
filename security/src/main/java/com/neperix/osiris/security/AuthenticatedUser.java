package com.neperix.osiris.security;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;

public interface AuthenticatedUser {

    String username();

    Set<String> roles();

    @AllArgsConstructor
    final class Simple implements AuthenticatedUser {

        private final String username;
        private final Set<String> roles;

        @Override
        public String username() {
            return this.username;
        }

        @Override
        public Set<String> roles() {
            return new HashSet<>(this.roles);
        }
    }
}
