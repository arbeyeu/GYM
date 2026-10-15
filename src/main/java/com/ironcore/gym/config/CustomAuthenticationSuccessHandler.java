package com.ironcore.gym.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            } else if (role.equals("ROLE_TRAINER")) {
                response.sendRedirect(request.getContextPath() + "/trainer/dashboard");
                return;
            } else if (role.equals("ROLE_MEMBER")) {
                response.sendRedirect(request.getContextPath() + "/member/dashboard");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
