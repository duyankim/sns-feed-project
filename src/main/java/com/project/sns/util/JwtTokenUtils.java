package com.project.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

//    public static boolean isValid(String token, key) {
//        String userNameByToken = getUserName(token, key);
//    }

    /**
     * userName 가져오기
     * @param token
     * @param key
     * @return userName
     */
    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class);
    }

    /**
     * 토큰의 유효기간 검증
     * @param token
     * @param key
     * @return boolean
     */
    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

    /**
     * key로 token 파싱해 Claims 추출하여 Body 반환
     * @param token
     * @param key
     * @return Claims
     */
    private static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder().setSigningKey(getKey(key))
                .build().parseClaimsJws(token).getBody();
    }

    /**
     * 토큰 생성
     * @param userName
     * @param key
     * @param expiredTimeMs
     * @return token
     */
    public static String generateToken(String userName, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key) ,SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Key 생성
     * @param key
     * @return Key
     */
    public static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
