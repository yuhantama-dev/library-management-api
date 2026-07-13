package dev.yuhantama.library.config;

import dev.yuhantama.library.entity.*;
import dev.yuhantama.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Profile("!test") // don't run during tests
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Roles (always needed)
        Role userRole = getOrCreateRole("ROLE_USER");
        Role adminRole = getOrCreateRole("ROLE_ADMIN");

        // 2. Admin user (always needed)
        createAdminIfMissing(adminRole, userRole);

        // 3. Categories
        List<Category> categories = createCategoriesIfEmpty();

        // 4. Books
        List<Book> books = createBooksIfEmpty(categories);

        // 5. Regular users (5 sample users)
        List<User> users = createUsersIfEmpty(userRole);

        // 6. Reviews (randomly assign between users and books)
        createReviewsIfEmpty(users, books);

        System.out.println("✅ Sample data seeded successfully.");
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    private void createAdminIfMissing(Role adminRole, Role userRole) {
        if (!userRepository.existsByEmail("admin@library.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
            System.out.println("✅ Admin created: admin@library.com / admin123");
        }
    }

    private List<Category> createCategoriesIfEmpty() {
        if (categoryRepository.count() > 0) {
            return categoryRepository.findAll();
        }

        List<Category> categories = Arrays.asList(
                new Category("Fiction", "Imaginative stories"),
                new Category("Non-Fiction", "Factual and educational"),
                new Category("Science", "Scientific knowledge"),
                new Category("History", "Historical events and biographies"),
                new Category("Fantasy", "Mythical and magical worlds"));
        return categoryRepository.saveAll(categories);
    }

    private List<Book> createBooksIfEmpty(List<Category> categories) {
        if (bookRepository.count() > 0) {
            return bookRepository.findAll();
        }

        List<Book> books = new ArrayList<>();

        // Sample books data: title, author, ISBN, publicationYear, categoryIndex
        // (0-based)
        Object[][] bookData = {
                { "The Great Gatsby", "F. Scott Fitzgerald", "0743273565", 1925, 0 },
                { "To Kill a Mockingbird", "Harper Lee", "0061120084", 1960, 0 },
                { "1984", "George Orwell", "0451524935", 1949, 0 },
                { "Pride and Prejudice", "Jane Austen", "0141439518", 1813, 0 },
                { "The Catcher in the Rye", "J.D. Salinger", "0316769488", 1951, 0 },
                { "A Brief History of Time", "Stephen Hawking", "0553380163", 1988, 2 },
                { "The Selfish Gene", "Richard Dawkins", "0199291151", 1976, 2 },
                { "Sapiens", "Yuval Noah Harari", "0062316097", 2011, 1 },
                { "Guns, Germs, and Steel", "Jared Diamond", "0393317558", 1997, 3 },
                { "The Diary of a Young Girl", "Anne Frank", "0553296983", 1947, 3 },
                { "The Hobbit", "J.R.R. Tolkien", "0547928227", 1937, 4 },
                { "The Lord of the Rings", "J.R.R. Tolkien", "0544003415", 1954, 4 },
                { "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "0439708180", 1997, 4 },
                { "The Name of the Wind", "Patrick Rothfuss", "0756404741", 2007, 4 },
                { "Dune", "Frank Herbert", "0441013593", 1965, 4 },
                { "The Alchemist", "Paulo Coelho", "0062502174", 1988, 0 },
                { "The Da Vinci Code", "Dan Brown", "0385504201", 2003, 0 },
                { "The Hunger Games", "Suzanne Collins", "0439023528", 2008, 4 },
                { "The Fault in Our Stars", "John Green", "0525478812", 2012, 0 },
                { "Educated", "Tara Westover", "0399590504", 2018, 1 }
        };

        for (Object[] data : bookData) {
            String title = (String) data[0];
            String author = (String) data[1];
            String isbn = (String) data[2];
            int year = (int) data[3];
            int catIndex = (int) data[4];
            Category category = categories.get(catIndex % categories.size());

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPublicationYear(year);
            book.setCategory(category);
            books.add(book);
        }

        return bookRepository.saveAll(books);
    }

    private List<User> createUsersIfEmpty(Role userRole) {
        if (userRepository.count() > 1) { // admin already exists
            return userRepository.findAll().stream()
                    .filter(u -> !u.getEmail().equals("admin@library.com"))
                    .toList();
        }

        List<User> users = new ArrayList<>();
        String[][] userData = {
                { "Alice", "alice@example.com", "password123" },
                { "Bob", "bob@example.com", "password123" },
                { "Charlie", "charlie@example.com", "password123" },
                { "Diana", "diana@example.com", "password123" },
                { "Eve", "eve@example.com", "password123" }
        };

        for (String[] data : userData) {
            User user = new User();
            user.setName(data[0]);
            user.setEmail(data[1]);
            user.setPassword(passwordEncoder.encode(data[2]));
            user.setRoles(Set.of(userRole));
            users.add(user);
        }

        return userRepository.saveAll(users);
    }

    private void createReviewsIfEmpty(List<User> users, List<Book> books) {
        if (reviewRepository.count() > 0) {
            return;
        }

        Random random = new Random();
        List<Review> reviews = new ArrayList<>();

        // Create at least 2 reviews per user, randomly assigned
        for (User user : users) {
            int numReviews = 2 + random.nextInt(3); // 2–4 reviews per user
            List<Book> shuffledBooks = new ArrayList<>(books);
            Collections.shuffle(shuffledBooks, random);

            for (int i = 0; i < Math.min(numReviews, shuffledBooks.size()); i++) {
                Book book = shuffledBooks.get(i);
                int rating = 1 + random.nextInt(5); // 1–5
                String[] comments = {
                        "Great book!",
                        "Could be better.",
                        "Loved it!",
                        "Interesting but slow.",
                        "Would recommend.",
                        "Not my favorite.",
                        "Amazing read!",
                        "Very informative."
                };
                String comment = comments[random.nextInt(comments.length)];

                Review review = new Review();
                review.setRating(rating);
                review.setComment(comment);
                review.setUser(user);
                review.setBook(book);
                review.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30))); // old reviews

                reviews.add(review);
            }
        }

        reviewRepository.saveAll(reviews);
        System.out.println("✅ Created " + reviews.size() + " reviews.");
    }
}