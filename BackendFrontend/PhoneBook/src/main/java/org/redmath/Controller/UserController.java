package org.redmath.Controller;
import org.redmath.Model.User;
import org.redmath.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private UserService us;

    public UserController(UserService us) {
        this.us = us;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return us.getUsers();
    }

    @PostMapping("/users/add")
    public void add(@RequestBody User u) {
        us.save(u);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean deleted = us.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + id + " not found");
        }
    }
}
