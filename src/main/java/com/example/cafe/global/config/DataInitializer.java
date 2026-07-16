package com.example.cafe.global.config;

import com.example.cafe.member.domain.Member;
import com.example.cafe.member.domain.MemberRole;
import com.example.cafe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!memberRepository.existsByUsername("admin")) {
            Member admin = Member.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(MemberRole.ADMIN)
                    .pointBalance(0L)
                    .build();
            memberRepository.save(admin);
        }
    }
}
