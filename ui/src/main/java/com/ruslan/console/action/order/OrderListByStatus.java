package com.ruslan.console.action.order;

import com.ruslan.console.IAction;
import org.springframework.stereotype.Component;

@Component
public class OrderListByStatus extends ActionsOrder implements IAction {

    @Override
    public void execute() {
        System.out.println("Orders sorted by Status");
        orderService.getOrdersSortedByStatus().forEach(System.out::println);
    }
}