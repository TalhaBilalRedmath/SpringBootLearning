package org.redmath.Repository;

import org.redmath.Model.Contact;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
public class People {
    private ArrayList<Contact> phoneBook = new ArrayList<>();
    private int numberOfContacts = 0;

    public People() {
        System.out.println("Repo Created");
    }

    public void addToBook(Contact cont){
        phoneBook.add(cont);
        numberOfContacts++;
    }

    public int getNumberOfContacts() {
        return numberOfContacts;
    }

    public void getAllContacts(ArrayList<Contact> ok){
        ok.addAll(phoneBook);
    }

    public void deleteContact(String id){
        int value = Integer.parseInt(id);
        phoneBook.removeIf(c -> c.getid() == value);
    }

    public void updateContact(Contact obj){
        for (Contact c : phoneBook){
            if (c.getid() == obj.getid()){
                c.name = obj.name;
                c.number = obj.number;
                break;
            }
        }
    }

    public Contact getFromBook(String namee){
        for(Contact c : phoneBook){
            if(c.name.equals(namee)){
                return c;
            }
        }
        return new Contact(namee, "");
    }
}
