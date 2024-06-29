package com.neweb.web.controller;

import com.neweb.web.model.Member;
import com.neweb.web.payload.request.LoginRequest;
import com.neweb.web.payload.request.RegisterRequest;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.service.UserDetailsImpl;
import com.neweb.web.util.JwtUtils;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

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

            // 將 JWT 存入 session
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            attr.getRequest().getSession(true).setAttribute("token", jwt);
            attr.getRequest().getSession(true).setAttribute("userId", userDetails.getId()); // 假設 UserDetailsImpl 有 getId 方法

            // 暫時定向至 products
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    @PostMapping("/logout")
    @Transactional
    public String logoutUser(HttpSession session) {
        session.invalidate(); // 清除所有 session 数据
        SecurityContextHolder.clearContext(); // 清除 Spring Security 上下文中的身份验证信息
        return "redirect:/login?logout";
    }
    
    @GetMapping("/logout")
    @Transactional
    public String logoutUserGet(HttpSession session) {
        session.invalidate(); // 清除所有 session 数据
        SecurityContextHolder.clearContext(); // 清除 Spring Security 上下文中的身份验证信息
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
    
    @PostMapping("/validateToken")
    @ResponseBody
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = jwtUtils.validateJwtToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok().body(Collections.singletonMap("valid", isValid));
    }
}
