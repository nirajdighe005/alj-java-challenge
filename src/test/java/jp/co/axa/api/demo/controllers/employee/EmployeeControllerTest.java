package jp.co.axa.api.demo.controllers.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import jp.co.axa.api.demo.controllers.employees.EmployeeController;
import jp.co.axa.api.demo.controllers.controlleradvice.ApplicationExceptionHandler;
import jp.co.axa.api.demo.dto.employee.BulkEmployeeGetDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeInfoDTO;
import jp.co.axa.api.demo.dto.response.VoidResponseDTO;
import jp.co.axa.api.demo.exceptions.EmployeeAPIException;
import jp.co.axa.api.demo.services.employee.CommonResponseMessage;
import jp.co.axa.api.demo.services.employee.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({SpringExtension.class})
public class EmployeeControllerTest {

    public static final String CONTEXT_PATH = "/api/v1/employees/";
    public static final String EMPLOYEE_ID = "{employeeId}";
    private static final String EMPLOYEE_DATA_FILE = "employee-data/employee-mock-data.json";
    private static final String EMPLOYEE = "Employee";
    @Mock
    EmployeeService employeeService;
    @InjectMocks
    ApplicationExceptionHandler exceptionHandler;
    @InjectMocks
    EmployeeController controller;
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<Long, EmployeeDTO> employees = new HashMap<>();

    @BeforeEach
    void initialize() throws IOException, EmployeeAPIException {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler).build();
        initializeEmployeeData();
        initializeMock();
    }

    private void initializeMock() throws EmployeeAPIException {
        BulkEmployeeGetDTO bulkDTO = new BulkEmployeeGetDTO(new ArrayList<>(employees.values()));
        given(employeeService.retrieveEmployees()).willReturn(bulkDTO);
        for (Long employeeId : employees.keySet()) {
            given(employeeService.getEmployee(employeeId)).willReturn(employees.get(employeeId));
        }

    }

    private void initializeEmployeeData() throws IOException {
        String employeeData = Resources.toString(Resources.getResource(EMPLOYEE_DATA_FILE), StandardCharsets.UTF_8);
        EmployeeDTO[] employeeInfo = new ObjectMapper().readValue(employeeData, EmployeeDTO[].class);
        employees = Arrays.stream(employeeInfo).collect(Collectors.toMap(EmployeeDTO::getId, Function.identity()));
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(CONTEXT_PATH)).andReturn();
        BulkEmployeeGetDTO bulkDto = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BulkEmployeeGetDTO.class);
        assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.OK.value());
        assertThat(bulkDto).isNotNull();
        assertThat(bulkDto.getEmployees()).isNotEmpty();
        assertEquals(bulkDto.getEmployees().size(), employees.size());
    }

    @Test
    public void testGetEmployee() throws Exception {
        Long employeeId = 4L;
        MvcResult mvcResult = mockMvc.perform(get(CONTEXT_PATH + EMPLOYEE_ID, employeeId)).andReturn();
        EmployeeDTO e = mapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDTO.class);
        assertThat(e).isNotNull();
        assertEquals(e, employees.get(employeeId));
    }

    @Test
    public void testSaveEmployee() throws Exception {
        Long employeeId = 11L;
        EmployeeInfoDTO employeeToCreate = new EmployeeInfoDTO("Alan", 5000, "SALES");
        VoidResponseDTO expectedResponse = new VoidResponseDTO(String.format(CommonResponseMessage.CREATE_SUCCESSFUL.getMessage(), EMPLOYEE, employeeId));
        given(employeeService.saveEmployee(employeeToCreate)).willReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(post(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(employeeToCreate))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.CREATED.value());
        VoidResponseDTO actualResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), VoidResponseDTO.class);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateEmployee() throws Exception {
        long employeeId = 1L;
        EmployeeDTO employeeToUpdate = new EmployeeDTO("Hashimoto", 3000, "HR", employeeId);

        VoidResponseDTO expectedResponse = new VoidResponseDTO(String.format(CommonResponseMessage.UPDATE_SUCCESSFUL.getMessage(), EMPLOYEE, employeeId));
        given(employeeService.updateEmployee(employeeToUpdate)).willReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(put(CONTEXT_PATH, employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(employeeToUpdate))
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        VoidResponseDTO actualResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), VoidResponseDTO.class);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        long employeeId = 2L;
        VoidResponseDTO expectedResponse = new VoidResponseDTO(String.format(CommonResponseMessage.UPDATE_SUCCESSFUL.getMessage(), EMPLOYEE, employeeId));
        given(employeeService.deleteEmployee(employeeId)).willReturn(expectedResponse);
        MvcResult mvcResult = this.mockMvc.perform(delete(CONTEXT_PATH + EMPLOYEE_ID, employeeId)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }



    @Test
    public void testUpdateEmployee_InValid_ID() throws Exception {
        long employeeId = 23L;
        EmployeeDTO employeeToUpdate = new EmployeeDTO("Hashimoto", 3000, "HR", employeeId);

        String exceptionMessage = String.format(CommonResponseMessage.ENTITY_UNAVAILABLE.getMessage(), EMPLOYEE);
        given(employeeService.updateEmployee(employeeToUpdate)).willThrow(new EmployeeAPIException(exceptionMessage));

        mockMvc.perform(put(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(employeeToUpdate))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String message = Objects.requireNonNull(result.getResolvedException()).getMessage();
                    assertEquals(message, exceptionMessage);
                });
    }

    @Test
    public void testUpdateEmployee_InValid_Data() throws Exception {
        long employeeId = 1L;
        EmployeeDTO employeeToUpdate = new EmployeeDTO("Hashimoto", 3000, "HR", employeeId);

        VoidResponseDTO expectedResponse = new VoidResponseDTO(String.format(CommonResponseMessage.UPDATE_SUCCESSFUL.getMessage(), EMPLOYEE, employeeId));
        given(employeeService.updateEmployee(employeeToUpdate)).willReturn(expectedResponse);

        String invalidData = "}{";
        mockMvc.perform(put(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidData))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateEmployee_InValid_FieldValues() throws Exception {
        long employeeId = 0L;
        EmployeeDTO employeeToUpdate = new EmployeeDTO("Hashimoto", 3000, "HR", employeeId);

        String exception = "Malformed Request : Invalid Fields:id";
        VoidResponseDTO expectedResponse = new VoidResponseDTO(String.format(CommonResponseMessage.UPDATE_SUCCESSFUL.getMessage(), EMPLOYEE, employeeId));
        given(employeeService.updateEmployee(employeeToUpdate)).willReturn(expectedResponse);

        mockMvc.perform(put(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(employeeToUpdate))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String message = result.getResponse().getContentAsString();
                    VoidResponseDTO response = mapper.readValue(message, VoidResponseDTO.class);
                    assertEquals(response.getResponse(), exception);
                });

    }

    @Test
    public void testUpdateEmployee_WithException() throws Exception {
        long employeeId = 23L;
        EmployeeDTO employeeToUpdate = new EmployeeDTO("Hashimoto", 5000, "HR", employeeId);

        String exceptionMessage = "Check Default Throwable controller advice";
        given(employeeService.updateEmployee(employeeToUpdate)).willThrow(new NullPointerException(exceptionMessage));

        mockMvc.perform(put(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(employeeToUpdate))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String message = Objects.requireNonNull(result.getResolvedException()).getMessage();
                    assertEquals(message, exceptionMessage);
                });
    }


}
