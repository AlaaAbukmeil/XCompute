/* (C)2024 */
package com.example.exchange.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.exchange.model.CookieModel;
import com.example.exchange.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

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
}
