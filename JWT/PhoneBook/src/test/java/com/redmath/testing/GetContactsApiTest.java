package com.redmath.testing;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.redmath.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class GetContactsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testGetUsers() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @Order(2)
    public void testDeleteUser() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteUser/{id}",1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @Order(4)
    public void testAddUser() throws Exception {
        String userJson = """
            {
                "username": "testuser",
                "password": "1234",
                "role": "ROLE_USER"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType("application/json")
                        .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @Order(3)
    public void testSaveContact() throws Exception {
        String contactJson = """
            {
                "name": "Ali",
                "number": "12345678"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType("application/json")
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Contact saved"));
    }


    @Test
    @Order(5)
    public void testGetContacts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getContacts"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }





}
