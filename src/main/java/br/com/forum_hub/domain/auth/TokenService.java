package br.com.forum_hub.domain.auth;

import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("{api.security.token.secret}")
    private String secret;

    public static final String ISSUER = "Forum Hub";

    public String generateTokenJWT(Usuario usuario, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // Use a strong, secure secret
            String token = JWT.create()
                                    .withSubject(usuario.getUsername())
                                    .withIssuer(ISSUER)
                                    .withIssuedAt(new Date())
                                    .withExpiresAt(dateExpires()) // 1 hour expiration
                                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims
            throw new RegraDeNegocioException("Error creating JWT token");
        }
    }

    public String getSubjectUser(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            return verifier;
        }catch (JWTCreationException ex){
            throw new JWTCreationException("Failed to verify user",ex);
        }
    }

    private Instant dateExpires() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

}
