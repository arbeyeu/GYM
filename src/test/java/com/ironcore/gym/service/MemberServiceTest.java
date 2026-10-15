package com.ironcore.gym.service;

import com.ironcore.gym.dto.MemberDto;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testRegisterNewMember() {
        MemberDto dto = new MemberDto();
        dto.setFirstName("Bruce");
        dto.setLastName("Wayne");
        dto.setEmail("bruce.wayne.test@waynecorp.com");
        dto.setPhone("+91 99999 88888");
        dto.setGender("MALE");
        dto.setDateOfBirth(LocalDate.of(1990, 2, 19));
        dto.setStatus("ACTIVE");
        dto.setCreateLoginUser(true);

        Member saved = memberService.registerMember(dto, "TEST_SUITE");

        assertNotNull(saved.getId());
        assertNotNull(saved.getMemberCode());
        assertTrue(saved.getMemberCode().startsWith("IC-M-"));
        assertEquals("Bruce", saved.getFirstName());
        assertEquals("Wayne", saved.getLastName());
        assertEquals("ACTIVE", saved.getStatus());
    }

    @Test
    void testDuplicateEmailThrowsBadRequest() {
        MemberDto dto1 = new MemberDto();
        dto1.setFirstName("Clark");
        dto1.setLastName("Kent");
        dto1.setEmail("clark.kent.test@dailyplanet.com");
        dto1.setPhone("+91 99999 77777");
        memberService.registerMember(dto1, "TEST_SUITE");

        MemberDto dto2 = new MemberDto();
        dto2.setFirstName("Clark");
        dto2.setLastName("Duplicate");
        dto2.setEmail("clark.kent.test@dailyplanet.com");
        dto2.setPhone("+91 99999 66666");

        assertThrows(BadRequestException.class, () -> {
            memberService.registerMember(dto2, "TEST_SUITE");
        });
    }
}
