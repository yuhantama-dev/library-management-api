package dev.yuhantama.library.config;

import dev.yuhantama.library.entity.Category;
import dev.yuhantama.library.entity.Role;
import dev.yuhantama.library.entity.User;
import dev.yuhantama.library.repository.CategoryRepository;
import dev.yuhantama.library.repository.RoleRepository;
import dev.yuhantama.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create roles if they don't exist
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // Create an admin user if not present
        if (!userRepository.existsByEmail("admin@library.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole, userRole)); // admin gets both roles
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin@library.com / admin123");
        }

        // Seed categories if they don't exist
        if (categoryRepository.count() == 0) {
            Category fiction = new Category("Fiction", "Fictional books");
            Category nonFiction = new Category("Non-Fiction", "Factual books");
            categoryRepository.saveAll(List.of(fiction, nonFiction));
            System.out.println("✅ Seeded sample categories.");
        }
    }
}