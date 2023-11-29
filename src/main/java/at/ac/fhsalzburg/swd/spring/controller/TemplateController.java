package at.ac.fhsalzburg.swd.spring.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import at.ac.fhsalzburg.swd.spring.TestBean;
import at.ac.fhsalzburg.swd.spring.dto.UserDTO;
import at.ac.fhsalzburg.swd.spring.model.User;
import at.ac.fhsalzburg.swd.spring.services.UserServiceInterface;
import at.ac.fhsalzburg.swd.spring.util.ObjectMapperUtils;

@Controller // marks the class as a web controller, capable of handling the HTTP requests. Spring
            // will look at the methods of the class marked with the @Controller annotation and
            // establish the routing table to know which methods serve which endpoints.
public class TemplateController {

	Logger logger = LoggerFactory.getLogger(TemplateController.class);

	
    // Dependency Injection
    // ----------------------------------------------------------------------

    @Autowired // To wire the application parts together, use @Autowired on the fields,
               // constructors, or methods in a component. Spring's dependency injection mechanism
               // wires appropriate beans into the class members marked with @Autowired.
    private ApplicationContext context;
    
    @Autowired
    private EntityManager entityManager;
    
   
    @Autowired
    UserServiceInterface userService;

    @Resource(name = "sessionBean") // The @Resource annotation is part of the JSR-250 annotation
                                    // collection and is packaged with Jakarta EE. This annotation
                                    // has the following execution paths, listed by Match by Name,
                                    // Match by Type, Match by Qualifier. These execution paths are
                                    // applicable to both setter and field injection.
                                    // https://www.baeldung.com/spring-annotations-resource-inject-autowire
    TestBean sessionBean;
    

    @Autowired
    TestBean singletonBean;


    // HTTP Request Mappings GET/POST/... and URL Paths
    // ----------------------------------------------------------------------


    @RequestMapping("/") // The @RequestMapping(method = RequestMethod.GET, value = "/path")
                         // annotation specifies a method in the controller that should be
                         // responsible for serving the HTTP request to the given path. Spring will
                         // work the implementation details of how it's done. You simply specify the
                         // path value on the annotation and Spring will route the requests into the
                         // correct action methods:
                         // https://springframework.guru/spring-requestmapping-annotation/#:~:text=%40RequestMapping%20is%20one%20of%20the,map%20Spring%20MVC%20controller%20methods.
    public String index(Model model, HttpSession session, @CurrentSecurityContext(expression = "authentication") Authentication authentication) {

    	logger.info("index called");
    	
        if (session == null) {
            model.addAttribute("message", "no session");
        } else {
            Integer count = (Integer) session.getAttribute("count");
            if (count == null) {
                count = Integer.valueOf(0);
            }
            count++;
            session.setAttribute("count", count);
        }
        
        // check if user is logged in
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            model.addAttribute("user",currentUserName);
        }

        model.addAttribute("message", userService.doSomething());

        model.addAttribute("halloNachricht", "welchem to SWD lab");
        
        // map list of entities to list of DTOs
        List<UserDTO> listOfUserTO = ObjectMapperUtils.mapAll(userService.getAll(), UserDTO.class);
        
        model.addAttribute("users", listOfUserTO);

        model.addAttribute("beanSingleton", singletonBean.getHashCode());

        TestBean prototypeBean = context.getBean("prototypeBean", TestBean.class);
        model.addAttribute("beanPrototype", prototypeBean.getHashCode());

        model.addAttribute("beanSession", sessionBean.getHashCode());
        
        Authentication lauthentication = SecurityContextHolder.getContext().getAuthentication();        
        model.addAttribute("authenticated", lauthentication);


        return "index";
    }
    
    @RequestMapping(value = {"/login"})
    public String login(Model model) {
    	logger.info("login called");
    	return "login";
    }
    
    @RequestMapping(value = {"/login-error"})
    public String loginError(Model model) {
    	logger.info("loginError called");
    	model.addAttribute("error","Login error");
    	return "login";
    }

    @RequestMapping(value = {"/admin/addUser"}, method = RequestMethod.GET)
    public String showAddPersonPage(Model model, @RequestParam(value = "username", required = false) String username) {
    	logger.info("showAddPersonPage called");
    	User modUser = null;
    	UserDTO userDto = new UserDTO();
    	
    	if (username!=null) {
    		modUser = userService.getByUsername(username);    		    	
    	}
    	
    	if (modUser!=null) {
    		// map user to userDTO
    		userDto = ObjectMapperUtils.map(modUser, UserDTO.class);
    	} else {
    		userDto = new UserDTO();
    	}
    	    	
        model.addAttribute("user", userDto);     

        return "addUser";
    }


    @RequestMapping(value = {"/admin/addUser"}, method = RequestMethod.POST)
    public String addUser(Model model, //
            @ModelAttribute("UserForm") UserDTO userDTO) { // The @ModelAttribute is
                                                                         // an annotation that binds
                                                                         // a method parameter or
                                                                         // method return value to a
                                                                         // named model attribute
                                                                         // and then exposes it to a
                                                                         // web view:
    	
    	logger.info("addUser called");
    	
    	// merge instances
        User user = ObjectMapperUtils.map(userDTO, User.class); 
    	
        // if user already existed in DB, new information is already merged and saved
        // a new user must be persisted (because not managed by entityManager yet)        
        if (!entityManager.contains(user)) userService.addUser(user);

        return "redirect:/";
    }
}
