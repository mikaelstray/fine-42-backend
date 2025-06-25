package com.mikael.project.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mikael.project.backend.model.dtos.fine.Status;
import com.mikael.project.backend.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)

@Table(name = "fines")
public class Fine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private Integer amount;

  @Column
  private String description;

  @CreationTimestamp
  @Column(name = "issued_at", nullable = false, updatable = false)
  private LocalDateTime issuedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.ACTIVE;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "receiver_id", nullable = false)
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private User receiver;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "given_by_id", nullable = false)
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private User giver;
}

