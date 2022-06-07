package jp.co.axa.api.demo.dto.employee;

import jp.co.axa.api.demo.dto.root.IRootDTO;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmployeeInfoDTO implements IRootDTO {

    @NotBlank
    private String name;

    @Min(value = 0, message="Salary, cannot be negative.(Can be zero for an intern).")
    @NotNull
    private Integer salary;

    @NotBlank
    private String department;
}
