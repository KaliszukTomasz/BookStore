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

    @GetMapping(value = "/books")
    public String viewAllBooks(Model model) {

//        String userLogin = httpServletRequest.getUserPrincipal().getName();
////        UserEntity loggedUser = userService.findByUserName(userLogin);
//        model.addAttribute("userLogin", userLogin);
        model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOKS;
    }

    @GetMapping(value = "/books/book")
    public String viewDetailOfBookId(@RequestParam Long id, Model model) {

        model.addAttribute("book", bookService.findBookById(id));
        return ViewNames.BOOK;
    }

    @GetMapping("/403")
    public String viewAccessDenied(){
        return ViewNames.ACCESS_DENIED;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/books/deleteBook")
    public String viewDeleteCompleted(@RequestParam Long id, Model model) {
        bookService.deleteBook(id);
        model.addAttribute("deletedId", id);
        model.addAttribute("bookList", bookService.findAllBooks());
        return ViewNames.BOOKS;
    }

    @GetMapping(value = "/books/add")
    public String viewAddBook(Model model) {
        model.addAttribute("newBook", new BookTo());
        return ViewNames.ADD_BOOK;
    }

    @PostMapping("/books")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String viewAfterAddBook(@ModelAttribute @Valid BookTo bookTo, Model model) {

        bookService.saveBook(bookTo);

        model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOKS;
    }

    @GetMapping("/login")
    public String viewLoginPage(Model model) {

        return ViewNames.LOGIN;
    }
}
