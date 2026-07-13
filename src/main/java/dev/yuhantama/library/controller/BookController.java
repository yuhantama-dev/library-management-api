package dev.yuhantama.library.controller;

import dev.yuhantama.library.dto.BookRequestDTO;
import dev.yuhantama.library.dto.BookResponseDTO;
import dev.yuhantama.library.dto.PageResponse;
import dev.yuhantama.library.service.BookService;
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
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public PageResponse<BookResponseDTO> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Sort sortObj = parseSort(sort); // sort expects "field,dir" e.g., "title,desc"
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<BookResponseDTO> pageResult = bookService.getAllBooks(pageable);

        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast(),
                pageResult.isFirst());
    }

    // @GetMapping
    // public List<BookResponseDTO> getAllBooks() {
    // return bookService.getAllBooks();
    // }

    @GetMapping("/{id}")
    public BookResponseDTO getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO created = bookService.createBook(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDTO updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String sort) {
        if (sort.contains(",")) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            String direction = parts.length > 1 ? parts[1].trim() : "asc";
            return Sort.by(Sort.Direction.fromString(direction), field);
        } else {
            // default ascending
            return Sort.by(Sort.Direction.ASC, sort.trim());
        }
    }
}
