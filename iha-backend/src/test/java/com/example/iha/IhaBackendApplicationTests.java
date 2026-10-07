package com.example.iha;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tüm katmanları (JPA + Specification + seed verisi) Flyway migration'larıyla kurulan H2 (PostgreSQL modu) üzerinde doğrulayan duman testi.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class IhaBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void seedVerisiIleFiltreliListeCalisir() throws Exception {
        mockMvc.perform(get("/api/v1/ihalar").param("model", "BAYRAKTAR").param("sort", "seriNo,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].seriNo").value("AKS-0001"));
    }

    @Test
    void gecersizSiralamaAlaninda400() throws Exception {
        mockMvc.perform(get("/api/v1/ihalar").param("sort", "olmayanAlan"))
                .andExpect(status().isBadRequest());
    }
}
