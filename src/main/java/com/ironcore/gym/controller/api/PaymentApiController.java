package com.ironcore.gym.controller.api;

import com.ironcore.gym.dto.ApiResponse;
import com.ironcore.gym.dto.PaymentDto;
import com.ironcore.gym.entity.Payment;
import com.ironcore.gym.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentApiController {

    private final PaymentService paymentService;

    public PaymentApiController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/record")
    public ResponseEntity<ApiResponse<Payment>> recordPayment(@Valid @RequestBody PaymentDto dto,
                                                              Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "STAFF";
        Payment payment = paymentService.recordPayment(dto, performedBy);
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded successfully", payment));
    }

    @GetMapping("/receipt/{receiptNo}")
    public ResponseEntity<ApiResponse<Payment>> getReceipt(@PathVariable("receiptNo") String receiptNo) {
        Payment payment = paymentService.getPaymentByReceipt(receiptNo);
        return ResponseEntity.ok(ApiResponse.ok("Payment receipt retrieved", payment));
    }
}
