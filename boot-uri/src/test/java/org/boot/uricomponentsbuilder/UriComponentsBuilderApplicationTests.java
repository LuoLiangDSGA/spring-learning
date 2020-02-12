package org.boot.uricomponentsbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
class UriComponentsBuilderApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void constructUri() {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.github.com").path("/constructing-uri")
                .queryParam("name", "tom")
                .build();

        assertEquals("/constructing-uri", uriComponents.getPath());
        assertEquals("name=tom", uriComponents.getQuery());
        assertEquals("/constructing-uri", uriComponents.toUriString());
    }

    @Test
    public void constructUriEncoded() {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.github.com").path("/constructing uri").build().encode();

        assertEquals("/constructing%20uri", uriComponents.getPath());
    }

    @Test
    public void constructUriFromTemplate() {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.github.com").path("/{path-name}")
                .query("name={keyword}")
                .buildAndExpand("constructing-uri", "tomcat");

        assertEquals("/constructing-uri", uriComponents.getPath());
    }

    @Test
    public void fromUriString() {
        UriComponents result = UriComponentsBuilder
                .fromUriString("https://www.github.com/constructing-uri?name=tomcat").build();
        MultiValueMap<String, String> expectedQueryParams = new LinkedMultiValueMap<>(1);
        expectedQueryParams.add("name", "tomcat");
        assertEquals(result.getQueryParams(), expectedQueryParams);
    }
}
