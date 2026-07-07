package dev.yuhantama.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.yuhantama.library.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
