package jp.co.axa.api.demo.services.employee;

import jp.co.axa.api.demo.dto.employee.EmployeeInfoDTO;
import jp.co.axa.api.demo.dto.response.VoidResponseDTO;
import jp.co.axa.api.demo.exceptions.EmployeeAPIException;
import jp.co.axa.api.demo.repositories.employee.EmployeeRepository;
import jp.co.axa.api.demo.dto.employee.BulkEmployeeGetDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeDTO;
import jp.co.axa.api.demo.entities.employee.Employee;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @NonNull
    private final EmployeeRepository employeeRepository;
    @NonNull
    private final ModelMapper mapper;

    private static final String EMPLOYEE = "Employee";

    /**
     * Retrieve all the employees present in the database.
     *
     * @return dto that has list of employees
     */
    public BulkEmployeeGetDTO retrieveEmployees() {
        Iterable<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOs = StreamSupport.stream(employees.spliterator(), false)
                .map(employee -> mapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
        return new BulkEmployeeGetDTO(employeeDTOs);
    }

    /**
     * Get employee for given ID.
     *
     * @param employeeId ID for which employee needs to be fetched
     * @return DTO of employee whose id was given as input.
     * @throws EmployeeAPIException thrown when no employee of this id exist
     */
    public EmployeeDTO getEmployee(Long employeeId) throws EmployeeAPIException {
        EmployeeDTO employeeDTO = getEmployeeDTOById(employeeId);
        if (employeeDTO == null) {
            throw new EmployeeAPIException(String.format(CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage(), EMPLOYEE));
        }
        return employeeDTO;
    }

    /**
     * Create Employee of the given ID.
     *
     * @param employeeInfo employee that you are trying to create.
     * @return VoidResponseDTO The api should generally should have a response. So instead of
     * using void using VoidResponseDTO which has a String message for end user.
     */
    public VoidResponseDTO saveEmployee(EmployeeInfoDTO employeeInfo) {
        Employee employeeEntity = mapper.map(employeeInfo, Employee.class);
        Employee employee = employeeRepository.save(employeeEntity);
        String response = String.format(CommonResponseMessage.CREATE_SUCCESSFUL.getMessage(), EMPLOYEE, employee.getId());
        return new VoidResponseDTO(response);
    }

    /**
     * Update Employee of the given ID.
     *
     * @param employee employee that you are trying to update.
     * @return VoidResponseDTO The api should generally should have a response. So instead of
     * using void using VoidResponseDTO which has a String message for end user.
     */
    public VoidResponseDTO updateEmployee(EmployeeDTO employee) throws EmployeeAPIException {
        Long id = employee.getId();
        Employee employeeEntity = mapper.map(employee, Employee.class);
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeAPIException(String.format(CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage(), EMPLOYEE));
        }
        employeeRepository.save(employeeEntity);
        return new VoidResponseDTO(String.format(CommonResponseMessage.UPDATE_SUCCESSFUL.getMessage(), EMPLOYEE, id));
    }

    /**
     * Delete Employee of the given ID.
     *
     * @param employeeId employee you are trying to delete
     * @return VoidResponseDTO The api should generally should have a response. So instead of
     * using void using VoidResponseDTO which has a String message for end user.
     */
    public VoidResponseDTO deleteEmployee(Long employeeId) throws EmployeeAPIException {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeAPIException(String.format(CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage(), EMPLOYEE));
        }
        employeeRepository.deleteById(employeeId);
        return new VoidResponseDTO(String.format(CommonResponseMessage.DELETE_SUCCESSFUL.getMessage(), EMPLOYEE, employeeId));
    }

    private EmployeeDTO getEmployeeDTOById(Long employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        return employeeOptional.map(employee -> mapper.map(employee, EmployeeDTO.class)).orElse(null);
    }
}