package org.redmath.Controller;

import jakarta.validation.Valid;
import org.redmath.Model.Contact;
import org.redmath.Service.ListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class Controller {
    //    @Autowired
    private ListService list;

    public Controller(ListService list) {
        this.list = list;
        System.out.println("Controller Created");
    }

    @PutMapping("/updateContact")
    public Map<String, String> updateContact(@Valid @RequestBody Contact updatedContact) {
        list.updateContact(updatedContact);
        return Map.of("message", "Contact updated");
    }

    @PostMapping("/saveContact")
    public Map<String, String> add(@Valid @RequestBody Contact cont) {
        list.addToBook(cont);
        return Map.of("message", "Contact saved");
    }

    @GetMapping("/getContacts")
    public List<Contact> getContact() {
        return list.getAllContacts();
    }

    @DeleteMapping("/deleteContact/{id}")
    public Map<String, String> DeleteContact(@PathVariable String id) {
        list.deleteContact(id);
        return Map.of("message", "Contact deleted");
    }

    @DeleteMapping("/deleteAll")
    public void deleteAll() {
        list.deleteAll();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


}
