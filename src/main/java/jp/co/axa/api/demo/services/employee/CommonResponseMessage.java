package jp.co.axa.api.demo.services.employee;

import lombok.Getter;

@Getter
public enum CommonResponseMessage {
    ENTITY_EXISTS("%s record already exists with given id."),
    ENTITY_UNAVAILABLE("%s record unavailable for given id."),
    VALIDATION_ISSUE("%s field invalid in given input."),
    CREATE_SUCCESSFUL("%s with id %d Successfully created."),
    UPDATE_SUCCESSFUL("%s with id %d Successfully updated."),
    DELETE_SUCCESSFUL("%s with id %d Successfully deleted.");

    private final String message;
    CommonResponseMessage(String message) {
        this.message = message;
    }

}
