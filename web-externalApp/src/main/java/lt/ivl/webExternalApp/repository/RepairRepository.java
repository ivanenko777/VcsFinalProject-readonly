package lt.ivl.webExternalApp.repository;

import lt.ivl.webExternalApp.domain.Customer;
import lt.ivl.webExternalApp.domain.Repair;
import lt.ivl.webExternalApp.domain.RepairStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairRepository extends CrudRepository<Repair, Integer> {
    Iterable<Repair> findAllByCreatedByCustomerAndStatusInOrderByCreatedAtDesc(
            Customer customer, Iterable<RepairStatus> statuses);

    Optional<Repair> findByIdAndCreatedByCustomer(Integer integer, Customer customer);
}
