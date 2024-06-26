package com.neweb.web.controller;

import com.neweb.web.model.Member;
import com.neweb.web.payload.request.LoginRequest;
import com.neweb.web.payload.request.RegisterRequest;
import com.neweb.web.payload.response.JwtResponse;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.service.UserDetailsImpl;
import com.neweb.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 设置 token 有效期
        Date expiryDate = new Date(System.currentTimeMillis() + jwtUtils.getJwtExpirationMs());

        // 更新数据库中的 userToken 和 tokenExpiry
        memberRepository.updateUserToken(loginRequest.getUsername(), jwt, expiryDate);

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logoutUser(@RequestParam String username) {
        memberRepository.clearUserToken(username);
        return ResponseEntity.ok("User logged out successfully!");
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerUser(@ModelAttribute RegisterRequest signUpRequest) {
        if (memberRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new user's account
        Member user = new Member(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()));

        memberRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
