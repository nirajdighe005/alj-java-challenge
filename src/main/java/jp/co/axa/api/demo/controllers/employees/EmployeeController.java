package jp.co.axa.api.demo.controllers.employees;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jp.co.axa.api.demo.controllers.constants.ResponseContentConstants;
import jp.co.axa.api.demo.dto.employee.BulkEmployeeGetDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeInfoDTO;
import jp.co.axa.api.demo.dto.response.VoidResponseDTO;
import jp.co.axa.api.demo.exceptions.EmployeeAPIException;
import jp.co.axa.api.demo.services.employee.EmployeeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Api( tags = {"Employee Service"})
@Tag(name = "Employee Service", description = "These services manage employees")
public class EmployeeController extends BaseController {

    @NonNull
    private final EmployeeService employeeService;

    @Value("api.demo.profile")
    static String api;

    @ApiOperation(value = "Get All Employees in the System.", produces = MediaType.APPLICATION_JSON_VALUE
            , notes = "List of Employees in System", httpMethod = "GET", response = BulkEmployeeGetDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ResponseContentConstants.INTERNAL_SERVER_ERROR,
                    response = BulkEmployeeGetDTO.class)})
    @GetMapping("/employees")
    public ResponseEntity<BulkEmployeeGetDTO> getEmployees() {
        log.debug("Controller : Getting All Employees");
        return handleSuccess(employeeService.retrieveEmployees());
    }

    @ApiOperation(value = "Get Employee Information on the basis of ID"
            , produces = MediaType.APPLICATION_JSON_VALUE
            , notes = "Employee Information of given ID"
            , httpMethod = "GET"
            , response = EmployeeDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = ResponseContentConstants.BAD_REQUEST),
            @ApiResponse(code = 500, message = ResponseContentConstants.INTERNAL_SERVER_ERROR,
                    response = EmployeeDTO.class)})
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable(name = "employeeId") @Min(1) @Positive Long employeeId)
            throws EmployeeAPIException {
        log.debug("Controller : Getting Employee of the following ID : {}", employeeId);
        return handleSuccess(employeeService.getEmployee(employeeId));
    }

    @ApiOperation(value = "Create a new employee with given information"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
            , notes = "Add a employee to the system"
            , httpMethod = "POST"
            , response = VoidResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = ResponseContentConstants.BAD_REQUEST),
            @ApiResponse(code = 500, message = ResponseContentConstants.INTERNAL_SERVER_ERROR,
                    response = EmployeeDTO.class)})
    @PostMapping("/employees")
    public ResponseEntity<VoidResponseDTO> saveEmployee(@Valid @RequestBody EmployeeInfoDTO employee) {
        log.debug("Controller : Create Employee with the following Name : {}", employee.getName());
        return handle(employeeService.saveEmployee(employee), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete an existing Employee Record."
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
            , notes = "Delete the employee record of the given employee ID."
            , httpMethod = "DELETE"
            , response = VoidResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = ResponseContentConstants.BAD_REQUEST),
            @ApiResponse(code = 500, message = ResponseContentConstants.INTERNAL_SERVER_ERROR,
                    response = EmployeeDTO.class)})
    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<VoidResponseDTO> deleteEmployee(@PathVariable(name = "employeeId") @Valid @Min(1) Long employeeId) throws EmployeeAPIException {
        log.debug("Controller : Delete Employee with the following Id : {}", employeeId);
        return handleSuccess(employeeService.deleteEmployee(employeeId));
    }

    @ApiOperation(value = "Update an existing Employee Record."
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
            , notes = "Update the employee record of the given id."
            , httpMethod = "PUT"
            , response = VoidResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = ResponseContentConstants.BAD_REQUEST),
            @ApiResponse(code = 500, message = ResponseContentConstants.INTERNAL_SERVER_ERROR,
                    response = EmployeeDTO.class)})
    @PutMapping("/employees")
    public ResponseEntity<VoidResponseDTO> updateEmployee(@Valid @RequestBody EmployeeDTO employee)
            throws EmployeeAPIException {
        log.debug("Controller : Update Employee Information with following ID : {}", employee.getId());
        return handleSuccess(employeeService.updateEmployee(employee));
    }

}
