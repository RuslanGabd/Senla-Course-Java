package com.ruslan.ui.action.request;

import com.ruslan.DAO.IBookDao;
import com.ruslan.DI.annotation.Inject;
import com.ruslan.services.RequestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionsRequest {
    final Logger logger = LogManager.getLogger(ActionsRequest.class);
    @Inject
    RequestService requestService;
    @Inject
    IBookDao bookDao;

    public ActionsRequest() {

    }
}
