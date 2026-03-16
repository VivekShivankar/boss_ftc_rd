package com.ftc.boss;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "app.mail.enabled=false"
})
class BossFtcRdApplicationTests {
    @Test
    void contextLoads() {
        System.out.println("Context loaded OK");
    }
}
