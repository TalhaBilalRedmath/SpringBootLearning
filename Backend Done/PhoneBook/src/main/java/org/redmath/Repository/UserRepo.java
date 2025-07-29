package org.redmath.Repository;

import jakarta.transaction.Transactional;
import org.redmath.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    @Transactional
    Optional<User> findByUsername(String username);

    User findByEmail(String email);
}
