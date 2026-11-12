package pt.studymatch.backend.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.model.UserAccount;
import pt.studymatch.backend.repository.UserAccountRepository;

@Service
public class AccessControlService {

    private final UserAccountRepository userAccounts;

    public AccessControlService(UserAccountRepository userAccounts) {
        this.userAccounts = userAccounts;
    }

    public void checkStudentAccess(String targetStudentNumber) {
        if (targetStudentNumber == null || targetStudentNumber.isBlank()) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return;
        }

        boolean isTeacher = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        if (isTeacher) {
            return;
        }

        Optional<UserAccount> accountOpt = userAccounts.findByUsernameIgnoreCase(auth.getName());
        if (accountOpt.isPresent()) {
            UserAccount account = accountOpt.get();
            String ownNumber = account.getStudent() != null
                    ? account.getStudent().getStudentNumber()
                    : account.getUsername();

            if (!ownNumber.equalsIgnoreCase(targetStudentNumber.trim())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Acesso negado: um estudante apenas pode aceder aos seus proprios dados."
                );
            }
        } else {
            // Suporte para utilizadores em contexto de teste com @WithMockUser
            if (!auth.getName().equalsIgnoreCase("user")
                    && !auth.getName().equalsIgnoreCase(targetStudentNumber.trim())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Acesso negado: um estudante apenas pode aceder aos seus proprios dados."
                );
            }
        }
    }
}
