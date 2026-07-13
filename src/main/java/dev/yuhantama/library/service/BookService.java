package dev.yuhantama.library.service;

import dev.yuhantama.library.dto.BookRequestDTO;
import dev.yuhantama.library.dto.BookResponseDTO;
import dev.yuhantama.library.entity.Book;
import dev.yuhantama.library.entity.Category;
import dev.yuhantama.library.exception.ResourceNotFoundException;
import dev.yuhantama.library.repository.BookRepository;
import dev.yuhantama.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        List<BookResponseDTO> content = bookPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, bookPage.getTotalElements());
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setCategory(category);

        Book saved = bookRepository.save(book);
        return mapToResponse(saved);
    }

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToResponse(book);
    }

    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setCategory(category);

        Book updated = bookRepository.save(book);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Mapper method
    private BookResponseDTO mapToResponse(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getCategory() != null ? book.getCategory().getName() : null,
                book.getCreatedAt());
    }
}
