/* (C)2024 */
package com.example.exchange.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.exchange.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthenticationAspect {

  @Autowired private AuthenticationService authenticationService;

  @Around("@annotation(com.example.exchange.annotation.RequiresAuth)")
  public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    if (!authenticationService.isAuthenticated(request)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authenticated");
    }

    return joinPoint.proceed();
  }
}
