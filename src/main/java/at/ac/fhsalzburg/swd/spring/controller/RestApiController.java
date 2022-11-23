package at.ac.fhsalzburg.swd.spring.controller;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.fhsalzburg.swd.spring.dto.UserDTO;
import at.ac.fhsalzburg.swd.spring.model.User;
import at.ac.fhsalzburg.swd.spring.services.UserServiceInterface;
import at.ac.fhsalzburg.swd.spring.util.ObjectMapperUtils;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class RestApiController {	
	
	@Autowired
    private EntityManager entityManager;

    @Autowired private UserServiceInterface userService;

    // test with: curl http://localhost:8080/api/users -H 'Accept: application/json' -H "Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE2NjQzMDc0NDksImV4cCI6MTY5NTg0MzQ0OSwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoiYWRtaW4iLCJ1c2VybmFtZSI6ImFkbWluIn0.curjpEf0q9S43s5EPLB9Pk7VXZEex0onsK2xr74QOak"
    @GetMapping("/users")
    public @ResponseBody List<UserDTO> allUsers() {
    	
    	// map list of entities to list of DTOs
        List<UserDTO> listOfUserTO = ObjectMapperUtils.mapAll(userService.getAll(), UserDTO.class);

        return listOfUserTO;
    }

    // test with: curl http://localhost:8080/api/users/admin -H 'Accept: application/json' -H "Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE2NjQzMDc0NDksImV4cCI6MTY5NTg0MzQ0OSwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoiYWRtaW4iLCJ1c2VybmFtZSI6ImFkbWluIn0.curjpEf0q9S43s5EPLB9Pk7VXZEex0onsK2xr74QOak"
    @RequestMapping(value = {"/users/{username}"}, method = RequestMethod.GET)
    public @ResponseBody UserDTO getUser(@PathVariable String username) {
        User user = userService.getByUsername(username);
        // map user to userDTO
        UserDTO userDTO = ObjectMapperUtils.map(user, UserDTO.class);
        return userDTO;
    }

    // test with: curl -X PUT http://localhost:8080/api/users/fromrest -H "Content-Type: application/json" -H 'Accept: application/json' -H "Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE2NjQzMDc0NDksImV4cCI6MTY5NTg0MzQ0OSwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoiYWRtaW4iLCJ1c2VybmFtZSI6ImFkbWluIn0.curjpEf0q9S43s5EPLB9Pk7VXZEex0onsK2xr74QOak" -d '{"username":"fromrest","fullname":"created by rest","tel":"+00000000","birthDate":"2022-09-30T00:00:00.000+00:00","password":"fromrest","jwtToken":null,"email":"fromrest@acme.at"}'
    @RequestMapping(value = {"/users/{username}"}, method = RequestMethod.PUT)
    public String setUser(@RequestBody UserDTO userDTO) {
    	User user = ObjectMapperUtils.map(userDTO, User.class); 
    	
        // if user already existed in DB, new information is already merged and saved
        // a new user must be persisted (because not managed by entityManager yet)        
        if (!entityManager.contains(user)) userService.addUser(user);

        return "redirect:/api/users";
    }

    // test with curl -X DELETE http://localhost:8080/api/users/fromrest -H "Content-Type: application/json" -H 'Accept: application/json' -H "Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE2NjQzMDc0NDksImV4cCI6MTY5NTg0MzQ0OSwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoiYWRtaW4iLCJ1c2VybmFtZSI6ImFkbWluIn0.curjpEf0q9S43s5EPLB9Pk7VXZEex0onsK2xr74QOak"
    @RequestMapping(value = {"/users/{username}"}, method = RequestMethod.DELETE)
    public String delete(@PathVariable String username) {
   
    	userService.deleteUser(username);
   
        return "redirect:/api/users";
    }
 
}
