package it.unical.inf.ea.backend.data.entities;

import it.unical.inf.ea.backend.dto.enums.Provider;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.dto.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "bio", length = 500)
    private String bio;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserImage photoProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

//    @OneToMany(mappedBy = "ownerUser",fetch = FetchType.LAZY)
//    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "seller",fetch = FetchType.LAZY)
    private List<Product> sellingProducts = new ArrayList<>();

    @Column(name = "reviews_total_sum", nullable = false)
    private int reviewsTotalSum;

    @Column(name = "reviews_number", nullable = false)
    private int reviewsNumber;

    @ManyToMany(mappedBy = "usersThatLiked",fetch = FetchType.LAZY)
    List<Product> likedProducts= new ArrayList<>();

    @OneToMany(mappedBy = "sendUser",fetch = FetchType.LAZY)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receivedUser",fetch = FetchType.LAZY)
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Order> orders;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @OneToMany(mappedBy = "reporterUser",fetch = FetchType.LAZY)
    private List<Report> reports;

    @OneToMany(mappedBy = "reportedUser",fetch = FetchType.LAZY)
    private List<Report> reported;

    @OneToMany(mappedBy = "adminFollowedReport",fetch = FetchType.LAZY)
    private List<Report> adminFollowedMyReport;

    public Boolean isAdministrator() {
        return role.equals(UserRole.ADMIN) || role.equals(UserRole.SUPER_ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (role){
            case USER -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case ADMIN -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            case SUPER_ADMIN -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            }
        }
        return null;
    }

    public void addReview(int vote) {
        reviewsNumber++;
        reviewsTotalSum += vote;
    }

    public void removeReview(int vote) {
        reviewsNumber--;
        reviewsTotalSum -= vote;
    }

    public void editReview(int oldVote, int newVote) {
        reviewsTotalSum -= oldVote;
        reviewsTotalSum += newVote;
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
        return  password;
    }



}
