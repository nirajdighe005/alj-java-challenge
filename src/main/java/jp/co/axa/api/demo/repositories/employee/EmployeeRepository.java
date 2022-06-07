package jp.co.axa.api.demo.repositories.employee;

import jp.co.axa.api.demo.entities.employee.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee,Long> {
}
