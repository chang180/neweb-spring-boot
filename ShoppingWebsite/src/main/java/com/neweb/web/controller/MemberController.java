package com.neweb.web.controller;

import com.neweb.web.model.Member;
import com.neweb.web.payload.request.LoginRequest;
import com.neweb.web.payload.request.RegisterRequest;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.service.UserDetailsImpl;
import com.neweb.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class MemberController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Transactional
    public String authenticateUser(@ModelAttribute LoginRequest loginRequest, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // 更新数据库中的userToken和tokenExpiry
            Date expiryDate = new Date(System.currentTimeMillis() + jwtUtils.getJwtExpirationMs());
            Optional<Member> memberOptional = memberRepository.findByUsername(loginRequest.getUsername());
            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();
                member.setUserToken(jwt);
                member.setTokenExpiry(expiryDate);
                memberRepository.save(member);
            }

            // 暂时定向至 profile
            return "redirect:/auth/profile?token=" + jwt;
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    @PostMapping("/logout")
    @Transactional
    public String logoutUser(@ModelAttribute String username) {
        memberRepository.clearUserToken(username);
        return "redirect:/login?logout";
    }

    @PostMapping("/register")
    @Transactional
    public String registerUser(@ModelAttribute RegisterRequest signUpRequest, Model model) {
        if (memberRepository.existsByUsername(signUpRequest.getUsername())) {
            model.addAttribute("error", "Error: Username is already taken!");
            return "register";
        }

        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            model.addAttribute("error", "Error: Email is already in use!");
            return "register";
        }

        Member user = new Member(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()));
        memberRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showProfile(@RequestParam("token") String token, Model model) {
        // 解析token获取用户名
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

    @GetMapping("/members")
    public String listMembers(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "members"; // 返回视图名
    }

    @GetMapping("/member/{id}")
    public String viewMemberProfile(@PathVariable("id") Long id, Model model) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isPresent()) {
            model.addAttribute("member", memberOptional.get());
        } else {
            model.addAttribute("error", "Member not found");
        }
        return "memberProfile"; // 返回视图名
    }
}
