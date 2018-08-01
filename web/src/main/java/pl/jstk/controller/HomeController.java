package pl.jstk.controller;

import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private static final String INFO_TEXT = "Here You shall display information containing information about newly created TO";
    protected static final String WELCOME = "This is a welcome page";

    @GetMapping(value = "/")
    public String welcome(HttpServletRequest httpServletRequest, Model model) {
        if (httpServletRequest.getUserPrincipal() != null) {
            String userLogin = httpServletRequest.getUserPrincipal().getName();
            model.addAttribute("userLogin", userLogin);
        }//TODO jak odgórnie do wszystkich przypisać do headera zmienną
        model.addAttribute(ModelConstants.MESSAGE, WELCOME);
        model.addAttribute(ModelConstants.INFO, INFO_TEXT);


        return ViewNames.WELCOME;
    }

}
