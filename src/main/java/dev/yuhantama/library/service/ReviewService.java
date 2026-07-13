package dev.yuhantama.library.service;

import dev.yuhantama.library.dto.ReviewRequestDTO;
import dev.yuhantama.library.dto.ReviewResponseDTO;
import dev.yuhantama.library.entity.Book;
import dev.yuhantama.library.entity.Review;
import dev.yuhantama.library.entity.User;
import dev.yuhantama.library.exception.ResourceNotFoundException;
import dev.yuhantama.library.repository.BookRepository;
import dev.yuhantama.library.repository.ReviewRepository;
import dev.yuhantama.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

     public Page<ReviewResponseDTO> getReviewsByBook(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }
        Page<Review> reviewPage = reviewRepository.findByBookId(bookId, pageable); // we need to update repository
        List<ReviewResponseDTO> content = reviewPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, reviewPage.getTotalElements());
    }

    // Helper to get current logged-in user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        User currentUser = getCurrentUser();

        // Check if user already reviewed this book
        List<Review> existing = reviewRepository.findByBookId(book.getId());
        if (existing.stream().anyMatch(r -> r.getUser().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("You have already reviewed this book");
        }

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(currentUser);
        review.setBook(book);

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    public List<ReviewResponseDTO> getReviewsByBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }
        return reviewRepository.findByBookId(bookId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponseDTO> getReviewsByCurrentUser() {
        User currentUser = getCurrentUser();
        return reviewRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        User currentUser = getCurrentUser();
        // Check ownership: only the owner or an admin can update
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You are not allowed to update this review");
        }

        // Only allow updating rating and comment; book cannot be changed
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        User currentUser = getCurrentUser();
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You are not allowed to delete this review");
        }

        reviewRepository.delete(review);
    }

    // Mapper
    private ReviewResponseDTO mapToResponse(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getName(),
                review.getUser().getEmail(),
                review.getBook().getId(),
                review.getBook().getTitle(),
                review.getCreatedAt());
    }
}
