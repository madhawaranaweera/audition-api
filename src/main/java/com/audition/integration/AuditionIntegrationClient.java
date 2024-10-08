package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.web.AuditionPostController;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.GuardLogStatement")
public class AuditionIntegrationClient {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionPostController.class);
    private final AuditionLogger auditionLogger;
    private final RestTemplate restTemplate;

    @Value("${audition.urls.posts}")
    private String postsUrl;

    @Value("${audition.urls.comments}")
    private String commentsUrl;

    @CircuitBreaker(name = "getPosts")
    public List<AuditionPost> getPosts(final List<String> userIds) {
        String urlWithParams;
        try {
            auditionLogger.info(LOG, "Fetching posts for user IDs: {}",
                userIds != null && !userIds.isEmpty() ? userIds : "No user IDs provided (fetching all posts)");

            if (userIds == null || userIds.isEmpty()) {
                urlWithParams = postsUrl;
            } else {
                urlWithParams = UriComponentsBuilder
                    .fromHttpUrl(postsUrl)
                    .queryParam("userId", userIds.toArray())
                    .toUriString();
            }

            auditionLogger.debug(LOG, "Making request to get post api: {}", urlWithParams);

            final AuditionPost[] posts = restTemplate.getForObject(urlWithParams, AuditionPost[].class);
            return posts != null ? List.of(posts) : new ArrayList<>();
        } catch (HttpClientErrorException e) {
            throw new SystemException("Error fetching posts for user IDs " + userIds, e.getMessage(),
                e.getRawStatusCode(), e);
        }
    }

    @CircuitBreaker(name = "getPostById")
    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        try {
            auditionLogger.info(LOG, "Fetching post with ID: {}", id);

            final String urlWithParams = UriComponentsBuilder
                .fromHttpUrl(postsUrl)
                .pathSegment("{id}")
                .buildAndExpand(id)
                .toUriString();

            auditionLogger.debug(LOG, "Making request to get posts by id api: {}", urlWithParams);

            return restTemplate.getForObject(urlWithParams, AuditionPost.class);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404, e);
            } else {
                throw new SystemException("Error fetching post with ID " + id + ": " + e.getMessage(),
                    e.getStatusText(), e.getRawStatusCode(), e);
            }
        }
    }

    @CircuitBreaker(name = "getCommentsByPostId")
    public List<AuditionComment> getCommentsByPostId(final String id) {
        try {
            auditionLogger.info(LOG, "Fetching comments for post ID: {}", id);

            final String urlWithParams = UriComponentsBuilder
                .fromHttpUrl(postsUrl)
                .pathSegment("{id}", "comments")
                .buildAndExpand(id)
                .toUriString();

            auditionLogger.debug(LOG, "Making request to get comments by id: {}", urlWithParams);

            final AuditionComment[] posts = restTemplate.getForObject(urlWithParams, AuditionComment[].class, id);
            return posts != null ? List.of(posts) : new ArrayList<>();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404, e);
            } else {
                throw new SystemException("Error fetching post with id " + id + ": " + e.getMessage(),
                    e.getStatusText(), e.getRawStatusCode(), e);
            }
        }
    }

    @CircuitBreaker(name = "getComments")
    public List<AuditionComment> getComments(final List<String> postIds) {
        String urlWithParams;
        try {
            auditionLogger.info(LOG, "Fetching comments for post IDs: {}",
                postIds != null && !postIds.isEmpty() ? postIds : "No post IDs provided (fetching all comments)");

            if (postIds == null || postIds.isEmpty()) {
                urlWithParams = commentsUrl;
            } else {
                urlWithParams = UriComponentsBuilder
                    .fromHttpUrl(commentsUrl)
                    .queryParam("postId", postIds.toArray())
                    .toUriString();
            }
            auditionLogger.debug(LOG, "Making request to get comments: {}", urlWithParams);
            final AuditionComment[] commentsArray = restTemplate.getForObject(urlWithParams, AuditionComment[].class);

            return commentsArray != null ? List.of(commentsArray) : new ArrayList<>();
        } catch (HttpClientErrorException e) {
            throw new SystemException("Error fetching comments for postIds " + postIds, e.getMessage(),
                e.getRawStatusCode(), e);
        }
    }
}