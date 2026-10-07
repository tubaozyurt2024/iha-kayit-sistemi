package com.example.iha.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "iha")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Iha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String seriNo;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, length = 100)
    private String uretici;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IhaKategori kategori;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IhaDurum durum;

    @Column(nullable = false)
    private Integer maksIrtifaMetre;

    @Column(nullable = false)
    private Double menzilKm;

    @Column(nullable = false)
    private Double havadaKalmaSuresiSaat;

    @Column(nullable = false)
    private Double azamiKalkisAgirligiKg;

    @Column(nullable = false)
    private LocalDate uretimTarihi;

    private LocalDate sonBakimTarihi;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
