package pt.studymatch.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import pt.studymatch.backend.dto.AuthUserResponse;
import pt.studymatch.backend.dto.LoginRequest;
import pt.studymatch.backend.model.UserAccount;
import pt.studymatch.backend.repository.UserAccountRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserAccountRepository userAccounts;

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            UserAccountRepository userAccounts) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userAccounts = userAccounts;
    }

    @PostMapping("/login")
    public AuthUserResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            UserAccount account = userAccounts.findByUsernameIgnoreCase(request.username())
                    .orElse(null);

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("ROLE_STUDENT");

            String studentNumber = account != null && account.getStudent() != null
                    ? account.getStudent().getStudentNumber()
                    : null;

            return new AuthUserResponse(authentication.getName(), role, studentNumber);
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilizador ou palavra-passe incorretos.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Sessao terminada com sucesso."));
    }

    @GetMapping("/me")
    public AuthUserResponse me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessao nao iniciada.");
        }

        String username = auth.getName();
        UserAccount account = userAccounts.findByUsernameIgnoreCase(username).orElse(null);

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(account != null ? account.getRole() : "ROLE_STUDENT");

        String studentNumber = account != null && account.getStudent() != null
                ? account.getStudent().getStudentNumber()
                : null;

        return new AuthUserResponse(username, role, studentNumber);
    }
}
