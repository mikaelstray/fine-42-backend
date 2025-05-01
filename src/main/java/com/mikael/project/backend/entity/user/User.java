package com.mikael.project.backend.entity.user;

import com.mikael.project.backend.entity.Fine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "users")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String username;

  @Column
  private String password;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserRoles> roles = new HashSet<>();

  @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Fine> receivedFines = new ArrayList<>();

  @OneToMany(mappedBy = "giver")
  private List<Fine> givenFines = new ArrayList<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
            .toList();
  }

  /**
   * Indicates whether the user's account has expired.
   * Always returns {@code true}, meaning accounts do not expire.
   *
   * @return {@code true} since the account never expires.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is locked.
   * Always returns {@code true}, meaning accounts are never locked.
   *
   * @return {@code true} since the account is never locked.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials (password) have expired.
   * Always returns {@code true}, meaning credentials never expire.
   *
   * @return {@code true} since credentials never expire.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled.
   * Always returns {@code true}, meaning all users are enabled.
   *
   * @return {@code true} since all users are enabled by default.
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

}
