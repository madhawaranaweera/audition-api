package com.audition.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.AuditionApplication;
import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(WireMockExtension.class)
@SpringBootTest(classes = AuditionApplication.class)
@AutoConfigureObservability
class AuditionIntegrationPositiveTest {

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
    void testGetPostsWithUserIds() {
        final List<String> userIds = Arrays.asList("1", "2");
        final String responseBody = """
            [{
                "userId": 1, "id": 1, "title": "Post 1", "body": "Body 1"},
            {
                "userId": 2, "id": 2, "title": "Post 2", "body": "Body 2"
            }]
            """;

        stubFor(get(urlEqualTo("/posts?userId=1&userId=2"))
            .willReturn(aResponse()
                .withHeader(TYPE, JSON)
                .withBody(responseBody)));

        final List<AuditionPost> actualPosts = auditionIntegrationClient.getPosts(userIds);

        assertEquals(2, actualPosts.size());
        assertEquals(1, actualPosts.get(0).getUserId());
        assertEquals("Post 1", actualPosts.get(0).getTitle());
        assertEquals("Body 1", actualPosts.get(0).getBody());
        assertEquals(2, actualPosts.get(1).getUserId());
        assertEquals("Post 2", actualPosts.get(1).getTitle());
        assertEquals("Body 2", actualPosts.get(1).getBody());
    }

    @Test
    void testGetPostsNoUserIds() {
        final String responseBody = """
            [{
                "userId": 1, "id": 1, "title": "Post 1", "body": "Body 1"
            },
            {
                "userId": 2, "id": 2, "title": "Post 2", "body": "Body 2"
            }]
            """;

        stubFor(get(urlEqualTo("/posts"))
            .willReturn(aResponse()
                .withHeader(TYPE, JSON)
                .withBody(responseBody)));

        final List<AuditionPost> actualPosts = auditionIntegrationClient.getPosts(new ArrayList<>());

        assertEquals(2, actualPosts.size());
        assertEquals(1, actualPosts.get(0).getUserId());
        assertEquals("Post 1", actualPosts.get(0).getTitle());
        assertEquals("Body 1", actualPosts.get(0).getBody());
        assertEquals(2, actualPosts.get(1).getUserId());
        assertEquals("Post 2", actualPosts.get(1).getTitle());
        assertEquals("Body 2", actualPosts.get(1).getBody());
    }

    @Test
    void testGetPostByIdSuccess() {
        final String postId = "1";
        final String responseBody = """
            {
                "userId": 1, 
                "id": 1, 
                "title": "Post Title", 
                "body": "Post Body"
            }
            """;

        stubFor(get(urlEqualTo(POST + postId))
            .willReturn(aResponse()
                .withHeader(TYPE, JSON)
                .withBody(responseBody)));

        final AuditionPost actualPost = auditionIntegrationClient.getPostById(postId);

        assertNotNull(actualPost);
        assertEquals(1, actualPost.getId());
        assertEquals("Post Title", actualPost.getTitle());
        assertEquals("Post Body", actualPost.getBody());
    }

    @Test
    void testGetCommentsByPostIdSuccess() {
        final String postId = "1";
        final String responseBody = """
            [
                {"postId": 1, "id": 1, "name": "Commenter 1", "email": "commenter1@example.com", "body": "Comment 1"},
                {"postId": 1, "id": 2, "name": "Commenter 2", "email": "commenter2@example.com", "body": "Comment 2"}
            ]
            """;

        stubFor(get(urlEqualTo(POST + postId + COMMENT))
            .willReturn(aResponse()
                .withHeader(TYPE, JSON)
                .withBody(responseBody)));

        final List<AuditionComment> actualComments = auditionIntegrationClient.getCommentsByPostId(postId);

        assertNotNull(actualComments);
        assertEquals(2, actualComments.size());
        assertEquals("Commenter 1", actualComments.get(0).getName());
        assertEquals("Comment 1", actualComments.get(0).getBody());
        assertEquals("Commenter 2", actualComments.get(1).getName());
        assertEquals("Comment 2", actualComments.get(1).getBody());
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
    void testGetCommentsValidPostIds() {
        final List<String> postIds = Arrays.asList("1", "2", "3");
        final String responseBody = """
            [
                {"postId": 1, "id": 1, "name": "Commenter 1", "email": "commenter1@example.com", "body": "Comment 1"},
                {"postId": 2, "id": 2, "name": "Commenter 2", "email": "commenter2@example.com", "body": "Comment 2"}
            ]
            """;

        stubFor(get(urlEqualTo("/comments?postId=1&postId=2&postId=3"))
            .willReturn(aResponse()
                .withHeader(TYPE, JSON)
                .withBody(responseBody)));

        final List<AuditionComment> actualComments = auditionIntegrationClient.getComments(postIds);

        assertEquals(2, actualComments.size());
        assertEquals("Commenter 1", actualComments.get(0).getName());
        assertEquals("Comment 1", actualComments.get(0).getBody());
        assertEquals("Commenter 2", actualComments.get(1).getName());
        assertEquals("Comment 2", actualComments.get(1).getBody());
    }

    @Test
    void testGetCommentsNoPostIds() {
        final String responseBody = """
            [
                {"postId": 1, "id": 1, "name": "Commenter 1", "email": "commenter1@example.com", "body": "Comment 1"},
                {"postId": 2, "id": 2, "name": "Commenter 2", "email": "commenter2@example.com", "body": "Comment 2"}
            ]
            """;

        stubFor(get(urlEqualTo(COMMENT))
            .willReturn(aResponse()
                .withHeader(TYPE, JSON)
                .withBody(responseBody)));

        final List<AuditionComment> actualComments = auditionIntegrationClient.getComments(new ArrayList<>());

        assertEquals(2, actualComments.size());
        assertEquals(1, actualComments.get(0).getPostId());
        assertEquals("Commenter 1", actualComments.get(0).getName());
        assertEquals("Comment 1", actualComments.get(0).getBody());
        assertEquals(2, actualComments.get(1).getPostId());
        assertEquals("Commenter 2", actualComments.get(1).getName());
        assertEquals("Comment 2", actualComments.get(1).getBody());
    }
}
