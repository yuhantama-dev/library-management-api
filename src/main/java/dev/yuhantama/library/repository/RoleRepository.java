package dev.yuhantama.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.yuhantama.library.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
