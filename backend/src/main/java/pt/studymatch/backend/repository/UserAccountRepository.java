package pt.studymatch.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.backend.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}