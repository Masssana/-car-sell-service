package ru.college.carmarketplace.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
public enum Role {
  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String authority;

  Role(String authority) {
    this.authority = authority;
  }

  public List<GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(authority));
  }
}