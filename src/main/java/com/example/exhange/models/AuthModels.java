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

  public static int validateCookie(
      HttpServletRequest request, ObjectMapper objectMapper, JwtUtil jwtUtil) {
    try {
      // 1. Extract the cookie
      Cookie[] cookies = request.getCookies();
      if (cookies == null) {
        return 401;
      }

      Cookie xComputeCookie = null;
      for (Cookie cookie : cookies) {
        if ("XCompute".equals(cookie.getName())) {
          xComputeCookie = cookie;
          break;
        }
      }

      if (xComputeCookie == null) {
        return 401;
      }

      // 2. Decode the cookie value
      String decodedValue = URLDecoder.decode(xComputeCookie.getValue(), StandardCharsets.UTF_8);

      // 3. Parse the JSON content
      CookieModel cookieContent = objectMapper.readValue(decodedValue, CookieModel.class);

      // 4. Validate the token
      if (jwtUtil.validateToken(cookieContent.getToken())) {
        return 200;
      } else {
        return 401;
      }

    } catch (JwtException e) {
      return 404;
    } catch (Exception e) {
      return 404;
    }
  }
}
