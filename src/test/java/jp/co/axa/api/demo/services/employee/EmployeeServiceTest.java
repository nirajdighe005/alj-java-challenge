package jp.co.axa.api.demo.services.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import jp.co.axa.api.demo.dto.employee.EmployeeInfoDTO;
import jp.co.axa.api.demo.dto.response.VoidResponseDTO;
import jp.co.axa.api.demo.entities.employee.Employee;
import jp.co.axa.api.demo.exceptions.EmployeeAPIException;
import jp.co.axa.api.demo.repositories.employee.EmployeeRepository;
import jp.co.axa.api.demo.dto.employee.BulkEmployeeGetDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private static final String EMPLOYEE = "Employee";
    EmployeeService employeeService;

    @Mock
    EmployeeRepository employeeRepository;

    @Spy
    ModelMapper m;
    Map<Long, Employee> employeeData = new HashMap<>();

    private static final String EMPLOYEE_DATA_FILE = "employee-data/employee-mock-data.json";

    @BeforeEach
    void init() throws IOException {
        employeeService = new EmployeeServiceImpl(employeeRepository, m);
        initializeEmployeeData();
        initializeMock();
    }

    private void initializeMock() {
        for (Map.Entry<Long, Employee> employee : employeeData.entrySet()) {
            lenient().doReturn(Optional.ofNullable(employee.getValue())).when(employeeRepository).findById(employee.getKey());
            lenient().doNothing().when(employeeRepository).deleteById(employee.getKey());
            lenient().doReturn(true).when(employeeRepository).existsById(employee.getKey());
        }
        lenient().doReturn(employeeData.values()).when(employeeRepository).findAll();

    }

    private void initializeEmployeeData() throws IOException {
        String employeeInfo = Resources.toString(Resources.getResource(EMPLOYEE_DATA_FILE), StandardCharsets.UTF_8);
        Employee[] employees = new ObjectMapper().readValue(employeeInfo, Employee[].class);
        employeeData = Arrays.stream(employees).collect(Collectors.toMap(Employee::getId, Function.identity()));
    }

    /**
     * This is a positive test case of Employee service for getting employee.
     * The given ID is present in data.
     *
     * @throws EmployeeAPIException may throw if entity is unavailable
     */
    @Test
    public void getEmployeeTest_validId() throws EmployeeAPIException {
        long empId = 1L;
        EmployeeDTO employee = employeeService.getEmployee(empId);
        assertThat(employee).isNotNull();
        assert (employee.getId().equals(empId));
        assert (employee.getName().equals(employeeData.get(empId).getName()));
    }

    /**
     * This is a negative test case of Employee service for getting employee.
     * The given ID is not present in data.
     */
    @Test
    public void getEmployeeTest_InvalidID() {
        //following id is not present in employeeData
        long empId = 9L;
        Exception exception = assertThrows(EmployeeAPIException.class, () -> employeeService.getEmployee(empId));
        String actualMessage = exception.getMessage();
        String messageFormat = CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage();
        assertTrue(actualMessage.contains(String.format(messageFormat, EMPLOYEE)));
    }

    /**
     *  This is a positive test_case for getting all employees. It checks whether all the employees are returned from
     * the service.
    **/
    @Test
    public void getAllEmployeesTest() {
        BulkEmployeeGetDTO allEmployees = employeeService.retrieveEmployees();
        assertThat(allEmployees).isNotNull();
        assertEquals(allEmployees.getEmployees().size(), employeeData.size());
        Set<Long> availableIds = employeeData.keySet();
        List<Long> idsFromService = allEmployees.getEmployees().stream().map(EmployeeDTO::getId).collect(Collectors.toList());
        assertTrue(availableIds.containsAll(idsFromService));
    }

    /**
     * Test Case to check whether employee is getting created with below ID or not.
     *
     */
    @Test
    public void createEmployeesTest() {
        EmployeeInfoDTO saveEmployee = new EmployeeInfoDTO("Rohan", 5000, "Sales");
        EmployeeDTO savedEmployeeDto = new EmployeeDTO("Rohan", 5000, "Sales", 122L);
        Employee savedEmployee = m.map(savedEmployeeDto, Employee.class);
        lenient().doReturn(savedEmployee).when(employeeRepository).save(any(Employee.class));

        VoidResponseDTO responseDTO = employeeService.saveEmployee(saveEmployee);
        assertThat(responseDTO).isNotNull();
        String expectedResponse = String.format(CommonResponseMessage.CREATE_SUCCESSFUL.getMessage(), EMPLOYEE, 122L);
        assertEquals(expectedResponse, responseDTO.getResponse());
    }


    /**
     * Test Case to check whether employee is getting updated with valid info.
     *
     * @throws EmployeeAPIException may throw exception if employee does not exist.
     */
    @Test
    public void updateEmployeesTest_ValidInfo() throws EmployeeAPIException {
        long id = 1L;
        EmployeeDTO savedEmployeeDto = new EmployeeDTO("Hashimoto", 5000, "HR", id);
        Employee savedEmployee = m.map(savedEmployeeDto, Employee.class);
        lenient().doReturn(savedEmployee).when(employeeRepository).save(savedEmployee);

        VoidResponseDTO result = employeeService.updateEmployee(savedEmployeeDto);
        String expectedResponse = String.format(CommonResponseMessage.UPDATE_SUCCESSFUL.getMessage(), EMPLOYEE, id);
        assertThat(result).isNotNull();
        assertEquals(expectedResponse, result.getResponse());
    }

    /**
     * This test is to check whether appropriate exception is thrown
     * if the entity of given ID does not exist.
     */
    @Test
    public void updateEmployeesTest_EntityAbsent() {

        EmployeeDTO savedEmployeeDto = new EmployeeDTO("Rohan", 5000, "Sales", 234L);
        EmployeeAPIException exception = assertThrows(EmployeeAPIException.class, () -> employeeService.updateEmployee(savedEmployeeDto));
        String actualMessage = exception.getMessage();
        String messageFormat = CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage();
        assertTrue(actualMessage.contains(String.format(messageFormat, EMPLOYEE)));
    }

    /**
     * Test case to check delete employee use-case with valid ID gives appropriate response.
     */
    @Test
    public void deleteEmployeeTest_validID() throws EmployeeAPIException {
        Long empId = 1L;
        VoidResponseDTO result = employeeService.deleteEmployee(empId);
        String expectedResponse = String.format(CommonResponseMessage.DELETE_SUCCESSFUL.getMessage(), EMPLOYEE, empId);
        assertThat(result).isNotNull();
        assertEquals(expectedResponse, result.getResponse());
    }

    /**
     * Test case to check delete employee use-case with invalid ID throws appropriate exception.
     */
    @Test
    public void deleteEmployeeTest_invalidID() {
        //id not present in database
        Long empId = 52L;
        EmployeeAPIException exception = assertThrows(EmployeeAPIException.class, () -> employeeService.deleteEmployee(empId));
        String actualMessage = exception.getMessage();
        String messageFormat = CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage();
        assertTrue(actualMessage.contains(String.format(messageFormat, EMPLOYEE)));
    }
}