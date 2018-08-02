package pl.jstk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;
import pl.jstk.entity.UserEntity;
import pl.jstk.enumerations.BookStatus;
import pl.jstk.service.BookService;
import pl.jstk.service.UserService;
import pl.jstk.to.BookTo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class ViewBooksController {

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    /**
     * Response to query get /books
     * books list page
     * @param model
     * @return
     */
    @GetMapping(value = "/books")
    public String viewAllBooks(Model model) {
    model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOKS;
    }

    /**
     * Response to query get /books/book
     * book details page
     * @param id
     * @param model
     * @return
     */
    @GetMapping(value = "/books/book")
    public String viewDetailOfBookId(@RequestParam Long id, Model model) {

        model.addAttribute("book", bookService.findBookById(id));
        return ViewNames.BOOK;
    }

    /**
     * Response to query get /403 (access denied)
     * 403 access denied page
     * @param model
     * @return
     */
    @GetMapping("/403")
    public String viewAccessDenied(Model model){
        model.addAttribute("error", "Access Denied! You are only a user, " +
                "you have to be ADMIN to delete books! This is a 403 fail!");
        return ViewNames.ACCESS_DENIED;
    }

    /**
     * Response to query get /books/deleteBook
     * Deleting book
     * @param id
     * @param model
     * @return
     */
    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/books/deleteBook")
    public String viewDeleteCompleted(@RequestParam Long id, Model model) {
        bookService.deleteBook(id);
        model.addAttribute("deletedId", id);
        model.addAttribute("bookList", bookService.findAllBooks());
        return ViewNames.BOOKS;
    }

    /**
     * Response to query get /books/add
     * adding book page
     * @param model
     * @return
     */
    @GetMapping(value = "/books/add")
    public String viewAddBook(Model model) {
        model.addAttribute("newBook", new BookTo());
        return ViewNames.ADD_BOOK;
    }

    /**
     * Response to query post /books
     * creating new book in database
     * @param bookTo
     * @param model
     * @return
     */
    @PostMapping("/books")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String viewAfterAddBook(@ModelAttribute @Valid BookTo bookTo, Model model) {

        bookService.saveBook(bookTo);

        model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOKS;
    }

    /**
     * Response to query get /login
     * login page
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String viewLoginPage(Model model) {

        return ViewNames.LOGIN;
    }
}
