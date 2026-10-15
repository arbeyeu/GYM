package com.ironcore.gym.controller.api;

import com.ironcore.gym.dto.ApiResponse;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.repository.MemberRepository;
import com.ironcore.gym.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public MemberApiController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Member>>> searchMembers(@RequestParam("q") String query) {
        List<Member> results = memberRepository.searchMembers(query);
        return ResponseEntity.ok(ApiResponse.ok("Members retrieved", results));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<Member>> getMemberByCode(@PathVariable("code") String code) {
        Member member = memberService.getMemberByCode(code);
        return ResponseEntity.ok(ApiResponse.ok("Member found", member));
    }
}
