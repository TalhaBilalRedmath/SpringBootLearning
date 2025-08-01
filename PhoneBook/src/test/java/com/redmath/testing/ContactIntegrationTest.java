package com.redmath.testing;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.redmath.Main;
import org.redmath.Repository.ContactRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class ContactIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRep contactRepo;

    @Test
    @Order(1)
    public void testSaveContactWithValidData() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": "1234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Contact saved"));
    }

    @Test
    @Order(2)
    public void testSaveContactWithEmail() throws Exception {
        String contactJson = """
            {
                "name": "Jane Smith",
                "number": "9876543210",
                "email": "jane@example.com"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    public void testSaveContactWithBlankName() throws Exception {
        String contactJson = """
            {
                "name": "",
                "number": "1234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(4)
    public void testSaveContactWithNullName() throws Exception {
        String contactJson = """
            {
                "number": "1234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(5)
    public void testSaveContactWithInvalidNamePattern() throws Exception {
        String contactJson = """
            {
                "name": "John123",
                "number": "1234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(6)
    public void testSaveContactWithSpecialCharsInName() throws Exception {
        String contactJson = """
            {
                "name": "John@Doe",
                "number": "1234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(7)
    public void testSaveContactWithBlankNumber() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": ""
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(8)
    public void testSaveContactWithNullNumber() throws Exception {
        String contactJson = """
            {
                "name": "John Doe"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(9)
    public void testSaveContactWithInvalidNumberPattern() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": "123abc456"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(10)
    public void testSaveContactWithNumberHavingSpaces() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": "123 456 789"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(11)
    public void testSaveContactWithNumberHavingDashes() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": "123-456-789"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(12)
    public void testSaveContactWithLongValidNumber() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": "12345678901234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(13)
    public void testSaveContactWithSingleDigitNumber() throws Exception {
        String contactJson = """
            {
                "name": "John Doe",
                "number": "0"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(14)
    public void testSaveContactWithNameHavingSpaces() throws Exception {
        String contactJson = """
            {
                "name": "John Middle Doe",
                "number": "1234567890"
            }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(15)
    public void testGetContactsAfterSaving() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getContacts"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    @Order(16)
    @WithMockUser(username = "talha", roles = "ADMIN")
    public void testDeleteContactByWrongId() throws Exception {
        // Save a contact first
        String contactJson = """
        {
            "name": "Delete Me",
            "number": "55555"
        }
    """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Now delete by ID 1 (assuming first inserted record)
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete/123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(17)
    @WithMockUser(username = "talha", roles = "ADMIN")
    public void testDeleteAllContacts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/deleteAll"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(18)
    @WithMockUser(username = "talha", roles = "ADMIN")
    public void testUpdateContact() throws Exception {
        String contactJson = """
        {
            "id": 1,
            "name": "Updated Name",
            "number": "999999"
        }
    """;

        mockMvc.perform(MockMvcRequestBuilders.put("/api/updateContact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Contact updated"));
    }



}
