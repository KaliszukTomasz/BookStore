package pl.jstk.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import pl.jstk.constants.ModelConstants;
import pl.jstk.enumerations.BookStatus;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.jstk.enumerations.BookStatus.LOAN;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
public class ViewBooksControllerTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ViewBooksController viewBooksController;

    @Mock
    BookService bookService;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(), new ViewBooksController()).build();
        MockitoAnnotations.initMocks(bookService);
        Mockito.reset(bookService);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ReflectionTestUtils.setField(viewBooksController, "bookService", bookService);
    }


    @Test
    public void testBooksPageAsAnnonymousUser() throws Exception{
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/books/add"));
        // then
        resultActions.andExpect(status().is(302))
                .andExpect(view().name("login"));

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN"})
    public void testBooksPage() throws Exception{
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(1L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("books"));
             //   .andDo(print());

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN"})
    public void testBookPageDetails() throws Exception{
        // given
        BookTo bookTo = new BookTo(1L, "Book1", "Author", LOAN);
        Mockito.when(bookService.findBookById(Mockito.any())).thenReturn(bookTo);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("books"));

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testDeleteBookAsAdmin() throws Exception{
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(1L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books/deleteBook?id=1"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("books"));

    }
    @Test (expected = NestedServletException.class)
    @WithMockUser(username = "admin", authorities = {"USER"})
    public void testDeleteBookAsUser() throws Exception{
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(0L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books/deleteBook?id=0"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("403"));

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN"})
    public void testBookAddPage() throws Exception{
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/books/add"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("addBook"));

    }
    @Test
    public void testLoginPage() throws Exception{
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/login"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("login"));

    }

    @Test
   // @WithMockUser(username = "admin", authorities = {"USER"})
    public void testViewAfterAddBook() throws Exception{
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(0L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
//        ResultActions resultActions = mockMvc.perform(post("/books"));
        ResultActions resultActions = mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("title=bok&authors=ad&status=LOAN").accept(MediaType.APPLICATION_FORM_URLENCODED));
//                .andExpect(status().isCreated());
////                .andDo(print())

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(view().name("books"));

    }

    @Test
//    @WithMockUser(username = "admin", authorities = {"USER"})
    public void testViewAfterAddBookAsUnautorizedUser() throws Exception{
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(0L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
//        ResultActions resultActions = mockMvc.perform(post("/books"));
        ResultActions resultActions = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("title=bok&authors=ad&status=LOAN")
                .accept(MediaType.APPLICATION_FORM_URLENCODED));
//                .andExpect(status().isCreated());
////                .andDo(print())

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(view().name("books"));

    }



}
