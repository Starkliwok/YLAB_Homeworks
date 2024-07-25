package com.Y_LAB.homework.security;

import com.Y_LAB.homework.exception.auth.AuthorizeException;
import com.Y_LAB.homework.exception.auth.NotEnoughRightsException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.ADMIN_ROLE;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.lifeTime}")
    private long tokenLifetime;

    @Value("${jwt.issuer}")
    private String issuer;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String generateToken(String login, String role) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("login", login)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenLifetime))
                .sign(algorithm);
    }

    public void verifyTokenFromHeader(String authHeader) throws AuthorizeException {
        String token = authHeader.replace("Bearer ", "");
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(decodedJWT.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
                throw new AuthorizeException("Token is expired");
            }
        } catch (JWTVerificationException ex) {
            throw new AuthorizeException("Token does not exists");
        }
    }

    public void verifyAdminTokenFromHeader(String authHeader) throws AuthorizeException, NotEnoughRightsException {
        verifyTokenFromHeader(authHeader);
        if(!getUserRole(authHeader).equals(ADMIN_ROLE)) {
            throw new NotEnoughRightsException("Not enough rights");
        }
    }

    public String getUserLogin(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getClaim("login").asString();
    }

    private String getUserRole(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getClaim("role").asString();
    }
}
