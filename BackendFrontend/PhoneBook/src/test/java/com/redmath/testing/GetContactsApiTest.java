//package com.redmath.testing;
//
//
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.redmath.Main;
//import org.redmath.Model.User;
//import org.redmath.Repository.UserRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@SpringBootTest(classes = Main.class)
//@AutoConfigureMockMvc
//public class GetContactsApiTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Test
//    @Order(1)
//    @WithMockUser(username = "talha", roles = "ADMIN")
//    public void testGetUsers() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//
//    @Test
//    @Order(2)
//    @WithMockUser(username = "talha", roles = "ADMIN")
//    public void testDeleteUser() throws Exception{
//        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteUser/{id}",1))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//
//    @Test
//    @Order(4)
//    @WithMockUser(username = "talha", roles = "ADMIN")
//    public void testAddUser() throws Exception {
//        String userJson = """
//            {
//                "username": "testuser123",
//                "password": "1234",
//                "role": "ROLE_USER",
//                "email":"test@gmail.com"
//            }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
//                        .contentType("application/json")
//                        .content(userJson))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//
//    @Test
//    @Order(3)
//    public void testSaveContact() throws Exception {
//        String contactJson = """
//            {
//                "name": "Ali",
//                "number": "12345678"
//            }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
//                        .contentType("application/json")
//                        .content(contactJson))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Contact saved"));
//    }
//
//
//    @Test
//    @Order(5)
//    public void testGetContacts() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/getContacts"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    @Order(7)
//    public void testLoginWithExistingUser() throws Exception {
//        String requestBody = """
//        {
//          "username": "ahmad",
//          "password": "1234"
//        }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("ahmad"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").value(3600));
//    }
//
//
//
//
//
//}
