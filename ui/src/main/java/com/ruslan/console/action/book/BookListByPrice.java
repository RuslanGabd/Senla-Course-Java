package com.ruslan.console.action.book;

import com.ruslan.console.IAction;
import org.springframework.stereotype.Component;

@Component
public class BookListByPrice extends ActionsBook implements IAction {

    @Override
    public void execute() {
        System.out.println("Books sorted by price: ");
        bookService.getBooksSortedByPrice().forEach(System.out::println);
    }
}