package com.audition.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.AuditionApplication;
import com.audition.common.exception.SystemException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@ExtendWith(WireMockExtension.class)
@SpringBootTest(classes = AuditionApplication.class)
@AutoConfigureObservability
class AuditionIntegrationNegativeTest {

    private WireMockServer wireMockServer;
    private static final String TYPE = "Content-Type";
    private static final String JSON = "application/json";
    private static final String POST = "/posts/";
    private static final String COMMENT = "/comments";

    @Autowired
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        configureFor("localhost", 8081);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Test
    void testGetPostsUserNotFound() {
        stubFor(get(urlPathEqualTo("/posts"))
            .withQueryParam("userId", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader(TYPE, JSON)
                .withBody("{ \"error\": \"User not found\" }")));

        assertThrows(SystemException.class, () -> {
            auditionIntegrationClient.getPosts(Collections.singletonList("1"));
        });
    }

    @Test
    void testGetPostByIdNotFound() {
        final String postId = "999";

        stubFor(get(urlEqualTo(POST + postId))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(TYPE, JSON)
                .withBody("{\"error\": \"Not Found\"}")));

        final Exception exception = assertThrows(SystemException.class, () -> {
            auditionIntegrationClient.getPostById(postId);
        });

        assertTrue(exception.getMessage().contains("Cannot find a Post with id " + postId));
    }

    @Test
    void testGetPostByIdBadRequest() {
        final String postId = "";
        stubFor(get(urlEqualTo(POST + postId))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader(TYPE, JSON)
                .withBody("{ \"error\": \"Bad Request\" }")));

        assertThrows(SystemException.class, () -> {
            auditionIntegrationClient.getPostById(postId);
        });
    }

    @Test
    void testGetCommentsByPostIdNotFound() {
        final String postId = "999";

        stubFor(get(urlEqualTo(POST + postId + COMMENT))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(TYPE, JSON)
                .withBody("{\"error\": \"Not Found\"}")));

        final Exception exception = assertThrows(SystemException.class, () -> {
            auditionIntegrationClient.getCommentsByPostId(postId);
        });

        assertTrue(exception.getMessage().contains("Cannot find a Post with id " + postId));
    }

    @Test
    void testGetCommentsByPostIdBadRequest() {
        final String postId = "";
        stubFor(get(urlEqualTo(POST + postId + COMMENT))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader(TYPE, JSON)
                .withBody("{ \"error\": \"Bad Request\" }")));

        assertThrows(SystemException.class, () -> {
            auditionIntegrationClient.getCommentsByPostId(postId);
        });
    }

    @Test
    void testGetCommentsNotFound() {
        final List<String> postIds = Collections.singletonList("1");
        stubFor(get(urlPathEqualTo(COMMENT))
            .withQueryParam("postId", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader(TYPE, JSON)
                .withBody("{ \"error\": \"Not Found\" }")));

        assertThrows(SystemException.class, () -> {
            auditionIntegrationClient.getComments(postIds);
        });
    }
}
