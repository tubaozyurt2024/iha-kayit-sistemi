package com.example.iha.controller;

import com.example.iha.dto.DurumUpdateRequest;
import com.example.iha.dto.IhaCreateRequest;
import com.example.iha.dto.IhaResponse;
import com.example.iha.dto.IhaUpdateRequest;
import com.example.iha.dto.PageResponse;
import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import com.example.iha.service.IhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/ihalar")
@RequiredArgsConstructor
@Tag(name = "İHA", description = "İHA kayıt işlemleri")
public class IhaController {

    private final IhaService ihaService;

    @PostMapping
    @Operation(summary = "Yeni İHA kaydı oluşturur")
    public ResponseEntity<IhaResponse> create(@Valid @RequestBody IhaCreateRequest request) {
        IhaResponse created = ihaService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile İHA getirir")
    public IhaResponse getById(@PathVariable Long id) {
        return ihaService.getById(id);
    }

    @GetMapping
    @Operation(summary = "İHA'ları sayfalı ve filtreli listeler")
    public PageResponse<IhaResponse> list(
            @Parameter(description = "Durum filtresi") @RequestParam(required = false) IhaDurum durum,
            @Parameter(description = "Kategori filtresi") @RequestParam(required = false) IhaKategori kategori,
            @Parameter(description = "Model adında arama (büyük/küçük harf duyarsız)") @RequestParam(required = false) String model,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return PageResponse.from(ihaService.list(durum, kategori, model, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "İHA kaydını tamamen günceller")
    public IhaResponse update(@PathVariable Long id, @Valid @RequestBody IhaUpdateRequest request) {
        return ihaService.update(id, request);
    }

    @PatchMapping("/{id}/durum")
    @Operation(summary = "Sadece İHA durumunu günceller")
    public IhaResponse updateDurum(@PathVariable Long id, @Valid @RequestBody DurumUpdateRequest request) {
        return ihaService.updateDurum(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "İHA kaydını siler")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ihaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
