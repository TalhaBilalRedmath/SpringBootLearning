package org.redmath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public Main(){
        System.out.println("Spring Boot Activated!");
    }


    public static void main(String[] args) {
        SpringApplication.run(Main.class);

        }
    }