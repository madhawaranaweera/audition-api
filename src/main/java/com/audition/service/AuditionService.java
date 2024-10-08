package com.audition.service;

import com.audition.common.logging.AuditionLogger;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.web.AuditionPostController;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.GuardLogStatement")
public class AuditionService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionPostController.class);
    private final AuditionIntegrationClient auditionIntegrationClient;
    private final AuditionLogger auditionLogger;


    public List<AuditionPost> getPosts(final List<String> userIds) {
        auditionLogger.info(LOG, "Fetching posts for user IDs: {}",
            userIds != null && !userIds.isEmpty() ? userIds : "No user IDs provided (fetching all posts)");
        return auditionIntegrationClient.getPosts(userIds);
    }

    public AuditionPost getPostById(final String postId) {
        auditionLogger.info(LOG, "Fetching post with ID: {}", postId);
        return auditionIntegrationClient.getPostById(postId);
    }

    public List<AuditionComment> getCommentsByPostId(final String postId) {
        auditionLogger.info(LOG, "Fetching comments for post ID: {}", postId);
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }

    public List<AuditionComment> getComments(final List<String> userIds) {
        auditionLogger.info(LOG, "Fetching comments for user IDs: {}",
            userIds != null && !userIds.isEmpty() ? userIds : "No user IDs provided (fetching all comments)");
        return auditionIntegrationClient.getComments(userIds);
    }
}
