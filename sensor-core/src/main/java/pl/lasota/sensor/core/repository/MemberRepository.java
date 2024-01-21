package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.Provider;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndProvider(String email, Provider provider);

    boolean existsByMemberKey(String memberKey);

    Optional<Member> findMemberByMemberKey(String emberKey);
}
