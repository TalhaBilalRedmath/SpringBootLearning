package org.redmath.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Contact{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    public String name;

    private String email;
    @NotBlank(message = "Number is required")
    @Pattern(regexp = "^\\d+$", message = "Number must contain digits only")
    public String number;
    public Contact(String n, String nu){
        this.name = n;
        this.number = nu;
    }

    public Contact(){}



}


