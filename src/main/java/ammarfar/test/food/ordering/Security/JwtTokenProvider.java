package ammarfar.test.food.ordering.Security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

  private static final String JWT_SECRET = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret";
  private static final long JWT_EXPIRATION_MS = 604800000L; // 7 days

  private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

  public String generateToken(String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

    return Jwts.builder()
        .subject(email)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  public String getEmailFromJwt(String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser()
          .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
          .build()
          .parseSignedClaims(authToken);
      return true;
    } catch (JwtException | IllegalArgumentException ex) {
      // Invalid token
    }
    return false;
  }
}
