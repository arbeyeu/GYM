package com.ironcore.gym;

import com.ironcore.gym.repository.MemberRepository;
import com.ironcore.gym.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IronCoreGymApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(memberRepository);
    }

    @Test
    void testDataInitializerSeededUsers() {
        assertTrue(userRepository.count() >= 4, "Default users should be seeded on startup");
        assertTrue(userRepository.findByUsername("admin").isPresent(), "Admin user must exist");
    }
}
