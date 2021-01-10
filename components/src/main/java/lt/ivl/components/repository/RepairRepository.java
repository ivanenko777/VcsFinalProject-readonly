package lt.ivl.components.repository;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.domain.Repair;
import lt.ivl.components.domain.RepairStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRepository extends CrudRepository<Repair, Integer> {
    List<Repair> findAllByCustomerOrderByCreatedAtDesc(Customer customer);

    Optional<Repair> findByIdAndCustomer(Integer integer, Customer customer);

    List<Repair> findAllByOrderByIdDesc();
}
