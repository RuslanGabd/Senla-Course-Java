package com.ruslan.console.action.order;

import com.ruslan.console.IAction;
import org.springframework.stereotype.Component;

@Component
public class OrderListByPrice extends ActionsOrder implements IAction {

    @Override
    public void execute() {
        System.out.println("Orders sorted by Price: ");
        orderService.getOrdersSortedByPrice().forEach(System.out::println);
    }
}