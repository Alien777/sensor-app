package pl.lasota.sensor.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.core.exceptions.SaveMemberException;
import pl.lasota.sensor.core.exceptions.UserExistingException;
import pl.lasota.sensor.core.exceptions.UserNotExistingException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.Provider;
import pl.lasota.sensor.core.models.Role;
import pl.lasota.sensor.core.repository.MemberRepository;

import java.util.Optional;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GeneratorService generateKeyPair;

    @Transactional
    public void createGoogle(String name, String email) throws UserExistingException, SaveMemberException {
        Member user = create(name, email);
        user.setProvider(Provider.GOOGLE);
        user.setPassword(null);
        save(user);

    }

    public void save(Member member) throws SaveMemberException {
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new SaveMemberException();
        }

    }

    private Member create(String name, String email) throws UserExistingException {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            throw new UserExistingException();
        }

        Member user = new Member();
        user.setEmail(email);
        user.setName(name);
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setMemberKey(generateKeyPair.generateRandomString());

        return user;
    }

    public Member findByEmailAndProvider(String username, Provider provider) throws UserNotExistingException {
        return memberRepository.findByEmailAndProvider(username, provider)
                .orElseThrow(UserNotExistingException::new);

    }

    public boolean isMemberExistByMemberKey(String memberKey) {
        return memberRepository.existsByMemberKey(memberKey);
    }

    public Member loggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return memberRepository.findByEmail(principal.getUsername()).orElse(null);
    }
}
