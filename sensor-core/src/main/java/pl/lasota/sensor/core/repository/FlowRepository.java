package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.models.flows.Flows;

import java.util.Optional;


@Repository
public interface FlowRepository extends JpaRepository<Flows, Long> {

    @Query("SELECT f FROM Flows f WHERE f.member.id = :memberId AND f.id = :flowId")
    Optional<Flows> findFlowsBy(@Param("memberId") Long memberId, @Param("flowId") Long flowId);


    @Query("UPDATE Flows f SET f.isActivate=true WHERE f.id=:id")
    void deactivate(Long id);

    @Query("UPDATE Flows f SET f.isActivate=false WHERE f.id=:id")
    void activate(Long id);
}
