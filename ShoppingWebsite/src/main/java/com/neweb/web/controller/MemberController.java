package com.neweb.web.controller;

import com.neweb.web.model.Member;
import com.neweb.web.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public Member registerMember(@RequestBody Member member) {
        return memberService.saveMember(member);
    }

    @GetMapping("/email/{email}")
    public Optional<Member> getMemberByEmail(@PathVariable String email) {
        return memberService.findByEmail(email);
    }
}
