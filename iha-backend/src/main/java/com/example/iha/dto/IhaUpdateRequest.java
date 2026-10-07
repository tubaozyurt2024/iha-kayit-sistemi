package com.example.iha.dto;

import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IhaUpdateRequest {

    @NotBlank(message = "Seri numarası zorunludur")
    @Size(max = 50, message = "Seri numarası en fazla 50 karakter olabilir")
    private String seriNo;

    @NotBlank(message = "Model zorunludur")
    @Size(max = 100, message = "Model en fazla 100 karakter olabilir")
    private String model;

    @NotBlank(message = "Üretici zorunludur")
    @Size(max = 100, message = "Üretici en fazla 100 karakter olabilir")
    private String uretici;

    @NotNull(message = "Kategori zorunludur")
    private IhaKategori kategori;

    @NotNull(message = "Durum zorunludur")
    private IhaDurum durum;

    @NotNull(message = "Maksimum irtifa zorunludur")
    @Positive(message = "Maksimum irtifa pozitif olmalıdır")
    private Integer maksIrtifaMetre;

    @NotNull(message = "Menzil zorunludur")
    @Positive(message = "Menzil pozitif olmalıdır")
    private Double menzilKm;

    @NotNull(message = "Havada kalma süresi zorunludur")
    @Positive(message = "Havada kalma süresi pozitif olmalıdır")
    private Double havadaKalmaSuresiSaat;

    @NotNull(message = "Azami kalkış ağırlığı zorunludur")
    @Positive(message = "Azami kalkış ağırlığı pozitif olmalıdır")
    private Double azamiKalkisAgirligiKg;

    @NotNull(message = "Üretim tarihi zorunludur")
    @PastOrPresent(message = "Üretim tarihi gelecekte olamaz")
    private LocalDate uretimTarihi;

    @PastOrPresent(message = "Son bakım tarihi gelecekte olamaz")
    private LocalDate sonBakimTarihi;
}
