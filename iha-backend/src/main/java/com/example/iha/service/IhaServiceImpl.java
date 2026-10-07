package com.example.iha.service;

import com.example.iha.dto.DurumUpdateRequest;
import com.example.iha.dto.IhaCreateRequest;
import com.example.iha.dto.IhaResponse;
import com.example.iha.dto.IhaUpdateRequest;
import com.example.iha.entity.Iha;
import com.example.iha.entity.IhaDurum;
import com.example.iha.entity.IhaKategori;
import com.example.iha.exception.BusinessRuleException;
import com.example.iha.exception.DuplicateSeriNoException;
import com.example.iha.exception.IhaNotFoundException;
import com.example.iha.mapper.IhaMapper;
import com.example.iha.repository.IhaRepository;
import com.example.iha.repository.IhaSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IhaServiceImpl implements IhaService {

    private final IhaRepository ihaRepository;
    private final IhaMapper ihaMapper;

    @Override
    @Transactional
    public IhaResponse create(IhaCreateRequest request) {
        String seriNo = request.getSeriNo().trim();
        if (ihaRepository.existsBySeriNo(seriNo)) {
            throw new DuplicateSeriNoException(seriNo);
        }
        validateBakimTarihi(request.getUretimTarihi(), request.getSonBakimTarihi());

        Iha saved = ihaRepository.save(ihaMapper.toEntity(request));
        return ihaMapper.toResponse(saved);
    }

    @Override
    public IhaResponse getById(Long id) {
        return ihaMapper.toResponse(findOrThrow(id));
    }

    @Override
    public Page<IhaResponse> list(IhaDurum durum, IhaKategori kategori, String model, Pageable pageable) {
        return ihaRepository.findAll(IhaSpecifications.filtrele(durum, kategori, model), pageable)
                .map(ihaMapper::toResponse);
    }

    @Override
    @Transactional
    public IhaResponse update(Long id, IhaUpdateRequest request) {
        Iha iha = findOrThrow(id);

        String seriNo = request.getSeriNo().trim();
        if (ihaRepository.existsBySeriNoAndIdNot(seriNo, id)) {
            throw new DuplicateSeriNoException(seriNo);
        }
        validateDurumDegisimi(iha, request.getDurum());
        validateBakimTarihi(request.getUretimTarihi(), request.getSonBakimTarihi());

        ihaMapper.updateEntity(iha, request);
        return ihaMapper.toResponse(ihaRepository.save(iha));
    }

    @Override
    @Transactional
    public IhaResponse updateDurum(Long id, DurumUpdateRequest request) {
        Iha iha = findOrThrow(id);
        validateDurumDegisimi(iha, request.getDurum());

        iha.setDurum(request.getDurum());
        return ihaMapper.toResponse(ihaRepository.save(iha));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Iha iha = findOrThrow(id);
        ihaRepository.delete(iha);
    }

    private Iha findOrThrow(Long id) {
        return ihaRepository.findById(id).orElseThrow(() -> new IhaNotFoundException(id));
    }

    /** İş kuralı 2: HURDA durumundaki bir İHA'nın durumu değiştirilemez. */
    private void validateDurumDegisimi(Iha iha, IhaDurum yeniDurum) {
        if (iha.getDurum() == IhaDurum.HURDA && yeniDurum != IhaDurum.HURDA) {
            throw new BusinessRuleException(
                    "HURDA durumundaki İHA'nın durumu değiştirilemez: id=" + iha.getId());
        }
    }

    /** İş kuralı 3: Son bakım tarihi üretim tarihinden önce olamaz. */
    private void validateBakimTarihi(LocalDate uretimTarihi, LocalDate sonBakimTarihi) {
        if (sonBakimTarihi != null && uretimTarihi != null && sonBakimTarihi.isBefore(uretimTarihi)) {
            throw new BusinessRuleException("Son bakım tarihi, üretim tarihinden önce olamaz");
        }
    }
}
