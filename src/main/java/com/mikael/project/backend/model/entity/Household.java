package com.mikael.project.backend.model.entity;

import com.mikael.project.backend.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)

@Table(name = "households")
public class Household {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  private String inviteCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_user_id", nullable = false)
  private User adminUser;

  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<User> members = new HashSet<>();
}
