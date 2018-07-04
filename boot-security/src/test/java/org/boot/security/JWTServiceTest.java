package org.boot.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luoliang
 * @date 2018/7/3
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JWTServiceTest {
    private static Logger log = LoggerFactory.getLogger(JWTServiceTest.class);
    private String key = "sign";

    @Test
    public void generateJwt() {
        String compactJwts = Jwts.builder()
                .setSubject("Liang Luo")
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        log.debug("----------> generate jwt：{}", compactJwts);
        Assert.assertNotNull(compactJwts);
    }

    @Test
    public void verifyJwt() {
        String compactJwts = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMaWFuZyBMdW8ifQ.dzp90qCj_SBSvNVlGeUwQqICIUi3dz4zvsbvFg_KJAM";
        Assert.assertEquals(Jwts.parser().setSigningKey(key).parseClaimsJws(compactJwts).getBody().getSubject(), "Liang Luo");
    }
}
