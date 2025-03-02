package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.ReviewRequest;
import com.vinhSeo.BookingCinema.dto.response.*;
import com.vinhSeo.BookingCinema.mapper.ReviewMapper;
import com.vinhSeo.BookingCinema.model.Review;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.service.ReviewService;
import com.vinhSeo.BookingCinema.utils.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/review")
@Validated
@Tag(name = "Review Controller")
@Slf4j(topic = "REVIEW_CONTROLLER")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    private static final String PAGE_SIZE = "10";
    private static final String PAGE_NUMBER = "1";

    @Operation(method = "POST", summary = "Create new review",
            description = "Send a request via this API to create new review")
    @PostMapping()
    public ResponseEntity<?> createReview(@AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        log.info("Create new review");

        Review review = reviewService.createReview(user.getId(), reviewRequest);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("Review created successfully")
                .data(reviewResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get review by Id",
            description = "Send a request via this API to get review by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCinemaById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get review by Id");;

        Review review = reviewService.getReviewById(id);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get review by Id successfully")
                .data(reviewResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all reviews by user",
            description = "Send a request via this API to get all reviews by user.")
    @GetMapping("/list-by-user")
    public ResponseEntity<?> getAllByUser(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        log.info("Get all reviews by user");

        Page<Review> reviewPage = reviewService.getAllByUser(user.getId(), number-1, size, sortBy, order);

        List<Review> reviewList = reviewPage.getContent();

        List<ReviewResponse> reviewResponses = reviewList.stream().map(reviewMapper::toReviewResponse).toList();

        PageInfo pageInfo = null;
        if (reviewPage != null) {
            pageInfo = PageInfo.builder()
                    .pageSize(reviewPage.getSize())
                    .pageNumber(reviewPage.getNumber() + 1)
                    .totalPages(reviewPage.getTotalPages())
                    .totalElements(reviewPage.getNumberOfElements())
                    .build();
        }

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all reviews by user successfully")
                .data(reviewResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get all reviews by movie",
            description = "Send a request via this API to get all reviews by movie.")
    @GetMapping("/list-by-movie")
    public ResponseEntity<?> getAllByMovie(
            @RequestParam(name = "movieId") Integer movieId,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        log.info("Get all reviews by movie");

        Page<Review> reviewPage = reviewService.getAllByMovie(movieId, number-1, size, sortBy, order);

        List<Review> reviewList = reviewPage.getContent();

        List<ReviewResponse> reviewResponses = reviewList.stream().map(reviewMapper::toReviewResponse).toList();

        PageInfo pageInfo = null;
        if (reviewPage != null) {
            pageInfo = PageInfo.builder()
                    .pageSize(reviewPage.getSize())
                    .pageNumber(reviewPage.getNumber() + 1)
                    .totalPages(reviewPage.getTotalPages())
                    .totalElements(reviewPage.getNumberOfElements())
                    .build();
        }

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get all reviews by movie successfully")
                .data(reviewResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update review by Id",
            description = "Send a request via this API to update review by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@AuthenticationPrincipal User user,
                                          @PathVariable @Min(value = 1, message = "id must be greater than 0")  int id,
                                          @Valid @RequestBody ReviewRequest request // rating, comment
    ) {
        log.info("Update movie by Id: {}", id);;

        Review review = reviewService.updateReview(id, user.getId(), request);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update review successfully")
                .data(reviewResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete review by Id",
            description = "Send a request via this API to delete review by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@AuthenticationPrincipal User user,
                                         @PathVariable @Min(value = 1, message = "id must be greater than 0")  int id) throws IOException {
        log.info("Delete review by Id: {}", id);;

        reviewService.deleteReview(id, user.getId());

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete review successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }
}
