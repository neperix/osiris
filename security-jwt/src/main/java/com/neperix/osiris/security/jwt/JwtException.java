package com.neperix.osiris.security.jwt;

class JwtException extends RuntimeException {

    JwtException(String msg) {
        super(msg);
    }

    JwtException(String msg, Exception cause) {
        super(msg, cause);
    }
}
