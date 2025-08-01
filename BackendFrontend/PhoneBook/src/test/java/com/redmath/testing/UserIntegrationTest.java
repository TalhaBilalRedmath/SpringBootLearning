package com.redmath.testing;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.redmath.Main;
import org.redmath.Model.User;
import org.redmath.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddUserWithValidData() throws Exception {
        String userJson = """
                    {
                        "username": "testuser1",
                        "password": "password123",
                        "role": "ROLE_USER",
                        "email": "test1@gmail.com"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddUserWithAdminRole() throws Exception {
        String userJson = """
                    {
                        "username": "adminuser",
                        "password": "adminpass",
                        "role": "ROLE_ADMIN",
                        "email": "admin@gmail.com"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    @Test
//    @Order(3)
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    public void testAddUserWithEmptyUsername() throws Exception {
//        String userJson = """
//            {
//                "username": "",
//                "password": "password123",
//                "role": "ROLE_USER",
//                "email": "test@gmail.com"
//            }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userJson))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }

//    @Test
//    @Order(4)
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    public void testAddUserWithNullUsername() throws Exception {
//        String userJson = """
//            {
//                "password": "password123",
//                "role": "ROLE_USER",
//                "email": "test@gmail.com"
//            }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userJson))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }


//    @Test
//    @Order(6)
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    public void testAddUserWithEmptyEmail() throws Exception {
//        String userJson = """
//            {
//                "username": "testuser",
//                "password": "password123",
//                "role": "ROLE_USER",
//                "email": ""
//            }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userJson))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }

    @Test
    @Order(8)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddUserWithLongUsername() throws Exception {
        String userJson = """
                    {
                        "username": "verylongusernamethatmightcauseissuesifnothandledproperly",
                        "password": "password123",
                        "role": "ROLE_USER",
                        "email": "longuser@gmail.com"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddUserWithSpecialCharsInUsername() throws Exception {
        String userJson = """
                    {
                        "username": "user@123",
                        "password": "password123",
                        "role": "ROLE_USER",
                        "email": "special@gmail.com"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(10)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetUsersAfterAdding() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    @Order(11)
    public void testLoginWithAddedUser() throws Exception {
        String loginJson = """
                    {
                        "username": "testuser1",
                        "password": "password123"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    @Order(12)
    public void testLoginWithWrongPassword() throws Exception {
        String loginJson = """
                    {
                        "username": "testuser1",
                        "password": "wrongpassword"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(13)
    public void testLoginWithNonExistentUser() throws Exception {
        String loginJson = """
                    {
                        "username": "nonexistent",
                        "password": "password123"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(14)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteUserById() throws Exception {
        // First, create a user to delete
        User userToDelete = new User();
        userToDelete.setUsername("deleteme");
        userToDelete.setEmail("delete@test.com");
        userToDelete.setPassword("password");
        userToDelete.setRole("ROLE_USER");
        User savedUser = userRepo.save(userToDelete);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteUser/{id}", savedUser.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(15)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteNonExistentUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteUser/{id}", 99999))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // Additional tests similar to your existing implementation
    @Test
    @Order(16)
    @WithMockUser(username = "talha", roles = "ADMIN")
    public void testGetUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    @Test
//    @Order(18)
//    @WithMockUser(username = "talha", roles = "ADMIN")
//    public void testAddUserExactFormat() throws Exception {
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

    @Test
    @Order(19)
    public void testLoginWithExistingUser() throws Exception {
        String requestBody = """
                {
                  "username": "ahmad",
                  "password": "1234"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("ahmad"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").value(3600));
    }

    @Test
    @Order(20)
    @WithMockUser(username = "talha", roles = "ADMIN")
    public void testAddMultipleUsersWithDifferentRoles() throws Exception {
        String userJson1 = """
                    {
                        "username": "moderator1",
                        "password": "modpass",
                        "role": "ROLE_MODERATOR",
                        "email":"mod1@gmail.com"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType("application/json")
                        .content(userJson1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        String userJson2 = """
                    {
                        "username": "guest1",
                        "password": "guestpass",
                        "role": "ROLE_GUEST",
                        "email":"guest1@gmail.com"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/users/add")
                        .contentType("application/json")
                        .content(userJson2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(21)
    public void testLoginFail() throws Exception {
        // Test login with testuser123
        String requestBody1 = """
                {
                  "username": "testuser123",
                  "password": "1234"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    @Order(23)
    @WithMockUser(username = "talha", roles = "ADMIN")
    public void testDeleteMultipleUsers() throws Exception {
        // Create users to delete
        User user1 = new User();
        user1.setUsername("deleteme1");
        user1.setEmail("delete1@test.com");
        user1.setPassword("password");
        user1.setRole("ROLE_USER");
        User savedUser1 = userRepo.save(user1);

        User user2 = new User();
        user2.setUsername("deleteme2");
        user2.setEmail("delete2@test.com");
        user2.setPassword("password");
        user2.setRole("ROLE_USER");
        User savedUser2 = userRepo.save(user2);

        // Delete first user
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteUser/{id}", savedUser1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Delete second user
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteUser/{id}", savedUser2.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
