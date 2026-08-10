package joseph.com.authifyy.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import joseph.com.authifyy.entities.UserEntityWrapper;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${spring.jwt.security-key}")
    private String secretKey;

    @Value("${spring.jwt.expiration}")
    private long expiration;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getExpiration2() {
        return expiration;
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token,
                              Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractDate(token).before(new Date());
    }

    public boolean isTokenValid(String token, @NonNull UserEntityWrapper userEntityWrapper) {
        return (extractUsername(token).equals(userEntityWrapper.getUsername())
                && !isTokenExpired(token));
    }

    public String generateToken(@NonNull UserEntityWrapper userEntityWrapper) {
        HashMap<String, Object> extraClaims = new HashMap<>(Map.of(
                "roles" , userEntityWrapper.getAuthorities()
        ));

        return buildToken(extraClaims, userEntityWrapper, expiration);
    }

    private String buildToken (Map <String, Object> extraClaims,
                               @NonNull UserEntityWrapper userEntityWrapper,
                               long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userEntityWrapper.getUser().getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();

    }


}
