package pl.lasota.sensor.flows.service;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lasota.sensor.core.entities.flows.Flow;

import java.util.List;
import java.util.Optional;


@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {

    @Query("SELECT f FROM Flow f WHERE f.member.id = :memberId")
    List<Flow> findFlowsBy(@Param("memberId") String memberId);

    @Query("SELECT f FROM Flow f WHERE f.member.id = :memberId AND f.id = :flowId order by f.updated")
    Optional<Flow> findFlowsBy(@Param("memberId") String memberId, @Param("flowId") Long flowId);

    @Modifying
    @Query("UPDATE Flow f SET f.isActivate=false WHERE f.id=:id")
    void deactivate(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Flow f SET f.isActivate=true WHERE f.id=:id")
    void activate(@Param("id") Long id);

    @Query("SELECT f FROM Flow f WHERE f.isActivate=true")
    List<Flow> findAllActiveFlows();

}
