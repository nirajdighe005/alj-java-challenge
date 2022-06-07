package jp.co.axa.api.demo.controllers.employees;

import jp.co.axa.api.demo.dto.root.IRootDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class BaseController {

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/restricted")
    @ResponseBody
    protected String restricted() {
        return "You found the secret lair!";
    }

    protected <T extends IRootDTO> ResponseEntity<T> handleSuccess(T dto) {
        return handle(dto, HttpStatus.OK);
    }

    protected <T extends IRootDTO> ResponseEntity<T> handle(T dto, HttpStatus status) {
        //log
        return new ResponseEntity<>(dto, status);
    }
}
