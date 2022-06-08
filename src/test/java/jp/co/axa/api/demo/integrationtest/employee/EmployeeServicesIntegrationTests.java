package jp.co.axa.api.demo.integrationtest.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import jp.co.axa.api.demo.dto.employee.BulkEmployeeGetDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeDTO;
import jp.co.axa.api.demo.dto.employee.EmployeeInfoDTO;
import jp.co.axa.api.demo.entities.employee.Employee;
import jp.co.axa.api.demo.repositories.employee.EmployeeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeServicesIntegrationTests {

    private MockMvc mockMvc;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WebApplicationContext applicationContext;

    Map<Long, EmployeeDTO> employees = new HashMap<>();

    private static final String URL_TEMPLATE = "/api/v1/employees/";
    private static final String EMPLOYEE_ID = "{employeeId}";
    private static final String EMPLOYEE_DATA_FILE = "employee-data/employee-mock-data.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void initialize() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        initializeData();
    }

    @SneakyThrows
    private void initializeData() {
        String employeeData = Resources.toString(Resources.getResource(EMPLOYEE_DATA_FILE), StandardCharsets.UTF_8);
        Employee[] employeeInfo = new ObjectMapper().readValue(employeeData, Employee[].class);
        EmployeeDTO[] employeeDto = new ObjectMapper().readValue(employeeData, EmployeeDTO[].class);
        employees = Arrays.stream(employeeDto).collect(Collectors.toMap(EmployeeDTO::getId, Function.identity()));
        employeeRepository.saveAll(Arrays.asList(employeeInfo));
    }

    @SneakyThrows
    @Test
    void getAllEmployees()  {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE))
                .andExpect(status().isOk()).andReturn();
        BulkEmployeeGetDTO bulkDto = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BulkEmployeeGetDTO.class);
        assertThat(bulkDto).isNotNull();
        assertThat(bulkDto.getEmployees()).isNotEmpty();
        ArrayList<Employee> employeesList = Lists.newArrayList(employeeRepository.findAll());
        assertEquals(bulkDto.getEmployees().size(), employeesList.size());
    }

    @SneakyThrows
    @Test
    public void testGetEmployee() {
        Long employeeId = 1L;
        MvcResult mvcResult = mockMvc.perform(get(URL_TEMPLATE + "/" + EMPLOYEE_ID, employeeId)).andReturn();
        EmployeeDTO e = mapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDTO.class);
        assertThat(e).isNotNull();
        assertEquals(e, employees.get(employeeId));
    }

    @SneakyThrows
    @Test
    public void testSaveEmployee() {
        EmployeeInfoDTO employeeToCreate = new EmployeeInfoDTO("John", 5000, "SALES");

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(employeeToCreate))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        ArrayList<Employee> employeeData = Lists.newArrayList(employeeRepository.findAll());
        Optional<Employee> createdEmployee = employeeData.stream().filter(employee -> employee.getName().equals(employeeToCreate.getName())
                && employee.getDepartment().equals(employeeToCreate.getDepartment())).findFirst();
        assert (createdEmployee.isPresent());
    }

    @SneakyThrows
    @Test
    public void testUpdateEmployee() {
        long employeeId = 1L;
        EmployeeDTO employeeToUpdate = new EmployeeDTO("Hashimoto", 3000, "HR", employeeId);

        mockMvc.perform(put(URL_TEMPLATE, employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(employeeToUpdate))
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();
        Optional<Employee> employee = employeeRepository.findById(1L);

        assertTrue(employee.isPresent());
        assertEquals(employee.get().getSalary(), employeeToUpdate.getSalary());
    }

    @SneakyThrows
    @Test
    public void testDeleteEmployee() {
        long employeeId = 2L;
        this.mockMvc.perform(delete(URL_TEMPLATE + EMPLOYEE_ID, employeeId))
                .andExpect(status().isOk());
        assertFalse(employeeRepository.existsById(2L));
    }
}
