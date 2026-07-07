package dev.yuhantama.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private String userName;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime createdAt;
}
