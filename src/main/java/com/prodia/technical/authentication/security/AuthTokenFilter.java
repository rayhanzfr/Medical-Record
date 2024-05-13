package com.prodia.technical.authentication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodia.technical.authentication.model.UserPrincipal;
import com.prodia.technical.authentication.service.UserService;
import com.prodia.technical.authentication.service.JwtService;
import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.model.response.WebResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Setter(onMethod_ = @Autowired, onParam_ = @Lazy)
  private UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final String username;
    final String userAgent = request.getHeader("User-Agent");
    final String path = request.getRequestURI().substring(request.getContextPath().length());
    if (userAgent == null) {
      this.returnError(response, "Missing User Agent");
      return;
    }

    final String ipAddress = request.getRemoteAddr();
    if (ipAddress == null) {
      this.returnError(response, "Missing IP Address");
      return;
    }

    if (path.matches(SecurityConstant.PERMITTED_URI)) {
      UserDetails userDetails =
          UserPrincipal.builder().ipAddress(ipAddress).userAgent(userAgent).build();

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } else {
      String jwt = getJwt(request);
      if (jwt != null) {
        try {
          jwtService.validateToken(jwt, userAgent);
        } catch (Exception e) {
          this.returnError(response, e.getMessage());
          e.printStackTrace();
        }
        username = jwtService.extractUserName(jwt);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(username)
            && SecurityContextHolder.getContext().getAuthentication() == null) {

          UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);
          if (jwtService.isTokenValid(jwt, userDetails)) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
          }
          filterChain.doFilter(request, response);
        } else {
          this.returnError(response, "Invalid token!");
          return;
        }
      } else {
        this.returnError(response, "Token is required!");
        return;
      }
    }
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }

  private void returnError(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    String content = "";
    String jsonStr = "";
    if (!message.equals(null)) {
      content = "Unauthorized! " + message;
      WebResponse<String> errorResponse = ResponseHelper.status(HttpStatus.FORBIDDEN, content);
      jsonStr = objectMapper.writeValueAsString(errorResponse);
    }
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(jsonStr);
    response.getWriter().flush();
    response.getWriter().close();
  }

  private String getJwt(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.replace("Bearer ", "");
    }
    return null;
  }
}