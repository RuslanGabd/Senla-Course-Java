package com.ruslan.ui.action.order;

import com.ruslan.ui.IAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OrderDetails extends ActionsOrder implements IAction {
    @Override
    public void execute() {
        while (true)
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter id order:");
                String s1 = reader.readLine();
                int id = Integer.parseInt(s1);
                System.out.println("Customer data:" + orderService.OrderDetails(id).getBuyer());
                System.out.println("Books:");
                orderService.OrderDetails(id).getListBook().forEach(System.out::println);
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NumberFormatException e) {
                System.out.println("You need enter numbers of orders ID");
            }
    }
}