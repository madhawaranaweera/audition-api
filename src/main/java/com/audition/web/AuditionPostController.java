package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.common.validator.ValidPostId;
import com.audition.common.validator.ValidUserId;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/posts")
public class AuditionPostController {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionPostController.class);
    private final AuditionService auditionService;
    private final AuditionLogger auditionLogger;

    @Operation(
        summary = "Fetch posts",
        description = "Retrieve a list of audition posts. Optionally filter by user IDs.",
        parameters = {
            @Parameter(name = "userIds", description = "List of user IDs to filter posts",
                schema = @Schema(type = "array", format = "string"))
        }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of posts",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuditionPost.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user IDs supplied")
    })
    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(
        @RequestParam(value = "userIds", required = false) @Valid @ValidUserId final List<String> userIds) {

        auditionLogger.info(LOG, "Fetching posts. Filter applied for userIds: {}",
            userIds != null && !userIds.isEmpty() ? userIds : "No filter (fetching all posts)");

        // TODO Add logic that filters response data based on the query param

        return auditionService.getPosts(userIds);
    }

    @Operation(
        summary = "Fetch post by ID",
        description = "Retrieve an audition post by its ID.",
        parameters = {
            @Parameter(name = "id", description = "ID of the post to retrieve", required = true)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the post",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuditionPost.class))),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsById(@PathVariable("id") @ValidPostId final String postId) {
        auditionLogger.info(LOG, "Fetching post with ID: {}", postId);
        return auditionService.getPostById(postId);

        // TODO Add input validation

    }

    @Operation(
        summary = "Fetch comments for a post",
        description = "Retrieve a list of comments for a specific audition post.",
        parameters = {
            @Parameter(name = "id", description = "ID of the post for which to fetch comments", required = true)
        }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of comments",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuditionComment.class))),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComment> getCommentsByPostId(
        @PathVariable("id") @ValidPostId final String postId) {
        auditionLogger.info(LOG, "Fetching comments for post ID: {}", postId);
        return auditionService.getCommentsByPostId(postId);

        // TODO Add input validation
    }
}
