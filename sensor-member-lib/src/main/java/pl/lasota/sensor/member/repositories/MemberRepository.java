package pl.lasota.sensor.member.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.entities.Provider;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndProvider(String email, Provider provider);

    boolean existsById(String memberId);

    Optional<Member> findMemberById(String memberId);

}
