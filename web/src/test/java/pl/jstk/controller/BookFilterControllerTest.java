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
import org.springframework.security.test.context.support.WithMockUser;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.jstk.enumerations.BookStatus.LOAN;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
public class BookFilterControllerTest {

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
    @WithMockUser(username = "admin", authorities = { "ADMIN"})
    public void testBooksPage() throws Exception{
        // given
        List<BookTo> bookList = new ArrayList<>();
        bookList.add(new BookTo(1L, "Book1", "Author", LOAN));
        Mockito.when(bookService.findAllBooks()).thenReturn(bookList);
        // when
        ResultActions resultActions = mockMvc.perform(get("/books/find"));
        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("bookFinder"));
        //   .andDo(print());

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
        ResultActions resultActions = mockMvc.perform(post("/books/find").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("title=bok&authors=ad&status=LOAN").accept(MediaType.APPLICATION_FORM_URLENCODED));
//                .andExpect(status().isCreated());
////                .andDo(print())

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("bookFinder"));

    }

}
