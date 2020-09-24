package lt.ivl.webExternalApp.repository;

import lt.ivl.webExternalApp.domain.Repair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairRepository extends CrudRepository<Repair, Integer> {
}
