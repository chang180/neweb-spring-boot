package com.neweb.web.controller;

import com.neweb.web.model.Member;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.util.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String token = (String) attr.getRequest().getSession().getAttribute("token");
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return "redirect:/login";
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        Optional<Member> memberOptional = memberRepository.findByUsername(username);
        if (memberOptional.isPresent()) {
            model.addAttribute("member", memberOptional.get());
        } else {
            model.addAttribute("error", "Member not found");
        }
        return "profile"; // 返回视图名
    }

    @PostMapping("/profile/update")
    @Transactional
    public String updateProfile(@ModelAttribute Member member, Model model) {
        Optional<Member> existingMemberOptional = memberRepository.findById(member.getId());
        if (existingMemberOptional.isPresent()) {
            Member existingMember = existingMemberOptional.get();
            existingMember.setEmail(member.getEmail());
            if (member.getPassword() != null && !member.getPassword().isEmpty()) {
                existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
            }
            memberRepository.save(existingMember);
            model.addAttribute("member", existingMember);
            model.addAttribute("success", "Profile updated successfully");
        } else {
            model.addAttribute("error", "Member not found");
        }
        return "profile"; // 返回视图名
    }

    @GetMapping("/list")
    public String listMembers(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "members"; // 返回视图名
    }

    @GetMapping("/{id}")
    public String viewMemberProfile(@PathVariable("id") Long id, Model model) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isPresent()) {
            model.addAttribute("member", memberOptional.get());
        } else {
            model.addAttribute("error", "Member not found");
        }
        return "memberProfile"; // 返回视图名
    }

    @GetMapping("/currentUser")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser() {
        // 获取当前请求属性
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String token = (String) attr.getRequest().getSession().getAttribute("token");

        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        Optional<Member> memberOptional = memberRepository.findByUsername(username);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return ResponseEntity.ok(Collections.singletonMap("id", member.getId()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }
    }
}
