/* (C)2024 */
package com.example.exchange.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exchange.model.AuthModels;
import com.example.exchange.repo.RepositoryExample;
import com.example.exchange.util.PasswordHasher;

@RestController
public class AuthService {
  private final RepositoryExample repositoryExample;
  private final PasswordHasher passwordHasher;
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  @Autowired
  public AuthService(RepositoryExample repositoryExample, PasswordHasher passwordHasher) {
    this.repositoryExample = repositoryExample;
    this.passwordHasher = passwordHasher;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody AuthModels.LoginRequest loginRequest) {
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
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");

        }
      }else{
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
      }


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
