package com.example.reddit.security;

import com.example.reddit.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.time.Instant;

import static java.util.Date.from;
import static io.jsonwebtoken.Jwts.parserBuilder;

@Service
public class JwtProvider {

    private KeyStore keyStore; //used to sign jwt

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @PostConstruct //used on a method that needs to be executed after dependency injection is done to perform any initialization
    //initialize keystore
    public void init(){
        try{
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new SpringRedditException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey(){
        try{
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        }catch (UnrecoverableKeyException| NoSuchAlgorithmException| KeyStoreException e){
            throw new SpringRedditException("Exception occurred while retrieving public key from keystore");
        }
    }

    public boolean validateToken(String jwt){
//        Jwts.parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        parserBuilder().setSigningKey(getPublickey()).build().parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublickey(){
        try{
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
    }

    public String getUsernameFromJwt(String token){
//        Claims claims = Jwts.parser().setSigningKey(getPublickey()).parseClaimsJws(token).getBody();
        Claims claims = parserBuilder().setSigningKey(getPublickey())
                .build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String generateTokenWithUserName(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public Long getJwtExpirationInMillis(){
        return jwtExpirationInMillis;
    }
}