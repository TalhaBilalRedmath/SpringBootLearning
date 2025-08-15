package org.redmath.Service;

import org.redmath.Model.User;
import org.redmath.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//

@Service
public class UserService implements UserDetailsService{

    private final UserRepo rep;
    PasswordEncoder passwordEncoder;

    public UserService(UserRepo repo, PasswordEncoder ps) {
        this.rep = repo;
        this.passwordEncoder = ps;
    }

    public List<User> getUsers() {
        return rep.findAll();
    }

    public void save(User u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        System.out.println("USER IS: " + u.getRole());
        rep.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("USER LOADER HIT");
        User user = rep.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public boolean deleteUser(Long id) {
        Optional<User> user = rep.findById(id);
        if(user.isEmpty()){
            return false;
        }

        rep.deleteById(id);
        return true;
    }
}
