package com.ironcore.gym.controller;

import com.ironcore.gym.dto.PaymentDto;
import com.ironcore.gym.entity.Payment;
import com.ironcore.gym.service.MemberService;
import com.ironcore.gym.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberService memberService;

    public PaymentController(PaymentService paymentService, MemberService memberService) {
        this.paymentService = paymentService;
        this.memberService = memberService;
    }

    @GetMapping
    public String listPayments(@RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "method", required = false, defaultValue = "ALL") String method,
                               @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                               Model model) {
        Page<Payment> paymentPage = paymentService.getPayments(query, method, status,
                PageRequest.of(page, size, Sort.by("paymentDate").descending()));

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("totalElements", paymentPage.getTotalElements());
        model.addAttribute("query", query);
        model.addAttribute("method", method);
        model.addAttribute("status", status);

        model.addAttribute("monthlyRevenue", paymentService.getMonthlyRevenue());
        model.addAttribute("pendingCount", paymentService.getPendingPaymentsCount());
        model.addAttribute("pendingAmount", paymentService.getPendingPaymentsAmount());

        model.addAttribute("paymentDto", new PaymentDto());
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("activeNav", "payments");
        return "payments/index";
    }

    @PostMapping("/record")
    public String recordPayment(@Valid @ModelAttribute("paymentDto") PaymentDto dto,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid payment input. Please verify all fields.");
            return "redirect:/payments";
        }

        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            Payment saved = paymentService.recordPayment(dto, performedBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Payment of ₹" + saved.getAmount() + " recorded successfully. Receipt No: " + saved.getReceiptNo());
            return "redirect:/payments/receipt/" + saved.getReceiptNo();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/payments";
        }
    }

    @GetMapping("/receipt/{receiptNo}")
    public String viewReceipt(@PathVariable("receiptNo") String receiptNo, Model model) {
        Payment payment = paymentService.getPaymentByReceipt(receiptNo);
        model.addAttribute("payment", payment);
        model.addAttribute("member", payment.getMember());
        model.addAttribute("activeNav", "payments");
        return "payments/receipt";
    }
}
