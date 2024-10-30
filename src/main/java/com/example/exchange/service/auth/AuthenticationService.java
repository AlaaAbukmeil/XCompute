/* (C)2024 */
package com.example.exchange.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.example.exchange.model.CookieModel;
import com.example.exchange.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  public boolean isAuthenticated(HttpServletRequest request) {
    try {
      Cookie[] cookies = request.getCookies();
      if (cookies == null) {
        return false;
      }

      Cookie xComputeCookie = null;
      for (Cookie cookie : cookies) {
        if ("XCompute".equals(cookie.getName())) {
          xComputeCookie = cookie;
          break;
        }
      }

      if (xComputeCookie == null) {
        return false;
      }

      String decodedValue = URLDecoder.decode(xComputeCookie.getValue(), StandardCharsets.UTF_8);
      CookieModel cookieContent = objectMapper.readValue(decodedValue, CookieModel.class);

      return jwtUtil.validateToken(cookieContent.getToken());

    } catch (Exception e) {
      return false;
    }
  }

  public boolean authenticateSession(WebSocketSession session) {
    try {
      List<String> cookieHeaders = session.getHandshakeHeaders().get("cookie");
      logger.debug("WebSocket cookie headers: {}", cookieHeaders); // Add this

      if (cookieHeaders == null || cookieHeaders.isEmpty()) {
        logger.debug("No cookies found in WebSocket request");
        return false;
      }

      Map<String, String> cookies = parseCookies(cookieHeaders.get(0));
      logger.debug("Parsed cookies: {}", cookies); // Add this

      String xComputeCookie = cookies.get("XCompute");
      if (xComputeCookie == null) {
        logger.debug("XCompute cookie not found");
        return false;
      }

      return validateCookie(xComputeCookie);

    } catch (Exception e) {
      logger.error("Error during WebSocket authentication", e);
      return false;
    }
  }

  private boolean validateCookie(String cookieValue) throws Exception {
    String decodedValue = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
    CookieModel cookieContent = objectMapper.readValue(decodedValue, CookieModel.class);
    return jwtUtil.validateToken(cookieContent.getToken());
  }

  private Map<String, String> parseCookies(String cookieHeader) {
    Map<String, String> cookies = new HashMap<>();
    if (cookieHeader != null && !cookieHeader.isEmpty()) {
      String[] cookiePairs = cookieHeader.split(";");
      for (String cookiePair : cookiePairs) {
        String[] keyValue = cookiePair.trim().split("=", 2);
        if (keyValue.length == 2) {
          cookies.put(keyValue[0], keyValue[1]);
        }
      }
    }
    return cookies;
  }
}
