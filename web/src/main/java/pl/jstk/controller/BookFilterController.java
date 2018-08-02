package pl.jstk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.jstk.constants.ViewNames;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

import java.util.List;

@Controller
public class BookFilterController {

    @Autowired
    BookService bookService;

    /**
     * Response on query get /books/find
     * find books page
     * @param model
     * @return
     */
    @GetMapping(value = "/books/find")
    public String viewAllBooks(Model model) {

        model.addAttribute("newBook", new BookTo());
        model.addAttribute("bookList", bookService.findAllBooks());

        return ViewNames.BOOK_FINDER;
    }

    /**
     * Response on query post /books/find
     * searching books
     * @param bookTo
     * @param model
     * @return
     */
    @PostMapping(value = "/books/find")
    public String viewFilteredBooks(@ModelAttribute BookTo bookTo, Model model) {

        model.addAttribute("newBook", new BookTo());
        List<BookTo> filteredBookList = bookService.findBooksByTitleAndAuthor(bookTo.getTitle(), bookTo.getAuthors());

        model.addAttribute("bookList", filteredBookList);

        if (null != filteredBookList) {
            model.addAttribute("sizeOfBookList", filteredBookList.size());
        } else {
            model.addAttribute("sizeOfBookList", "0");
        }
        return ViewNames.BOOK_FINDER;
    }
}
