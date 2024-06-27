package com.neweb.web.controller;

import com.neweb.web.model.Member;
import com.neweb.web.repository.MemberRepository;
import com.neweb.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/")
    public String home(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token != null && jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Optional<Member> memberOptional = memberRepository.findByUsername(username);
            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();
                session.setAttribute("userId", member.getId());
            }
        }
        return "home"; // 返回视图名 home.html
    }
}
