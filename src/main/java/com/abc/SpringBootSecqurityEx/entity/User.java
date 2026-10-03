package com.abc.SpringBootSecqurityEx.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.abc.SpringBootSecqurityEx.enums.ERole;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Table(name = "USERS")
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    private String userName;

    @Column
    private String userFirstName;

    @Column
    private String userLastName;

    @Column
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // Initialize all Boolean fields with default values
    @Column
    private Boolean enabled = true;

    @Column
    private Boolean credentialsNonExpired = true;

    @Column
    private Boolean accountNonExpired = true;

    @Column
    private Boolean accountNonLocked = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_name"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ERole> roles = EnumSet.of(ERole.ROLE_USER);

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant lastUpdated;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        // Initialize Booleans in constructor too
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }

    public User() {
        // Initialize in default constructor
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }

}
