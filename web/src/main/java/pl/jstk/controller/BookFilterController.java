package pl.jstk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

import java.awt.print.Book;

@Controller
public class BookFilterController {

    @Autowired
    BookService bookService;


    @GetMapping(value = "/books/find")
    public String viewAllBooks( Model model) {

        model.addAttribute("newBook", new BookTo());
        model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOK_FINDER;
    }


    @PostMapping(value = "/books/find")
    public String viewFilteredBooks(@ModelAttribute BookTo bookTo, Model model) {

        model.addAttribute("newBook", new BookTo());
        model.addAttribute("bookList", bookService.findBooksByTitleAndAuthor(bookTo.getTitle(), bookTo.getAuthors()));

        return ViewNames.BOOK_FINDER;
    }

//    @PostMapping(value = "/books/find")
//    public String viewFilteredBooks(@ModelAttribute BookTo book, Model model){
//
//        model.addAttribute("bookList", bookService.findBooksByAuthor(book.getAuthors()));
//
//        return ViewNames.BOOK_FINDER;
//    }
//    @GetMapping(value = "/books/book")
//    public String viewDetailOfBookId(@RequestParam("id") Long id,  Model model){
//
//        model.addAttribute("book", bookService.findBookById(id));
//        return ViewNames.BOOK;
//    }

}
