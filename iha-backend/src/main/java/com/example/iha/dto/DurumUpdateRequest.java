package com.example.iha.dto;

import com.example.iha.entity.IhaDurum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DurumUpdateRequest {

    @NotNull(message = "Durum zorunludur")
    private IhaDurum durum;
}
