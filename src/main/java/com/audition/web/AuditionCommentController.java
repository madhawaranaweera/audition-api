package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.common.validator.ValidPostId;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/comments")
public class AuditionCommentController {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionPostController.class);
    private final AuditionService auditionService;
    private final AuditionLogger auditionLogger;

    @Operation(summary = "Fetch Comments by Post IDs",
        description = "Retrieve a list of audition post comments. Optionally filter by post IDs.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuditionPost.class))),
        @ApiResponse(responseCode = "400", description = "Invalid post IDs provided"),
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComment> getCommentsByPostIds(
        @RequestParam(value = "postIds", required = false) @Valid @ValidPostId final List<String> postIds) {
        auditionLogger.info(LOG, "Fetching comments for post IDs: {}",
            postIds != null && !postIds.isEmpty() ? postIds : "No post IDs provided (fetching all comments)");
        return auditionService.getComments(postIds);
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/

}
