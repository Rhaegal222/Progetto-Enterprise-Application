package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String phoneNumber;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserImage image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Cart cart;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_payment_methods",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_method_id")
    )
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Boolean isAdministrator() {
        return role.equals(UserRole.ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (role) {
            case USER -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case ADMIN -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !status.equals(UserStatus.CANCELLED);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !status.equals(UserStatus.BANNED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
