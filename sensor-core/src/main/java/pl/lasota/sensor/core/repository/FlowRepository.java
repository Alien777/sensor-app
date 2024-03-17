package pl.lasota.sensor.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.models.flows.Flow;

import java.util.List;
import java.util.Optional;


@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {

    @Query("SELECT f FROM Flow f WHERE f.member.id = :memberId")
    List<Flow> findFlowsBy(@Param("memberId") Long memberId);

    @Query("SELECT f FROM Flow f WHERE f.member.id = :memberId AND f.id = :flowId")
    Optional<Flow> findFlowsBy(@Param("memberId") Long memberId, @Param("flowId") Long flowId);

    @Query("UPDATE Flow f SET f.isActivate=true WHERE f.id=:id")
    void deactivate(Long id);

    @Query("UPDATE Flow f SET f.isActivate=false WHERE f.id=:id")
    void activate(Long id);
}
