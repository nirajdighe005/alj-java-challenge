package jp.co.axa.api.demo.dto.employee;

import jp.co.axa.api.demo.dto.root.IRootDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkEmployeeGetDTO implements IRootDTO {

    List<EmployeeDTO> employees;
}
