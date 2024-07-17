package pl.kamilagronska.recipes_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.entity.User;

import javax.crypto.SecretKey;
import javax.security.auth.kerberos.EncryptionKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY ="GindXWILKLSjOlvmxQSNDfl6OKb9twSM";
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public String generateToken(User user){
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*24))
                .signWith(getKey())
                .compact();
        return token;
    }

    public boolean isTokenValid(String token, UserDetails user){
        String username  =  extractUsername(token);
        return username.equals(user.getUsername());

    }

    //pojedyncze Claims
    public <T> T extractClaim(String token, Function<Claims,T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    //wszytskie claims
    public Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);

    }


}
