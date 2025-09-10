package co.pragma.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    private static final String ROLE_CLAIM = "role";
    private static final String USERID_CLAIM = "userId";
    private static final String PERMISSIONS_CLAIM = "permissions";

    public JwtService(@Value("8d0ec032515e2e10331c05c99086f2a117ab8c23cd19ec71209cf77354c38870") String secret, @Value("${security.jwt.expiration-time}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token) {
        try {
            final Date expirationDate = extractClaim(token, Claims::getExpiration);
            return !expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public String getUserEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getUserIdFromToken(String token) {
        return extractClaim(token, claims -> (String) claims.get(USERID_CLAIM));
    }

    public String getRoleFromToken(String token) {
        return extractClaim(token, claims -> (String) claims.get(ROLE_CLAIM));
    }

    public Set<String> getPermissionsFromToken(String token) {
        List<String> permissionsList = extractClaim(token, claims -> (List<String>) claims.get(PERMISSIONS_CLAIM));
        return permissionsList != null ? new HashSet<>(permissionsList) : Set.of();
    }
}
