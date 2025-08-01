package org.redmath.Repository;

import org.redmath.Model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRep extends JpaRepository<Contact, Integer> {
    boolean existsByEmail(String email);
}
