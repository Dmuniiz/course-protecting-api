package br.com.forum_hub.infra.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityJwtFilter securityJwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/auth/login", "/auth/refresh", "/register/user", "/register/verificar-conta", "/register/{nomeUsuario}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cursos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/topicos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/topicos").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.PUT, "/topicos").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.DELETE, "/topicos/**").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.PATCH, "/topicos/**").hasRole("MODERADOR")
                        .requestMatchers(HttpMethod.PATCH, "/topicos/{idTopico}/respostas/**").hasAnyRole("INSTRUTOR", "ESTUDANTE")
                        .requestMatchers(HttpMethod.PATCH, "/adicionar-perfil/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/reativar-conta/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){

        //creating a profile description by string

        /*return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("MODERADOR")
                .role("MODERADOR").implies("ESTUDANTE", "INSTRUTOR")
                .build();*/

        String hierarchy = "ROLE_ADMIN > ROLE_MODERADOR\n"+
                "ROLE_MODERADOR > ROLE_INSTRUTOR\n"+
                "ROLE_MODERADOR > ROLE_ESTUDANTE";

       return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

}
