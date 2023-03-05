package com.jp.pssior.assignment.constant;

import com.jp.pssior.assignment.exception.ShowException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ExecuteAction {

    SETUP(true),
    VIEW(true),
    AVAILABILITY(false),
    BOOK(false),
    CANCEL(false);

    private Boolean adminFlag;

    ExecuteAction(boolean adminFlag) {
        this.adminFlag = adminFlag;
    }

    public static ExecuteAction valueOfAction(String action) throws Exception {
        return Arrays.stream(ExecuteAction.values())
                .filter(executeAction -> executeAction.name().equalsIgnoreCase(action))
                .findAny()
                .orElseThrow(() -> new ShowException(ErrorCode.COMMAND_NOT_FOUND, action));
    }
}
