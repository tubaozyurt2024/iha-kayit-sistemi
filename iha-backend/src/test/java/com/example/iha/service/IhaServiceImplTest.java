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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IhaServiceImplTest {

    @Mock
    private IhaRepository ihaRepository;

    @Spy
    private IhaMapper ihaMapper = new IhaMapper();

    @InjectMocks
    private IhaServiceImpl ihaService;

    private Iha mevcutIha;

    @BeforeEach
    void setUp() {
        mevcutIha = Iha.builder()
                .id(1L)
                .seriNo("TB2-0001")
                .model("Bayraktar TB2")
                .uretici("Baykar")
                .kategori(IhaKategori.MALE)
                .durum(IhaDurum.AKTIF)
                .maksIrtifaMetre(7620)
                .menzilKm(150.0)
                .havadaKalmaSuresiSaat(27.0)
                .azamiKalkisAgirligiKg(700.0)
                .uretimTarihi(LocalDate.of(2019, 5, 12))
                .build();
    }

    private IhaCreateRequest createRequest() {
        return IhaCreateRequest.builder()
                .seriNo("AKS-0099")
                .model("Bayraktar Akıncı")
                .uretici("Baykar")
                .kategori(IhaKategori.HALE)
                .durum(IhaDurum.AKTIF)
                .maksIrtifaMetre(12000)
                .menzilKm(6000.0)
                .havadaKalmaSuresiSaat(24.0)
                .azamiKalkisAgirligiKg(6000.0)
                .uretimTarihi(LocalDate.of(2022, 1, 1))
                .build();
    }

    private IhaUpdateRequest updateRequest(IhaDurum durum) {
        return IhaUpdateRequest.builder()
                .seriNo("TB2-0001")
                .model("Bayraktar TB2S")
                .uretici("Baykar")
                .kategori(IhaKategori.MALE)
                .durum(durum)
                .maksIrtifaMetre(8000)
                .menzilKm(300.0)
                .havadaKalmaSuresiSaat(27.0)
                .azamiKalkisAgirligiKg(750.0)
                .uretimTarihi(LocalDate.of(2019, 5, 12))
                .sonBakimTarihi(LocalDate.of(2024, 1, 1))
                .build();
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        void gecerliIstekteKaydeder() {
            when(ihaRepository.existsBySeriNo("AKS-0099")).thenReturn(false);
            when(ihaRepository.save(any(Iha.class))).thenAnswer(inv -> {
                Iha iha = inv.getArgument(0);
                iha.setId(10L);
                return iha;
            });

            IhaResponse response = ihaService.create(createRequest());

            assertThat(response.getId()).isEqualTo(10L);
            assertThat(response.getSeriNo()).isEqualTo("AKS-0099");
            assertThat(response.getKategori()).isEqualTo(IhaKategori.HALE);
        }

        @Test
        void seriNoTrimlenerekKontrolEdilir() {
            IhaCreateRequest request = createRequest();
            request.setSeriNo("  AKS-0099  ");
            when(ihaRepository.existsBySeriNo("AKS-0099")).thenReturn(false);
            when(ihaRepository.save(any(Iha.class))).thenAnswer(inv -> inv.getArgument(0));

            ihaService.create(request);

            ArgumentCaptor<Iha> captor = ArgumentCaptor.forClass(Iha.class);
            verify(ihaRepository).save(captor.capture());
            assertThat(captor.getValue().getSeriNo()).isEqualTo("AKS-0099");
        }

        @Test
        void seriNoCakisirsaDuplicateFirlatir() {
            when(ihaRepository.existsBySeriNo("AKS-0099")).thenReturn(true);

            assertThatThrownBy(() -> ihaService.create(createRequest()))
                    .isInstanceOf(DuplicateSeriNoException.class)
                    .hasMessageContaining("AKS-0099");
            verify(ihaRepository, never()).save(any());
        }

        @Test
        void sonBakimUretimdenOnceyseHataFirlatir() {
            IhaCreateRequest request = createRequest();
            request.setSonBakimTarihi(LocalDate.of(2021, 12, 31));
            when(ihaRepository.existsBySeriNo(any())).thenReturn(false);

            assertThatThrownBy(() -> ihaService.create(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Son bakım tarihi");
            verify(ihaRepository, never()).save(any());
        }

        @Test
        void sonBakimUretimleAyniGunOlabilir() {
            IhaCreateRequest request = createRequest();
            request.setSonBakimTarihi(request.getUretimTarihi());
            when(ihaRepository.existsBySeriNo(any())).thenReturn(false);
            when(ihaRepository.save(any(Iha.class))).thenAnswer(inv -> inv.getArgument(0));

            IhaResponse response = ihaService.create(request);

            assertThat(response.getSonBakimTarihi()).isEqualTo(request.getUretimTarihi());
        }
    }

    @Nested
    @DisplayName("getById / delete")
    class GetAndDelete {

        @Test
        void mevcutIhaDoner() {
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));

            assertThat(ihaService.getById(1L).getSeriNo()).isEqualTo("TB2-0001");
        }

        @Test
        void yoksaNotFoundFirlatir() {
            when(ihaRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ihaService.getById(5L))
                    .isInstanceOf(IhaNotFoundException.class)
                    .hasMessage("İHA bulunamadı: id=5");
        }

        @Test
        void silerken() {
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));

            ihaService.delete(1L);

            verify(ihaRepository).delete(mevcutIha);
        }

        @Test
        void olmayaniSilmekNotFoundFirlatir() {
            when(ihaRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ihaService.delete(5L)).isInstanceOf(IhaNotFoundException.class);
            verify(ihaRepository, never()).delete(any(Iha.class));
        }
    }

    @Nested
    @DisplayName("list")
    class ListIhalar {

        @Test
        @SuppressWarnings("unchecked")
        void sayfaliSonucuResponseaDonusturur() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Iha> page = new PageImpl<>(List.of(mevcutIha), pageable, 1);
            when(ihaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<IhaResponse> result = ihaService.list(IhaDurum.AKTIF, null, "tb2", pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getModel()).isEqualTo("Bayraktar TB2");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        void tumAlanlariGunceller() {
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));
            when(ihaRepository.existsBySeriNoAndIdNot("TB2-0001", 1L)).thenReturn(false);
            when(ihaRepository.save(any(Iha.class))).thenAnswer(inv -> inv.getArgument(0));

            IhaResponse response = ihaService.update(1L, updateRequest(IhaDurum.BAKIMDA));

            assertThat(response.getModel()).isEqualTo("Bayraktar TB2S");
            assertThat(response.getDurum()).isEqualTo(IhaDurum.BAKIMDA);
            assertThat(response.getMenzilKm()).isEqualTo(300.0);
        }

        @Test
        void baskaKayittaAyniSeriNoVarsaDuplicateFirlatir() {
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));
            when(ihaRepository.existsBySeriNoAndIdNot("TB2-0001", 1L)).thenReturn(true);

            assertThatThrownBy(() -> ihaService.update(1L, updateRequest(IhaDurum.AKTIF)))
                    .isInstanceOf(DuplicateSeriNoException.class);
            verify(ihaRepository, never()).save(any());
        }

        @Test
        void hurdaIhaninDurumuPutIleDegistirilemez() {
            mevcutIha.setDurum(IhaDurum.HURDA);
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));
            when(ihaRepository.existsBySeriNoAndIdNot(any(), any())).thenReturn(false);

            assertThatThrownBy(() -> ihaService.update(1L, updateRequest(IhaDurum.AKTIF)))
                    .isInstanceOf(BusinessRuleException.class);
        }

        @Test
        void olmayanIhaGuncellenemez() {
            when(ihaRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ihaService.update(5L, updateRequest(IhaDurum.AKTIF)))
                    .isInstanceOf(IhaNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateDurum")
    class UpdateDurum {

        @Test
        void durumuGunceller() {
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));
            when(ihaRepository.save(any(Iha.class))).thenAnswer(inv -> inv.getArgument(0));

            IhaResponse response = ihaService.updateDurum(1L, new DurumUpdateRequest(IhaDurum.BAKIMDA));

            assertThat(response.getDurum()).isEqualTo(IhaDurum.BAKIMDA);
        }

        @Test
        void aktifIhaHurdayaAyrilabilir() {
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));
            when(ihaRepository.save(any(Iha.class))).thenAnswer(inv -> inv.getArgument(0));

            assertThat(ihaService.updateDurum(1L, new DurumUpdateRequest(IhaDurum.HURDA)).getDurum())
                    .isEqualTo(IhaDurum.HURDA);
        }

        @Test
        void hurdaIhaninDurumuDegistirilemez() {
            mevcutIha.setDurum(IhaDurum.HURDA);
            when(ihaRepository.findById(1L)).thenReturn(Optional.of(mevcutIha));

            assertThatThrownBy(() -> ihaService.updateDurum(1L, new DurumUpdateRequest(IhaDurum.AKTIF)))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("HURDA");
            verify(ihaRepository, never()).save(any());
        }
    }
}
