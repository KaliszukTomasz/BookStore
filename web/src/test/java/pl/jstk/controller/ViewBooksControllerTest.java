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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private FilterChainProxy springSeciurityFilterChain;


    @Mock
    BookService bookService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(bookService);
        Mockito.reset(bookService);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSeciurityFilterChain)
                .defaultRequest(get("/").with(testSecurityContext()))
                .build();
        ReflectionTestUtils.setField(viewBooksController, "bookService", bookService);
    }


    @Test
    @WithAnonymousUser
    public void testBooksPageAsAnnonymousUser() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/books"));
        // then
        resultActions.andExpect(status().is(302)).
                andExpect(redirectedUrl("http://localhost/login"));
        Mockito.verifyNoMoreInteractions(bookService);


    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testBooksPage() throws Exception {
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
        Mockito.verify(bookService, Mockito.times(1))
                .findAllBooks();


    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void test403Page() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/403"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("403"));
        Mockito.verifyNoMoreInteractions(bookService);

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testBookPageDetails() throws Exception {
        // given
        BookTo bookTo = new BookTo(1L, "Book1", "Author", LOAN);
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(bookTo);
        Mockito.when(bookService.findBookById(Mockito.any())).thenReturn(bookTo);
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books/book?id=1"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attribute("book", hasProperty("id", is(bookTo.getId()))))
                .andExpect(model().attribute("book", hasProperty("title", is(bookTo.getTitle()))))
                .andExpect(model().attribute("book", hasProperty("authors", is(bookTo.getAuthors()))))
                .andExpect(model().attribute("book", hasProperty("status", is(bookTo.getStatus()))));

        Mockito.verify(bookService, Mockito.times(1)).findBookById(Mockito.any());

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testDeleteBookAsAdmin() throws Exception {
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(1L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books/deleteBook?id=1"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("books"));
        Mockito.verify(bookService, Mockito.times(1)).findAllBooks();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER"})
    public void testDeleteBookAsUser() throws Exception {
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(0L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books/deleteBook?id=0"));
        // then
        resultActions.andExpect(status().isForbidden());
        Mockito.verifyNoMoreInteractions(bookService);

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void testBookAddPage() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/books/add"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("addBook"));
        Mockito.verifyNoMoreInteractions(bookService);

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testBookDetailsPage() throws Exception {
        // given
        BookTo bookTo = new BookTo(0L, "Book1", "Author", LOAN);
        Mockito.when(bookService.findBookById(Mockito.any())).thenReturn(bookTo);

        // when
        ResultActions resultActions = mockMvc.perform(get("/books/book?id=1"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("book"));
        Mockito.verify(bookService, Mockito.times(1)).findBookById(Mockito.any());
    }

    @Test
    public void testLoginPage() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/login"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("login"));
        Mockito.verifyNoMoreInteractions(bookService);

    }

    @Test
    public void testWelcomePage() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("welcome"));
        Mockito.verifyNoMoreInteractions(bookService);

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER"})
    public void testViewAfterAddBook() throws Exception {
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(0L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(post("/books").
                contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("title=bok&authors=ad&status=LOAN").accept(MediaType.APPLICATION_FORM_URLENCODED));
        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(view().name("books"));
        Mockito.verify(bookService, Mockito.times(1)).findAllBooks();

    }

    @Test
    @WithAnonymousUser
    public void testViewAfterAddBookAsUnautorizedUser() throws Exception {
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(0L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("title=bok&authors=ad&status=LOAN")
                .accept(MediaType.APPLICATION_FORM_URLENCODED));
        // then
        resultActions.andExpect(status().is3xxRedirection());
        Mockito.verifyNoMoreInteractions(bookService);
    }


}
