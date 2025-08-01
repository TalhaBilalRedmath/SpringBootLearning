package com.redmath.testing;

import org.junit.jupiter.api.Test;
import org.redmath.Model.Contact;

import jakarta.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ContactModelTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testNoArgsConstructorAndSetters() {
        Contact contact = new Contact();
        contact.setId(1);
        contact.setName("Alice");
        contact.setEmail("alice@example.com");
        contact.setNumber("123456789");

        assertEquals(1, contact.getId());
        assertEquals("Alice", contact.getName());
        assertEquals("alice@example.com", contact.getEmail());
        assertEquals("123456789", contact.getNumber());
    }

    @Test
    void testParameterizedConstructor() {
        Contact contact = new Contact("Bob", "987654321");
        assertEquals("Bob", contact.getName());
        assertEquals("987654321", contact.getNumber());
        assertNull(contact.getEmail()); // Email not set in constructor
    }

    @Test
    void testValidationFailsOnInvalidName() {
        Contact contact = new Contact();
        contact.setName("1234"); // invalid
        contact.setNumber("123456");

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidationFailsOnBlankNumber() {
        Contact contact = new Contact();
        contact.setName("John");
        contact.setNumber("   "); // invalid

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidationPasses() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setNumber("123456789");

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty());
    }
}