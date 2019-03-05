package com.glovoapp.backender.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.glovoapp.backender.API;
import com.glovoapp.backender.Order;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = API.class)

public class IntegrationOrderFinderTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("the return should be food, vip and location grouped by 500 Meters Slots and the glover must have a MOTORCYCLE and GLOVE BOX")
    public void integrationOrderFind() throws Exception {
        this.mockMvc.perform(get("/orders/courier-1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is("order-11")))
                .andExpect(jsonPath("$.[0].description", equalToIgnoringCase("Lemon Pie")))
                
                .andExpect(jsonPath("$.[1].id", is("order-2")))
                .andExpect(jsonPath("$.[1].description", equalToIgnoringCase("I want a Flamingo")))

                .andExpect(jsonPath("$.[2].id", is("order-9")))
                .andExpect(jsonPath("$.[2].description", equalToIgnoringCase("Notebook")))

                .andExpect(jsonPath("$.[3].id", is("order-3")))
                .andExpect(jsonPath("$.[3].description", equalToIgnoringCase("I want a big CaKe")));

           
    }
}
