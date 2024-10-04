/* (C)2024 */
package com.example.exchange.model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.example.exchange.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class AuthModels {

  public static class LoginRequest {
    private String email;
    private String password;

    // Default constructor
    public LoginRequest() {}

    // Constructor with parameters
    public LoginRequest(String email, String password) {
      this.email = email;
      this.password = password;
    }

    // Getter for email
    public String getEmail() {
      return email;
    }

    // Setter for email
    public void setEmail(String email) {
      this.email = email;
    }

    // Getter for password
    public String getPassword() {
      return password;
    }

    // Setter for password
    public void setPassword(String password) {
      this.password = password;
    }

    // Override toString() method for debugging purposes
    @Override
    public String toString() {
      return "LoginRequest{" + "email='" + email + '\'' + ", password='[PROTECTED]'" + '}';
    }
  }

  public static class SignupRequest {
    private String email;
    private String password;
    private String secret;

    // Default constructor
    public SignupRequest() {}

    // Constructor with parameters
    public SignupRequest(String email, String password, String secret) {
      this.email = email;
      this.password = password;
      this.secret = secret;
    }

    // Getter for email
    public String getEmail() {
      return email;
    }

    // Setter for email
    public void setEmail(String email) {
      this.email = email;
    }

    // Getter for password
    public String getPassword() {
      return password;
    }

    // Setter for password
    public void setPassword(String password) {
      this.password = password;
    }

    public String getSecret() {
      return secret;
    }

    // Override toString() method for debugging purposes
    @Override
    public String toString() {
      return "SignupRequest{" + "email='" + email + '\'' + ", password='[PROTECTED]'" + '}';
    }
  }

}
