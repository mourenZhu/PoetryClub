package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.config.YamlPropertySourceFactory;
import cn.zhumouren.poetryclub.dao.UserEntityRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/16 18:57
 **/
@Component
@PropertySource(value = "classpath:privacy/jwt.yml", factory = YamlPropertySourceFactory.class)
@Slf4j
public class JwtTokenUtil {
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

    private final UserEntityRepository userEntityRepository;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    @Value("${jwt.issuer}")
    private String issuer;

    public JwtTokenUtil(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Bean
    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getUsername()))
//                .claim(SecurityConstants.TOKEN_ROLE_CLAIM.getName(), user.getRoles())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.debug("Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.debug("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            log.debug("JWT is not supported", ex);
        } catch (SignatureException ex) {
            log.debug("Signature validation failed");
        }
        return false;
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();
    }

    public boolean hasAuthorizationBearer(String authorization) {
        return !ObjectUtils.isEmpty(authorization) && authorization.startsWith("Bearer");
    }

    public String getAccessToken(String authorization) {
        return authorization.split(" ")[1].trim();
    }

    public UserEntity getUserDetails(String token) {
        return userEntityRepository.findByUsername(getUsername(token));
    }

    public String getUsername(String token) {
        String[] jwtSubject = getSubject(token).split(",");
        return jwtSubject[1];
    }
}
