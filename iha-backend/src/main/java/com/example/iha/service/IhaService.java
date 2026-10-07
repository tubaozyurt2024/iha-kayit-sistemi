package com.example.iha.service;

import com.example.iha.dto.DurumUpdateRequest;
import com.example.iha.dto.IhaCreateRequest;
import com.example.iha.dto.IhaResponse;
import com.example.iha.dto.IhaUpdateRequest;
import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IhaService {

    IhaResponse create(IhaCreateRequest request);

    IhaResponse getById(Long id);

    Page<IhaResponse> list(IhaDurum durum, IhaKategori kategori, String model, Pageable pageable);

    IhaResponse update(Long id, IhaUpdateRequest request);

    IhaResponse updateDurum(Long id, DurumUpdateRequest request);

    void delete(Long id);
}
