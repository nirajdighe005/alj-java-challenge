package jp.co.axa.api.demo.services.employee;

import jp.co.axa.api.demo.dto.employee.EmployeeInfoDTO;
import jp.co.axa.api.demo.dto.response.VoidResponseDTO;
import jp.co.axa.api.demo.exceptions.EmployeeAPIException;
import jp.co.axa.api.demo.dto.employee.EmployeeDTO;
import jp.co.axa.api.demo.dto.employee.BulkEmployeeGetDTO;

public interface EmployeeService {

    BulkEmployeeGetDTO retrieveEmployees();

    VoidResponseDTO saveEmployee(EmployeeInfoDTO employee) ;

    EmployeeDTO getEmployee(Long employeeId) throws EmployeeAPIException;

    VoidResponseDTO deleteEmployee(Long employeeId) throws EmployeeAPIException;

    VoidResponseDTO updateEmployee(EmployeeDTO employee) throws EmployeeAPIException;
}