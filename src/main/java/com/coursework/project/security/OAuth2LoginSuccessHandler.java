package com.coursework.project.security;

import com.coursework.project.dto.AuthResponseDTO;
import com.coursework.project.entity.User;
import com.coursework.project.repository.UserRepository;
import com.coursework.project.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        if ("google".equals(oAuth2User.getAttribute("login"))) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setName(name);
                        return userRepository.save(newUser);
                    });

            String token = jwtTokenProvider.generateToken(user);

            AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                    .token(token)
                    .userId(user.getId())
                    .build();

            response.sendRedirect(frontendUrl + "/login?token=" + token + "&userId=" + user.getId());
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}

