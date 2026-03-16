package com.ftc.boss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BossFtcRdApplication {
    public static void main(String[] args) {
        SpringApplication.run(BossFtcRdApplication.class, args);
        System.out.println("=========================================");
        System.out.println("  Boss FTC RD Portal is running!");
        System.out.println("  URL : http://localhost:8080");
        System.out.println("  DB  : boss_ftc_rd  (auto-created)");
        System.out.println("=========================================");
    }
}
