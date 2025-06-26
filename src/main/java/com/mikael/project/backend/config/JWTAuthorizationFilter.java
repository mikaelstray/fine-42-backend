package com.mikael.project.backend.config;

import com.mikael.project.backend.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

  private static final String AUTH_COOKIE_NAME   = "auth-token";
  private static final String AUTH_ENDPOINT_PATH = "/api/auth/";
  private static final String BEARER_PREFIX      = "Bearer ";

  private final JWTUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain)
          throws ServletException, IOException {

    String uri = request.getRequestURI();
    if (uri.startsWith(AUTH_ENDPOINT_PATH)) {
      log.debug("Skipping JWT filter for auth endpoint: {}", uri);
      chain.doFilter(request, response);
      return;
    }

    String token = resolveToken(request);
    if (token == null) {
      chain.doFilter(request, response);
      return;
    }

    try {
      if (jwtUtil.isTokenValid(token)) {
        String username = jwtUtil.extractUsername(token);
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken auth =
                  new UsernamePasswordAuthenticationToken(
                          userDetails, null, userDetails.getAuthorities());

          auth.setDetails(
                  new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(auth);
          log.debug("Authenticated user {} for URI {}", username, uri);
        }
      } else {
        log.debug("Invalid JWT token for URI {}", uri);
      }
    } catch (Exception ex) {
      log.warn("JWT processing failed: {}", ex.getMessage());
      // optionally clear context or let anonymous pass
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (bearer != null && bearer.startsWith(BEARER_PREFIX)) {
      return bearer.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}
