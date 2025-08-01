package org.redmath.Controller;
import org.redmath.Model.User;
import org.redmath.Service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private UserService us;

    public UserController(UserService us){
        this.us = us;
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return us.getUsers();
    }

    @PostMapping("/users/add")
    public void add(@RequestBody User u){
        us.save(u);
    }

}
