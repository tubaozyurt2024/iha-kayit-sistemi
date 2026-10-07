package com.example.iha.controller;

import com.example.iha.dto.DurumUpdateRequest;
import com.example.iha.dto.IhaCreateRequest;
import com.example.iha.dto.IhaResponse;
import com.example.iha.dto.IhaUpdateRequest;
import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import com.example.iha.exception.BusinessRuleException;
import com.example.iha.exception.DuplicateSeriNoException;
import com.example.iha.exception.IhaNotFoundException;
import com.example.iha.service.IhaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IhaController.class)
class IhaControllerTest {

    private static final String BASE = "/api/v1/ihalar";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IhaService ihaService;

    private IhaCreateRequest gecerliCreate() {
        return IhaCreateRequest.builder()
                .seriNo("TB2-0100")
                .model("Bayraktar TB2")
                .uretici("Baykar")
                .kategori(IhaKategori.MALE)
                .durum(IhaDurum.AKTIF)
                .maksIrtifaMetre(7620)
                .menzilKm(150.0)
                .havadaKalmaSuresiSaat(27.0)
                .azamiKalkisAgirligiKg(700.0)
                .uretimTarihi(LocalDate.of(2020, 1, 1))
                .build();
    }

    private IhaResponse response(Long id) {
        return IhaResponse.builder()
                .id(id)
                .seriNo("TB2-0100")
                .model("Bayraktar TB2")
                .uretici("Baykar")
                .kategori(IhaKategori.MALE)
                .durum(IhaDurum.AKTIF)
                .maksIrtifaMetre(7620)
                .menzilKm(150.0)
                .havadaKalmaSuresiSaat(27.0)
                .azamiKalkisAgirligiKg(700.0)
                .uretimTarihi(LocalDate.of(2020, 1, 1))
                .build();
    }

    @Test
    void create_201VeLocationDoner() throws Exception {
        when(ihaService.create(any())).thenReturn(response(7L));

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliCreate())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/ihalar/7")))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.seriNo").value("TB2-0100"));
    }

    @Test
    void create_gecersizGovdede400VeAlanHatalari() throws Exception {
        IhaCreateRequest request = gecerliCreate();
        request.setSeriNo(" ");
        request.setMenzilKm(-5.0);
        request.setUretimTarihi(LocalDate.now().plusDays(1));

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.seriNo").exists())
                .andExpect(jsonPath("$.validationErrors.menzilKm").exists())
                .andExpect(jsonPath("$.validationErrors.uretimTarihi").exists());

        verifyNoInteractions(ihaService);
    }

    @Test
    void create_gecersizEnumDegerinde400() throws Exception {
        String json = objectMapper.writeValueAsString(gecerliCreate()).replace("\"MALE\"", "\"UCAN_HALI\"");

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_seriNoCakisirsa409() throws Exception {
        when(ihaService.create(any())).thenThrow(new DuplicateSeriNoException("TB2-0100"));

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliCreate())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void getById_200() throws Exception {
        when(ihaService.getById(7L)).thenReturn(response(7L));

        mockMvc.perform(get(BASE + "/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Bayraktar TB2"))
                .andExpect(jsonPath("$.uretimTarihi").value("2020-01-01"));
    }

    @Test
    void getById_yoksa404VeTekTipHata() throws Exception {
        when(ihaService.getById(5L)).thenThrow(new IhaNotFoundException(5L));

        mockMvc.perform(get(BASE + "/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("İHA bulunamadı: id=5"))
                .andExpect(jsonPath("$.path").value("/api/v1/ihalar/5"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void list_filtreVeSayfalamaParametreleriniIletir() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(ihaService.list(eq(IhaDurum.AKTIF), eq(IhaKategori.MALE), eq("tb"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response(1L)), pageable, 1));

        mockMvc.perform(get(BASE)
                        .param("durum", "AKTIF")
                        .param("kategori", "MALE")
                        .param("model", "tb")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void list_gecersizFiltreDegerinde400() throws Exception {
        mockMvc.perform(get(BASE).param("durum", "YOK"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Geçersiz parametre değeri: durum=YOK"));
    }

    @Test
    void update_200() throws Exception {
        IhaUpdateRequest request = IhaUpdateRequest.builder()
                .seriNo("TB2-0100").model("Bayraktar TB2").uretici("Baykar")
                .kategori(IhaKategori.MALE).durum(IhaDurum.BAKIMDA)
                .maksIrtifaMetre(7620).menzilKm(150.0).havadaKalmaSuresiSaat(27.0)
                .azamiKalkisAgirligiKg(700.0).uretimTarihi(LocalDate.of(2020, 1, 1))
                .build();
        IhaResponse updated = response(7L);
        updated.setDurum(IhaDurum.BAKIMDA);
        when(ihaService.update(eq(7L), any())).thenReturn(updated);

        mockMvc.perform(put(BASE + "/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durum").value("BAKIMDA"));
    }

    @Test
    void updateDurum_200() throws Exception {
        IhaResponse updated = response(7L);
        updated.setDurum(IhaDurum.PASIF);
        when(ihaService.updateDurum(eq(7L), any())).thenReturn(updated);

        mockMvc.perform(patch(BASE + "/7/durum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DurumUpdateRequest(IhaDurum.PASIF))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durum").value("PASIF"));
    }

    @Test
    void updateDurum_bosDurumda400() throws Exception {
        mockMvc.perform(patch(BASE + "/7/durum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.durum").value("Durum zorunludur"));
    }

    @Test
    void updateDurum_hurdaIse400() throws Exception {
        when(ihaService.updateDurum(eq(7L), any()))
                .thenThrow(new BusinessRuleException("HURDA durumundaki İHA'nın durumu değiştirilemez: id=7"));

        mockMvc.perform(patch(BASE + "/7/durum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durum\":\"AKTIF\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("HURDA durumundaki İHA'nın durumu değiştirilemez: id=7"));
    }

    @Test
    void delete_204() throws Exception {
        mockMvc.perform(delete(BASE + "/7"))
                .andExpect(status().isNoContent());

        verify(ihaService).delete(7L);
    }

    @Test
    void delete_yoksa404() throws Exception {
        doThrow(new IhaNotFoundException(9L)).when(ihaService).delete(9L);

        mockMvc.perform(delete(BASE + "/9"))
                .andExpect(status().isNotFound());
    }
}
