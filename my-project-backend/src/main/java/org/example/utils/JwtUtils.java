package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;


    //对传入的 JWT 进行解析和验证，若验证通过且未过期，就返回解码后的 JWT 对象；反之则返回 null。
    public DecodedJWT resolveJwt(String tokenHeader) {
        String token = convertToken(tokenHeader);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = verifier.verify(token);
            if(this.isInvalidToken(verify.getId())) return null;
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt)?null:verify;
        }catch (JWTVerificationException e) {
            return null;
        }

    }
    //让指定的 JWT 失效，把该 JWT 的 ID 存入 Redis 黑名单，剩余过期时间作为存储时长。
    public boolean invalidToken(String headerToken) {
        String token = this.convertToken(headerToken);
        if(token == null) return false;
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).build();
        try{
            DecodedJWT verify = verifier.verify(token);
            String id = verify.getId();
            return deleteToken(id,verify.getExpiresAt());
        }catch(JWTVerificationException e){return false;}

    }

    // 依据用户信息生成新的 JWT，其中包含用户 ID、用户名和权限信息。
    public String createJwt(UserDetails details , int id , String username){
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    //计算 JWT 的过期时间。
    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24 );
        return  calendar.getTime();
    }
    private String convertToken(String token)
    {
        if(token == null || !token.startsWith("Bearer ")) return null;
        return token.substring(7);
    }

    public UserDetails toUser(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("*******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }
    //使token失效，剩余时间内暂时存到redis中。
    public boolean deleteToken(String uuid,Date time) {
        if(this.isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid,"",expire, TimeUnit.MILLISECONDS);
        return  true;
    }
    //判断令牌是否已经过期
    private boolean isInvalidToken(String uuid) {
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    public Integer toId(DecodedJWT jwt)
    {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

}
