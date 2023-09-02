package com.ruslan.services;

import com.ruslan.data.book.Book;
import com.ruslan.data.book.BookStatus;
import com.ruslan.data.order.Order;
import com.ruslan.data.repository.BookRepository;
import com.ruslan.data.repository.OrderRepository;
import com.ruslan.data.repository.RequestRepository;
import com.ruslan.services.sinterface.IBookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;


public class BookService implements IBookService {
    private static final Logger logger = LogManager.getLogger();
    private static final String fileName = "Books.csv";
    private final BookRepository bookRepository;
    private final RequestRepository requestRepository;
    private final OrderRepository orderRepository;

    private final Path path = Paths.get("config.properties");

    private final String numberOfMonths = "NumberOfMonths";

    private final String autoRequestsClosed = "AutoRequestsClosed";

    Properties properties = new Properties();

    public BookService(BookRepository bookRepository, OrderRepository orderRepository, RequestRepository requestRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public Book createBook(String title, String author, int price, LocalDate datePublication) {
        Book bk = new Book(title, author, price, datePublication);
        bookRepository.saveBook(bk);
        System.out.println("Created book: " + bk);
        return bk;
    }

    @Override
    public void changeStatusBook(int bookId, BookStatus status) {
        bookRepository.updateStatus(bookId, status);
        System.out.println("Book id=" + bookId + " changed status " + status);
    }

    @Override
    public void removeBookFromStock(int bookId) {
        bookRepository.updateStatus(bookId, BookStatus.OUT_OF_STOCK);
        System.out.println("Book id=" + bookId + " status changed OUT_OF_STOCK");
    }

    public void addBookToStockAndCancelRequests(int bookId) {
        bookRepository.updateStatus(bookId, BookStatus.IN_STOCK);
        try {
            if (getAutoRequestsClosedIfBookAddStock().equals("on")) {
                cancelRequestsByIdBook(bookId);
            }
        } catch (IOException e) {
            System.out.println("Something went wrong.");
            logger.error("Something went wrong.", e);
        }
        System.out.println("Book " + bookId + " add to stock");
    }

    public void cancelRequestsByIdBook(int bookId) {
        requestRepository.getRequestList().stream()
                .filter(request ->
                        request.getBook()
                                .equals(bookRepository.getById(bookId)))
                .forEach(request -> {
                    requestRepository
                            .removeRequest(request.getId());
                    System.out.println("Request id=" + request.getId() + " canceled");
                });
    }


    //Description of the book.
    public String getDescriptionOfBook(int bookId) {
        return "Description for book with id=" + bookId + ":\n" +
                bookRepository.getById(bookId).getDescription();
    }

    //List of "stale" books which were not sold for more than 6 months. (sort by date of receipt, by price);

    public List<Book> getStaleBooks() {
        List<Book> staleBookList = bookRepository.getBooksList();
        List<Order> orderList = null;
        try {
            orderList = orderRepository.getCompletedOrdersForPeriod(
                    LocalDate.now().minusMonths(getNumberMonthsOfStaleBooks()),
                    LocalDate.now());
        } catch (IOException e) {
            System.out.println("Something went wrong.");
            logger.error("Something went wrong.", e);
        }
        orderList.forEach(order -> staleBookList.removeAll(order.getListBook()));
        return staleBookList;
    }

    public List<Book> getStaleBooksSortedByDate() {
        List<Book> sortedBooks = getStaleBooks();
        sortedBooks.sort(Comparator.comparing(Book::getDatePublication));
        return sortedBooks;
    }

    public List<Book> getStaleBooksSortedByPrice() {
        List<Book> sortedBooks = getStaleBooks();
        sortedBooks.sort(Comparator.comparing(Book::getPrice));
        return sortedBooks;
    }

    //   List of books (sort alphabetically, by date of publication, by price, by stock availability);


    public List<Book> getBooksSortedByTitleAlphabetically() {
        List<Book> bookList = bookRepository.getBooksList();
        bookList.sort((o1, o2) -> CharSequence.compare(o2.getTitle(), o1.getTitle()));
        return bookList;
    }


    public List<Book> getBooksSortedByDatePublication() {
        List<Book> bookList = bookRepository.getBooksList();
        bookList.sort(Comparator.comparing(Book::getDatePublication));
        return bookList;
    }


    public List<Book> getBooksSortedByPrice() {
        List<Book> bookList = bookRepository.getBooksList();
        bookList.sort(Comparator.comparingInt(Book::getPrice));
        return bookList;
    }

    public List<Book> getBooksSortedByStatus() {
        List<Book> bookList = bookRepository.getBooksList();
        bookList.sort(Comparator.comparing(Book::getStatus));
        return bookList;
    }

    public List<Book> getBooksSortedByAuthor() {
        List<Book> sortedBooks = bookRepository.getBooksList();
        sortedBooks.sort((o1, o2) -> CharSequence.compare(o2.getAuthor(), o1.getAuthor()));
        return sortedBooks;
    }

    public void writeBookToFile(int id) {
        File bookFile = new File(fileName);
        FileOutputStream bookCSV;
        ObjectOutputStream oos;
        List<Book> bookList;

        try {
            bookFile.createNewFile();
            bookList = getBookListFromFile();
            bookList.add(BookRepository.getInstance().getById(id));

            bookCSV = new FileOutputStream(bookFile);
            oos = new ObjectOutputStream(bookCSV);
            oos.writeObject(bookList);
            oos.close();
        } catch (NullPointerException e) {
            System.out.println("Could not get data from file.");
            logger.error("Could not get data to file", e);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Please repeat operation");
            logger.error("File not found", e);
        } catch (IOException e) {
            System.out.println("Could not write data to file");
            logger.error("Could not write data from file", e);
        }
    }

    public List<Book> getBookListFromFile() {
        FileInputStream fis;
        ObjectInputStream ois;
        List<Book> bookList;

        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            bookList = (List<Book>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Class 'Book' not found!");
            logger.error("Class 'Book' not found!", e);
            return null;
        } catch (EOFException e) {
            System.out.println("File Books.csv was empty");
            return bookList = new ArrayList<>();
        } catch (StreamCorruptedException e) {
            System.out.println("Uncorrected data in file. Data was erased");
            return bookList = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Could not read data from file");
            logger.error("Could not read data from file", e);
            return null;
        }
        return bookList;
    }

    public Book getBookFromFile(int id) {
        return getBookListFromFile()
                .stream()
                .filter(book ->
                        book.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void setNumberMonthsOfStaleBooks(String number)  {
        try {
            properties.load(Files.newInputStream(path));
            properties.setProperty(numberOfMonths, number);
            properties.store(Files.newOutputStream(path), "This file stores the value number of months to mark a book as \"stale\" " +
                    "and Switch for mark requests as completed when adding a book to the warehouse");
        } catch (IOException e) {
            System.out.println("Something went wrong.");
            logger.error("Something went wrong.", e);
        }

    }

    public void setAutoClosedRequestIfBookAddToStock(String switchOnOff) {
        try {
            properties.load(Files.newInputStream(path));
            properties.setProperty(autoRequestsClosed, switchOnOff);
            properties.store(Files.newOutputStream(path),
                    "This file stores the value number of months to mark a book as \"stale\" " +
                            "and Switch for mark requests as completed when adding a book to the warehouse");
        } catch (IOException e) {
            System.out.println("Something went wrong.");
            logger.error("Something went wrong.", e);
        }

    }

    public Integer getNumberMonthsOfStaleBooks() throws IOException {
        properties.load(Files.newInputStream(path));
        return Integer.parseInt(properties.getProperty(numberOfMonths));
    }

    public String getAutoRequestsClosedIfBookAddStock() throws IOException {
        properties.load(Files.newInputStream(path));
        return properties.getProperty(autoRequestsClosed);
    }
}