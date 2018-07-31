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

@Controller
public class ViewBooksController {

    @Autowired
    BookService bookService;

    @GetMapping(value = "/books")
    public String viewAllBooks(Model model) {

        model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOKS;
    }

    @GetMapping(value = "/books/book")
    public String viewDetailOfBookId(@RequestParam Long id, Model model) {

        model.addAttribute("book", bookService.findBookById(id));
        return ViewNames.BOOK;
    }

    @GetMapping(value = "/books/deleteBook")
    public String viewDeleteCompleted(@RequestParam Long id, Model model) {

        model.addAttribute("bookId", id);
        return ViewNames.DELETE_BOOK;
    }

//    @GetMapping(value = "/books/deleteBook/confirmed")
//    public String viewDeleteConfirmed(@RequestParam Long id, Model model) {
//
//        bookService.deleteBook(id);
//        return ViewNames.BOOKS;
//    }
//czy da się przekazać metodą daną nie tylko do widoku, ale do dalszego zdarzenia?
//    @GetMapping(value = "/books/deleteBook/confirmed")
//    public String viewDeleteCompleted(@RequestParam Long id, Model model){
//@{/books/deleteBook/confirmed(bookId= ${bookId})}
//        bookse
//    }



}
