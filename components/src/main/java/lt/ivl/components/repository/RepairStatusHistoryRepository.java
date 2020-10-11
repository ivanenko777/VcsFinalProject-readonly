package lt.ivl.components.repository;

import lt.ivl.components.domain.RepairStatusHistory;
import org.springframework.data.repository.CrudRepository;

public interface RepairStatusHistoryRepository extends CrudRepository<RepairStatusHistory, Integer> {
}
