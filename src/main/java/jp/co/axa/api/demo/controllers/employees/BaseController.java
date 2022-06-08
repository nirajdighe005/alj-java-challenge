package jp.co.axa.api.demo.controllers.employees;

import jp.co.axa.api.demo.dto.root.IRootDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    protected <T extends IRootDTO> ResponseEntity<T> handleSuccess(T dto) {
        return handle(dto, HttpStatus.OK);
    }

    protected <T extends IRootDTO> ResponseEntity<T> handle(T dto, HttpStatus status) {
        return new ResponseEntity<>(dto, status);
    }
}
