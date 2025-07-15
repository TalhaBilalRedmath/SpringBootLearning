package org.redmath.Service;

import org.redmath.Model.Contact;
import org.redmath.Repository.ContactRep;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListService {
    private final ContactRep repo;

    public ListService(ContactRep repo){
        this.repo = repo;
        System.out.println("Repo Done!");
        System.out.println("Service Created");
    }

    public void updateContact(Contact obj){
        repo.save(obj);
    }

    public void addToBook(Contact cont){
        repo.save(cont);
    }
    public void deleteContact(String id){
        int value = Integer.parseInt(id);
        repo.deleteById(value);
    }
    public List<Contact> getAllContacts(){
        return repo.findAll();
    }
    public void deleteAll() {
        repo.deleteAll();
    }
}
