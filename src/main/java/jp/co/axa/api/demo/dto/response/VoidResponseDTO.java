package jp.co.axa.api.demo.dto.response;

import jp.co.axa.api.demo.dto.root.IRootDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoidResponseDTO implements IRootDTO {
    private String response;
}
