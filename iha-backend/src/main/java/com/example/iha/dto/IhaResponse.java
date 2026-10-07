package com.example.iha.dto;

import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IhaResponse {

    private Long id;
    private String seriNo;
    private String model;
    private String uretici;
    private IhaKategori kategori;
    private IhaDurum durum;
    private Integer maksIrtifaMetre;
    private Double menzilKm;
    private Double havadaKalmaSuresiSaat;
    private Double azamiKalkisAgirligiKg;
    private LocalDate uretimTarihi;
    private LocalDate sonBakimTarihi;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
