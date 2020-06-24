package pl.zulwik.lambda.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.zulwik.lambda.dto.UserRegistrationForm;
import pl.zulwik.lambda.exception.UserAlreadyExistAuthenticationException;
import pl.zulwik.lambda.model.Material;
import pl.zulwik.lambda.model.User;
import pl.zulwik.lambda.repo.UserRepository;
import pl.zulwik.lambda.service.MessageService;
import pl.zulwik.lambda.service.UserService;

/**
 * @author Chinna
 */
@RestController
public class PagesController {
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(getClass());

    @Resource
    private MessageService messageService;

    @Autowired
    private UserService userService;

    public PagesController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout, @RequestParam(value = "errorCode", required = false) String errorCode)
            throws ServletException, IOException {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("css", "danger");
            model.addObject("msg", error);
        } else if (logout != null) {
            model.addObject("css", "success");
            model.addObject("msg", messageService.getMessage("message.logout." + logout));
        }
        model.addObject("title", "Login Page");
        model.setViewName("login");
        return model;
    }

    @GetMapping("/register")
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return new ModelAndView("register", "userRegistrationForm", new UserRegistrationForm());
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid UserRegistrationForm userRegistrationForm, BindingResult result, final HttpServletRequest request, RedirectAttributes attributes) {
        ModelAndView model = new ModelAndView("register");
        if (!result.hasErrors()) {
            try {
                userService.registerNewUser(userRegistrationForm);
                attributes.addFlashAttribute("css", "success");
                attributes.addFlashAttribute("msg", messageService.getMessage("message.regSucc"));
                model = new ModelAndView("redirect:/home");
            } catch (UserAlreadyExistAuthenticationException e) {
                logger.error(e);
                result.rejectValue("email", "message.regError");
            }
        }
        return model;
    }

    @GetMapping({"/", "/home"})
    public ModelAndView home(@RequestParam(value = "view", required = false) String view) {
        logger.info("Entering home page");
        ModelAndView model = new ModelAndView("home");
        model.addObject("title", "Home");
        model.addObject("view", view);
        return model;
    }

    @GetMapping("/logout")
    public String logout() {
        return "home";
    }

    @PostMapping("/logout")
    public String logout2() {
        return "redirect:/home";
    }

//	@GetMapping("/login?logout=success")
//	public String logout2(){
//		return "home";
//	}
/*    @GetMapping("/home2")
    public String home2(Model model){
        return "home2";
    }*/
    @GetMapping("/home2")
    public ModelAndView home2(@RequestParam(value = "view", required = false) String view) {
        logger.info("Entering home2 page");
        ModelAndView model = new ModelAndView("home2");
        model.addObject("title", "home2");
        model.addObject("view", view);
        return model;
    }
    @GetMapping("/user")
    @ResponseBody
    public String userInfo(Principal principal){
       /* User user =  userRepository.findByEmail(principal.getName());
        return user.toString();*/
        return userRepository.findByEmail(principal.getName()).toString();
    }


}