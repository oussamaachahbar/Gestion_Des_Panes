package com.GestionPanne.GestionPanne.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTUtils {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
    private static final String SECRET_KEY = "votre_cle_secrete_tres_longue_et_complexe_ici_au_moins_64_caracteres";
    private final SecretKey key;

    public JWTUtils() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        try {
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .claim("authorities", authorities)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (JwtException e) {
            throw new JwtException("Erreur lors de la génération du token", e);
        }
    }

    public String extractUsername(String token) throws JwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) throws JwtException {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public List<String> extractAuthorities(String token) throws JwtException {
        final Claims claims = extractAllClaims(token);
        return claims.get("authorities", List.class);
    }

    public boolean validateTokenStructure(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            return false;
        } catch (MalformedJwtException e) {
            // Token malformé
            return false;
        } catch (ExpiredJwtException e) {
            // Token expiré
            return false;
        } catch (UnsupportedJwtException e) {
            // Token non supporté
            return false;
        } catch (IllegalArgumentException e) {
            // Claims vide
            return false;
        }
    }
}