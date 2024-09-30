/* (C)2024 */
package com.example.exchange.model;

public class CookieModel {
  public String email;
  public String token;

  public CookieModel() {}

  // Constructor with fields
  public CookieModel(String email, String token) {
    this.email = email;
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
