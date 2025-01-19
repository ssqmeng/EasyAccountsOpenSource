package com.deepblue.yd_jz.utils;

// JwtTokenUtil.java
import io.jsonwebtoken.*;

import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

public class JwtTokenUtil {

    private static final String SECRET_KEY = "y8ae0+AqxMJ7/Wtts4bxYKhj45MePbEwSOVs4ZUTVhCeFfnKQweh1Q5IOmhFlqv6VlyfO8x9jej0di7ItDI0Vw=="; // 使用强密钥
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 1小时有效期

    // 生成JWT token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 验证JWT token
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()  // 更新的解析方式
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);  // 解析 JWT 并验证签名
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 从JWT token中获取用户名
    public static String getUsernameFromToken(String token) {
        return Jwts.parser()  // 更新的解析方式
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[512 / 8]; // 512 bits / 8 = 64 bytes
        secureRandom.nextBytes(keyBytes);
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated Secret Key: " + secretKey);
    }
}

