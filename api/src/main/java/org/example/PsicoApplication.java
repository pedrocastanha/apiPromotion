package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class PsicoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PsicoApplication.class, args);
    }
}
