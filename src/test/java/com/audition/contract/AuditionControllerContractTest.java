package com.audition.contract;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuditionControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditionService auditionService;

    @Test
    void testGetPosts() throws Exception {
        final List<AuditionPost> mockPosts = Arrays.asList(
            new AuditionPost(1, 2, "Post Body 1", "Post Title 1"),
            new AuditionPost(2, 2, "Post Body 2", "Post Title 2")
        );

        when(auditionService.getPosts(anyList())).thenReturn(mockPosts);

        mockMvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testGetPostsInvalidUserIds() throws Exception {
        final String invalidUserId = "a";

        mockMvc.perform(get("/posts")
                .param("userIds", invalidUserId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPostsById() throws Exception {
        final String postId = "1";
        final AuditionPost mockPost = new AuditionPost(Integer.parseInt(postId), 2, "Post Body 1", "Post Title 1");

        when(auditionService.getPostById(postId)).thenReturn(mockPost);

        mockMvc.perform(get("/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testGetPostByIdInvalid() throws Exception {
        final String invalidPostId = "invalid";

        mockMvc.perform(get("/posts/{id}", invalidPostId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCommentsByPostId() throws Exception {
        final String postId = "1";
        final List<AuditionComment> mockComments = Arrays.asList(
            new AuditionComment(1, Integer.parseInt(postId), "Comment Author 1", "commenter1@example.com",
                "Comment Body 1"),
            new AuditionComment(2, Integer.parseInt(postId), "Comment Author 2", "commenter2@example.com",
                "Comment Body 2")
        );

        when(auditionService.getCommentsByPostId(postId)).thenReturn(mockComments);

        mockMvc.perform(get("/posts/{id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testGetCommentsByPostIdInvalid() throws Exception {
        final String invalidPostId = "invalid";

        mockMvc.perform(get("/posts/{id}/comments", invalidPostId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCommentsByPostIds() throws Exception {
        final List<AuditionComment> mockComments = Arrays.asList(
            new AuditionComment(1, 1, "Comment Author 1", "commenter1@example.com", "Comment Body 1"),
            new AuditionComment(2, 2, "Comment Author 2", "commenter2@example.com", "Comment Body 2")
        );

        when(auditionService.getComments(anyList())).thenReturn(mockComments);

        mockMvc.perform(get("/comments")
                .param("postIds", "1", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testGetCommentsByPostIdsInvalid() throws Exception {
        mockMvc.perform(get("/comments")
                .param("postIds", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
