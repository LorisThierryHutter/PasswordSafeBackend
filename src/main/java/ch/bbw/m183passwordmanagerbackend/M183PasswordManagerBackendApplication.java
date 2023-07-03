package ch.bbw.m183passwordmanagerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class M183PasswordManagerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(M183PasswordManagerBackendApplication.class, args);
    }

}
