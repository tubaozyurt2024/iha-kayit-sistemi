package com.example.iha.mapper;

import com.example.iha.dto.IhaCreateRequest;
import com.example.iha.dto.IhaResponse;
import com.example.iha.dto.IhaUpdateRequest;
import com.example.iha.entity.Iha;
import org.springframework.stereotype.Component;

@Component
public class IhaMapper {

    public Iha toEntity(IhaCreateRequest request) {
        return Iha.builder()
                .seriNo(request.getSeriNo().trim())
                .model(request.getModel().trim())
                .uretici(request.getUretici().trim())
                .kategori(request.getKategori())
                .durum(request.getDurum())
                .maksIrtifaMetre(request.getMaksIrtifaMetre())
                .menzilKm(request.getMenzilKm())
                .havadaKalmaSuresiSaat(request.getHavadaKalmaSuresiSaat())
                .azamiKalkisAgirligiKg(request.getAzamiKalkisAgirligiKg())
                .uretimTarihi(request.getUretimTarihi())
                .sonBakimTarihi(request.getSonBakimTarihi())
                .build();
    }

    public void updateEntity(Iha iha, IhaUpdateRequest request) {
        iha.setSeriNo(request.getSeriNo().trim());
        iha.setModel(request.getModel().trim());
        iha.setUretici(request.getUretici().trim());
        iha.setKategori(request.getKategori());
        iha.setDurum(request.getDurum());
        iha.setMaksIrtifaMetre(request.getMaksIrtifaMetre());
        iha.setMenzilKm(request.getMenzilKm());
        iha.setHavadaKalmaSuresiSaat(request.getHavadaKalmaSuresiSaat());
        iha.setAzamiKalkisAgirligiKg(request.getAzamiKalkisAgirligiKg());
        iha.setUretimTarihi(request.getUretimTarihi());
        iha.setSonBakimTarihi(request.getSonBakimTarihi());
    }

    public IhaResponse toResponse(Iha iha) {
        return IhaResponse.builder()
                .id(iha.getId())
                .seriNo(iha.getSeriNo())
                .model(iha.getModel())
                .uretici(iha.getUretici())
                .kategori(iha.getKategori())
                .durum(iha.getDurum())
                .maksIrtifaMetre(iha.getMaksIrtifaMetre())
                .menzilKm(iha.getMenzilKm())
                .havadaKalmaSuresiSaat(iha.getHavadaKalmaSuresiSaat())
                .azamiKalkisAgirligiKg(iha.getAzamiKalkisAgirligiKg())
                .uretimTarihi(iha.getUretimTarihi())
                .sonBakimTarihi(iha.getSonBakimTarihi())
                .createdAt(iha.getCreatedAt())
                .updatedAt(iha.getUpdatedAt())
                .build();
    }
}
