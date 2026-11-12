package pt.studymatch.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import pt.studymatch.backend.model.UserAccount;
import pt.studymatch.backend.repository.UserAccountRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccounts) {
        return username -> {
            UserAccount account = userAccounts.findByUsernameIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilizador nao encontrado."));
            return new User(
                    account.getUsername(),
                    account.getPasswordHash(),
                    account.isEnabled(),
                    true,
                    true,
                    true,
                    List.of(new SimpleGrantedAuthority(account.getRole()))
            );
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/health", "/api/auth/login").permitAll()
                .requestMatchers("/api/auth/logout", "/api/auth/me").authenticated()

                // Endpoints exclusivos de PROFESSOR (ROLE_TEACHER)
                .requestMatchers("/api/grouping-contexts/**").hasRole("TEACHER")
                .requestMatchers("/api/degree-courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/students").hasRole("TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/students").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/students/*").hasRole("TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/students/*/academic-records").hasRole("TEACHER")

                // Endpoints partilhados (com verificacao de titularidade via AccessControlService para ALUNO)
                .requestMatchers(HttpMethod.GET, "/api/students/*").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/students/*/academic-records").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/students/*/profile").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers("/api/students/*/cold-start").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers("/api/course-units/**", "/api/competencies/**", "/api/cold-start/**").hasAnyRole("TEACHER", "STUDENT")

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.setContentType("application/json");
                    res.getWriter().write("{\"status\":401,\"error\":\"Autenticacao necessaria.\"}");
                })
                .accessDeniedHandler((req, res, accessEx) -> {
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.setContentType("application/json");
                    res.getWriter().write("{\"status\":403,\"error\":\"Sem permissao para realizar esta operacao.\"}");
                })
            );

        return http.build();
    }
}
