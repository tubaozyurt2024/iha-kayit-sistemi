package com.example.iha.repository;

import com.example.iha.entity.Iha;
import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * Liste filtreleri için dinamik sorgu parçaları. Her metod, parametre boşsa null döner;
 * Spring Data null Specification'ları birleştirirken yok sayar.
 */
public final class IhaSpecifications {

    private IhaSpecifications() {
    }

    public static Specification<Iha> filtrele(IhaDurum durum, IhaKategori kategori, String model) {
        return Specification.where(durumEsit(durum))
                .and(kategoriEsit(kategori))
                .and(modelIcerir(model));
    }

    public static Specification<Iha> durumEsit(IhaDurum durum) {
        return durum == null ? null : (root, query, cb) -> cb.equal(root.get("durum"), durum);
    }

    public static Specification<Iha> kategoriEsit(IhaKategori kategori) {
        return kategori == null ? null : (root, query, cb) -> cb.equal(root.get("kategori"), kategori);
    }

    public static Specification<Iha> modelIcerir(String model) {
        if (!StringUtils.hasText(model)) {
            return null;
        }
        String pattern = "%" + escapeLike(model.trim().toLowerCase(Locale.ROOT)) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("model")), pattern, '\\');
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
