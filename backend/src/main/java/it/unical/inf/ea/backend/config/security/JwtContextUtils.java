package it.unical.inf.ea.backend.config.security;

import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtContextUtils {

    private final UserDao userRepository;

<<<<<<< HEAD
=======
    //USELESS CLASS, USE TokenStore.getUser(token) INSTEAD

>>>>>>> 06af0e3d553d8ced8be3e2ddb7569f3beedec786
    public Optional<String> getUsernameFromContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        if(authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        }

        return Optional.empty();
    }

    public User getUserLoggedFromContext(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication==null)
            return null;
        if(authentication.getPrincipal() instanceof UserDetails userDetails){
            return userRepository.findByUsername(userDetails.getUsername());
        }
        return null;
    }
}
