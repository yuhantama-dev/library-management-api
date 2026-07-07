package dev.yuhantama.library.controller;

import dev.yuhantama.library.dto.ReviewRequestDTO;
import dev.yuhantama.library.dto.ReviewResponseDTO;
import dev.yuhantama.library.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO request) {
        ReviewResponseDTO created = reviewService.createReview(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/book/{bookId}")
    public List<ReviewResponseDTO> getReviewsByBook(@PathVariable Long bookId) {
        return reviewService.getReviewsByBook(bookId);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public List<ReviewResponseDTO> getMyReviews() {
        return reviewService.getReviewsByCurrentUser();
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ReviewResponseDTO updateReview(@PathVariable Long reviewId, @Valid @RequestBody ReviewRequestDTO request) {
        return reviewService.updateReview(reviewId, request);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
