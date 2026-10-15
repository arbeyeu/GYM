package com.ironcore.gym.service;

import com.ironcore.gym.dto.PaymentDto;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.entity.Payment;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testRecordPaymentGeneratesReceipt() {
        Member member = memberRepository.findByMemberCode("IC-M-1001").orElseThrow();

        PaymentDto dto = new PaymentDto();
        dto.setMemberId(member.getId());
        dto.setAmount(new BigDecimal("4999.00"));
        dto.setPaymentMethod("UPI");
        dto.setTransactionId("UPI/TEST/99281");
        dto.setNotes("Quarterly Renewal");

        Payment payment = paymentService.recordPayment(dto, "TEST_SUITE");

        assertNotNull(payment.getId());
        assertNotNull(payment.getReceiptNo());
        assertTrue(payment.getReceiptNo().startsWith("REC-"));
        assertEquals("PAID", payment.getStatus());
        assertEquals(new BigDecimal("4999.00"), payment.getAmount());
    }

    @Test
    void testZeroAmountThrowsBadRequest() {
        Member member = memberRepository.findByMemberCode("IC-M-1001").orElseThrow();

        PaymentDto dto = new PaymentDto();
        dto.setMemberId(member.getId());
        dto.setAmount(BigDecimal.ZERO);
        dto.setPaymentMethod("CASH");

        assertThrows(BadRequestException.class, () -> {
            paymentService.recordPayment(dto, "TEST_SUITE");
        });
    }
}
