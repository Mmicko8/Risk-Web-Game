package kdg.be.riskbackend.identity.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.List;
import kdg.be.riskbackend.identity.domain.user.Player;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.function.Function;

/**
 * This class is used to manage the token utilities.
 */
public class JwtTokenUtil {
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
    private static final String SIGNING_KEY = "secret";

    /**
     * validate token by checking if it has expired
     *
     * @param token the token to validate
     * @return true if the token is valid, false if not
     */
    public boolean validate(String token) {
        final String username = getUsername(token);
        return username != null;
    }

    /**
     * get username from token
     *
     * @param token the token to get the username from
     * @return username
     */
    public String getUsername(String token) {
        return getClaimFromToken(token, claims -> claims.get("username")).toString();
    }

    /**
     * get all claims from token
     *
     * @param token the token to get the claims from
     * @return claims from given token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts a claim from the token
     *
     * @param token          the token to extract the claim from
     * @param claimsResolver the claim resolver
     * @param <T>            the type of the claim
     * @return the claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims.
     * These properties are taken from the specified User object.
     *
     * @param player the player to generate the token for
     * @return the token
     */
    public String generateAccessToken(Player player) {
        Claims claims = Jwts.claims().setSubject(player.getEmail());
        claims.put("email", player.getEmail());
        claims.put("username", player.getUsername());
        claims.put("loyaltyPoints", player.getLoyaltyPoints());
        claims.put("scopes", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("https://riskybusiness.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }
}