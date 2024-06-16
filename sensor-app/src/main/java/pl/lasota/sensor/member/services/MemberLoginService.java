package pl.lasota.sensor.member.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.entities.Provider;
import pl.lasota.sensor.entities.Role;
import pl.lasota.sensor.exceptions.SensorMemberException;
import pl.lasota.sensor.member.MemberLoginDetailsServiceInterface;
import pl.lasota.sensor.member.MemberServiceInterface;
import pl.lasota.sensor.member.repositories.MemberRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberLoginService implements MemberServiceInterface, MemberLoginDetailsServiceInterface {

    private final MemberRepository memberRepository;
    private final GeneratorService generateKeyPair;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void createGoogle(String name, String email) {
        Member user = create(name, email);
        user.setProvider(Provider.GOOGLE);
        user.setPassword(null);
        save(user);
    }


    @Override
    public void save(Member member) {
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new SensorMemberException("Occurred while saving member", e);
        }

    }


    @Override
    public Member findByEmailAndProvider(String username, Provider provider) {
        return memberRepository.findByEmailAndProvider(username, provider)
                .orElseThrow(() -> new SensorMemberException("User not found"));

    }

    @Override
    public boolean checkCredential(String username, String password, Provider provider) {
        Member member = memberRepository.findByEmailAndProvider(username, provider)
                .orElseThrow(() -> new SensorMemberException("User not found"));

        if (member.getPassword() == null || member.getPassword().isBlank()) {
            return false;
        }
        return passwordEncoder.matches(password, member.getPassword());
    }

    @Override
    public boolean isMemberExistByMemberId(String memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public Member loggedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member principal = (Member) authentication.getPrincipal();
        return memberRepository.findMemberById(principal.getUsername()).orElseThrow(() -> new SensorMemberException("User not found"));
    }

    @Override
    public Member getMember(String memberId) {
        return memberRepository.findMemberById(memberId).orElseThrow(() -> new SensorMemberException("User not found"));
    }

    private Member create(String name, String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            throw new SensorMemberException("Occurred problem to create member {}", name);
        }

        Member user = new Member();
        user.setEmail(email);
        user.setName(name);
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setId(generateKeyPair.generateRandomString());

        return user;
    }
}
