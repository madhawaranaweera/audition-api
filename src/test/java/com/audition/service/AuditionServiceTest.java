package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("PMD.UnusedPrivateField")
class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @Mock
    private AuditionLogger auditionLogger;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPostsSuccess() {
        final List<String> userIds = Arrays.asList("1", "2", "3");
        final List<AuditionPost> expectedPosts = Arrays.asList(new AuditionPost(), new AuditionPost());
        when(auditionIntegrationClient.getPosts(userIds)).thenReturn(expectedPosts);

        final List<AuditionPost> actualPosts = auditionService.getPosts(userIds);

        assertEquals(expectedPosts, actualPosts);
        verify(auditionIntegrationClient).getPosts(userIds);
    }


    @Test
    void testGetPostsNoUserIds() {
        final List<String> userIds = Collections.emptyList();
        final List<AuditionPost> expectedPosts = Arrays.asList(new AuditionPost(), new AuditionPost());
        when(auditionIntegrationClient.getPosts(userIds)).thenReturn(expectedPosts);

        final List<AuditionPost> actualPosts = auditionService.getPosts(userIds);

        assertEquals(expectedPosts, actualPosts);
        verify(auditionIntegrationClient).getPosts(userIds);
    }

    @Test
    void testGetPostByIdSuccess() {
        final String postId = "1";
        final AuditionPost expectedPost = new AuditionPost();
        when(auditionIntegrationClient.getPostById(postId)).thenReturn(expectedPost);

        final AuditionPost actualPost = auditionService.getPostById(postId);

        assertEquals(expectedPost, actualPost);
        verify(auditionIntegrationClient).getPostById(postId);
    }

    @Test
    void testGetCommentsByPostIdSuccess() {
        final String postId = "1";
        final List<AuditionComment> expectedComments = Arrays.asList(new AuditionComment(), new AuditionComment());
        when(auditionIntegrationClient.getCommentsByPostId(postId)).thenReturn(expectedComments);

        final List<AuditionComment> actualComments = auditionService.getCommentsByPostId(postId);

        assertEquals(expectedComments, actualComments);
        verify(auditionIntegrationClient).getCommentsByPostId(postId);
    }

    @Test
    void testGetCommentsSuccess() {
        final List<String> userIds = Arrays.asList("1", "2", "3");
        final List<AuditionComment> expectedComments = Arrays.asList(new AuditionComment(),
            new AuditionComment());
        when(auditionIntegrationClient.getComments(userIds)).thenReturn(expectedComments);

        final List<AuditionComment> actualComments = auditionService.getComments(userIds);

        assertEquals(expectedComments, actualComments);
        verify(auditionIntegrationClient).getComments(userIds);
    }

    @Test
    void testGetCommentsNoUserIdsProvided() {
        final List<String> userIds = Collections.emptyList();
        final List<AuditionComment> expectedComments = Arrays.asList(new AuditionComment(),
            new AuditionComment());
        when(auditionIntegrationClient.getComments(userIds)).thenReturn(expectedComments);

        final List<AuditionComment> actualComments = auditionService.getComments(userIds);

        assertEquals(expectedComments, actualComments);
        verify(auditionIntegrationClient).getComments(userIds);
    }
}
