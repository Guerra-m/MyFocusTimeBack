package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.repository.TiempoEstudioDiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TiempoEstudioDiaServiceTest {

    private TiempoEstudioDiaRepository repo;
    private TiempoEstudioDiaService service;

    @BeforeEach
    void setUp() {
        repo = mock(TiempoEstudioDiaRepository.class);
        service = new TiempoEstudioDiaService();
        ReflectionTestUtils.setField(service, "repo", repo);
    }

    @Test
    void traerHorasTotales_conMinutosRegistrados_devuelveHoras() {
        when(repo.sumMinutosByUsuarioId(1L)).thenReturn(120.0);

        double horas = service.traerHorasTotales(1L);

        assertEquals(2.0, horas);
    }

    @Test
    void traerHorasTotales_sinRegistros_devuelveCero() {
        when(repo.sumMinutosByUsuarioId(2L)).thenReturn(null);

        double horas = service.traerHorasTotales(2L);

        assertEquals(0.0, horas);
    }
}
