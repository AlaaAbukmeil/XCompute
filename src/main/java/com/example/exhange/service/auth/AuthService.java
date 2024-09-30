/* (C)2024 */
package com.example.exchange.service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.model.AuthModels;
import com.example.exchange.model.CookieModel;
import com.example.exchange.repo.RepositoryExample;
import com.example.exchange.util.JwtUtil;
import com.example.exchange.util.PasswordHasher;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthService {
  private final RepositoryExample repositoryExample;
  private final PasswordHasher passwordHasher;
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final ObjectMapper objectMapper;
  private final JwtUtil jwtUtil;

  @Autowired
  public AuthService(
      RepositoryExample repositoryExample,
      PasswordHasher passwordHasher,
      ObjectMapper objectMapper,
      JwtUtil jwtUtil) {
    this.repositoryExample = repositoryExample;
    this.passwordHasher = passwordHasher;
    this.objectMapper = objectMapper;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(
      @RequestBody AuthModels.LoginRequest loginRequest, HttpServletResponse response) {
    try {
      String email = loginRequest.getEmail();
      String password = loginRequest.getPassword();

      String sql = "SELECT * FROM users WHERE email = ?";
      List<Map<String, Object>> result = repositoryExample.queryForList(sql, email);

      if (!result.isEmpty()) {
        Map<String, Object> user = result.get(0);
        String storedHash = (String) user.get("password");

        Boolean resultPassword = passwordHasher.verifyPassword(password, storedHash);
        if (resultPassword) {
          String newToken = jwtUtil.generateToken(email);
          CookieModel cookieContent = new CookieModel(email, newToken);
          String cookieContentJson = objectMapper.writeValueAsString(cookieContent);
          String encodedCookieValue = URLEncoder.encode(cookieContentJson, StandardCharsets.UTF_8);
          Cookie cookie = new Cookie("XCompute", encodedCookieValue);
          cookie.setMaxAge(14 * 24 * 60 * 60);
          cookie.setSecure(true);
          cookie.setHttpOnly(true);
          response.addCookie(cookie);
          return ResponseEntity.status(HttpStatus.OK).body("Success");
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
      }

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/auth")
  public ResponseEntity<String> validateCookie(HttpServletRequest request) {
    try {
      // 1. Extract the cookie
      Cookie[] cookies = request.getCookies();
      if (cookies == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookies found");
      }

      Cookie xComputeCookie = null;
      for (Cookie cookie : cookies) {
        if ("XCompute".equals(cookie.getName())) {
          xComputeCookie = cookie;
          break;
        }
      }

      if (xComputeCookie == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("XCompute cookie not found");
      }

      // 2. Decode the cookie value
      String decodedValue = URLDecoder.decode(xComputeCookie.getValue(), StandardCharsets.UTF_8);

      // 3. Parse the JSON content
      CookieModel cookieContent = objectMapper.readValue(decodedValue, CookieModel.class);

      // 4. Validate the token
      if (jwtUtil.validateToken(cookieContent.getToken())) {
        return ResponseEntity.status(HttpStatus.OK).body("Cookie is valid");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
      }

    } catch (JwtException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("JWT validation failed: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred: " + e.getMessage());
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody AuthModels.SignupRequest signupRequest) {
    try {
      String email = signupRequest.getEmail();
      String password = signupRequest.getPassword();
      String secret = signupRequest.getSecret();
      Boolean resultSecret = passwordHasher.checkSecret(secret);
      logger.warn("This is a Secret message " + resultSecret);

      if (!resultSecret) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Secret is Wrong");
      }

      String sql = "SELECT * FROM users WHERE email = ?";
      List<Map<String, Object>> result = repositoryExample.queryForList(sql, email);

      if (!result.isEmpty()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
      }

      String hashedPassword = passwordHasher.hashPassword(password);
      int resultInsert =
          repositoryExample.update(
              "INSERT INTO users (email, password) VALUES (?, ?)", email, hashedPassword);

      if (resultInsert == 0) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
      } else {
        return ResponseEntity.status(HttpStatus.CREATED).body("New User");
      }

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred: " + e.getMessage());
    }
  }
}
