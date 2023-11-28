package com.ruslan.ui.action.request;

import com.ruslan.ui.IAction;
import org.springframework.stereotype.Component;

@Component
public class ImportRequestsFromJsonToDataBase extends ActionsRequest implements IAction {
    public ImportRequestsFromJsonToDataBase() {
    }

    public void execute() {
        requestService.importRequestsFromJsonToDataBase();
    }
}
