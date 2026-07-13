package dev.yuhantama.library.controller;

import dev.yuhantama.library.dto.PageResponse;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    // @GetMapping("/book/{bookId}")
    // public List<ReviewResponseDTO> getReviewsByBook(@PathVariable Long bookId) {
    //     return reviewService.getReviewsByBook(bookId);
    // }

    @GetMapping("/book/{bookId}")
    public PageResponse<ReviewResponseDTO> getReviewsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<ReviewResponseDTO> pageResult = reviewService.getReviewsByBook(bookId, pageable);

        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast(),
                pageResult.isFirst());
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

    // Same helper – you can extract it to a utility class, but for now keep it
    // local.
    private Sort parseSort(String sort) {
        if (sort.contains(",")) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            String direction = parts.length > 1 ? parts[1].trim() : "asc";
            return Sort.by(Sort.Direction.fromString(direction), field);
        } else {
            return Sort.by(Sort.Direction.ASC, sort.trim());
        }
    }
}
