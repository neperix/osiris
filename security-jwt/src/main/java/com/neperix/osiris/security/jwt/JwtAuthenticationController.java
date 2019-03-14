package com.neperix.osiris.security.jwt;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.neperix.osiris.security.SecurityService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@RestController
public class JwtAuthenticationController {

    private final SecurityService securityService;
    private final JwtFactory jwtFactory;

    @PostMapping("/auth")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) {
        return securityService.authenticate(authRequest.getUsername(), authRequest.getPassword())
                .map(jwtFactory::generate)
                .map(token -> ResponseEntity.ok(new JwtResponse(token)))
                .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        String oldToken = request.getHeader("Authorization");
        Optional<Token> decodedOldToken = jwtFactory.decode(oldToken);
        if (decodedOldToken.isPresent()) {
            String username = decodedOldToken.get().getUsername();
            securityService.find(username)
                    .orElseThrow(() -> new JwtException("Can't refresh token: " + oldToken + "; User not found: " + username));
        }

        String refreshedToken = jwtFactory.refresh(oldToken);
        return ResponseEntity.ok(new JwtResponse(refreshedToken));
    }

    @ExceptionHandler
    public ResponseEntity<?> handle(JwtException jwtEx) {
        return new ResponseEntity<>(new ErrorResponse(jwtEx.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class ErrorResponse {

        private String message;
    }
}
