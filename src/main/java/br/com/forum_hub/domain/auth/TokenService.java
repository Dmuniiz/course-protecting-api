package br.com.forum_hub.domain.auth;

import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public static final String ISSUER = "Forum Hub";

    public String generateTokenJWT(Usuario usuario) {
        // Access Token: curto (ex: 30 min), usa username ou ID
        return createToken(usuario.getUsername(), (Integer)30, "ACCESS");
    }

    public String generateRefreshToken(Usuario usuario) {
        // Refresh Token: longo (ex: 120 min), usa ID
        return createToken(usuario.getId().toString(), (Integer) 120, "REFRESH");
    }

    private String createToken(String subject, Integer minutes, String type) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(subject)
                    .withClaim("type", type) // Diferencia o propósito do token
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(dateExpires(minutes))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RegraDeNegocioException("Erro ao gerar token " + type);
        }
    }

    public String getSubjectUser(String token){
        DecodedJWT decodedJWT;

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();

        }catch (JWTCreationException ex){
            throw new JWTCreationException("Failed to verify user",ex);
        }
    }

    private Instant dateExpires(Integer minutes) {
        return LocalDateTime.now()
                .plusHours(minutes)
                .toInstant(ZoneOffset.of("-03:00"));
    }

}
