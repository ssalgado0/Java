package edu.uoc.epcsd.productcatalog.infrastructure.security.filter;

import edu.uoc.epcsd.productcatalog.infrastructure.security.jwt.JwtTokenProvider;
import edu.uoc.epcsd.productcatalog.infrastructure.security.model.CurrentUser;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);

    var parsed = tokenProvider.validateToken(token);
    Claims claims = parsed.getPayload();

    CurrentUser currentUser = new CurrentUser(
        claims.getId(),
        claims.getSubject(),
        claims.get("fullName", String.class),
        claims.get("role", String.class)
    );

    var authentication = new UsernamePasswordAuthenticationToken(
        currentUser,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.getRole()))
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}