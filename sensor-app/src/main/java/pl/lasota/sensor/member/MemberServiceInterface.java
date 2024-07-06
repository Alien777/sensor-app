package pl.lasota.sensor.member;

import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.entities.Provider;


public interface MemberServiceInterface {

    void createGoogle(String name, String email);

    void save(Member member);

    Member findByEmailAndProvider(String username, Provider provider);

    boolean checkCredential(String username, String password, Provider provider);

    boolean isMemberExistByMemberId(String memberId);

    Member getMember(String memberId);


}
