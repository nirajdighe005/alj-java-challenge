package jp.co.axa.api.demo.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor()
@NoArgsConstructor
public class EmployeeDTO extends EmployeeInfoDTO {

    @EqualsAndHashCode.Include
    @Min(value = 1, message="Id can't be zero or negative")
    @NotNull
    private Long id;

    public EmployeeDTO(@NotBlank String name, @Min(value = 0, message = "Salary, cannot be negative.(Can be zero for an intern).") @NotNull Integer salary, @NotBlank String department, Long id) {
        super(name, salary, department);
        this.id = id;
    }
}

