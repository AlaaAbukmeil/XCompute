/* (C)2024 */
package com.example.exchange.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Value;

public class PasswordHasher {

  private static final int ITERATIONS = 65536;
  private static final int KEY_LENGTH = 128;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

  @Value("${password.sigup.secret}")
  private String sigupSecret;

  private final String secret;

  public PasswordHasher(String secret) {
    this.secret = secret;
  }

  public String hashPassword(String password) {
    try {
      byte[] salt = secret.getBytes();
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
      SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] hash = skf.generateSecret(spec).getEncoded();
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException("Error hashing password", e);
    }
  }

  public Boolean verifyPassword(String password, String storedHash) {
    String newHash = hashPassword(password);
    return newHash.equals(storedHash);
  }

  public Boolean checkSecret(String secretInput) {
    return secretInput.equals(sigupSecret); // Use .equals() for string comparison
  }
}
