package lt.ivl.components.repository;

import lt.ivl.components.domain.RepairStatusHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RepairStatusHistoryRepository extends CrudRepository<RepairStatusHistory, Integer> {
    @Modifying
    @Query("DELETE FROM RepairStatusHistory rsh WHERE rsh.repair.id=:id")
    void deleteByRepairId(@Param("id") Integer id);
}
