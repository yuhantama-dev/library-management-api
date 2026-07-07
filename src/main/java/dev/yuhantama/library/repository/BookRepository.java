package dev.yuhantama.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.yuhantama.library.entity.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoryId(Long categoryId);
    List<Book> findByTitleContainingIgnoreCase(String title);
}
