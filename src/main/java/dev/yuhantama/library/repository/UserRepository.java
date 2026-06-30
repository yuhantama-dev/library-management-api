package dev.yuhantama.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.yuhantama.library.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data will automatically implement this method based on the method name
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
