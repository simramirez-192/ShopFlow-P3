package com.shopflow.mspago.service;

import com.shopflow.mspago.client.NotificacionClient;
import com.shopflow.mspago.client.OrdenClient;
import com.shopflow.mspago.dto.PagoRequestDTO;
import com.shopflow.mspago.dto.PagoResponseDTO;
import com.shopflow.mspago.model.Pago;
import com.shopflow.mspago.repository.PagoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private OrdenClient ordenClient;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private PagoService pagoService;

    private Pago pago;
    private PagoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        pago = new Pago();
        pago.setId(1L);
        pago.setOrdenId(1L);
        pago.setMonto(new BigDecimal("599990"));
        pago.setMetodoPago("TARJETA_CREDITO");
        pago.setEstado("APROBADO");
        pago.setReferencia("TXN-ABCD1234");

        requestDTO = new PagoRequestDTO();
        requestDTO.setOrdenId(1L);
        requestDTO.setMonto(new BigDecimal("599990"));
        requestDTO.setMetodoPago("TARJETA_CREDITO");
    }

    @Test
    void obtenerTodos_retornaListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(List.of(pago));

        List<PagoResponseDTO> resultado = pagoService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("APROBADO", resultado.get(0).getEstado());
    }

    @Test
    void obtenerPorOrden_pagoExistente_retornaDTO() {
        when(pagoRepository.findByOrdenId(1L)).thenReturn(Optional.of(pago));

        Optional<PagoResponseDTO> resultado = pagoService.obtenerPorOrden(1L);

        assertTrue(resultado.isPresent());
        assertEquals(new BigDecimal("599990"), resultado.get().getMonto());
    }

    @Test
    void obtenerPorOrden_noExiste_retornaVacio() {
        when(pagoRepository.findByOrdenId(99L)).thenReturn(Optional.empty());

        Optional<PagoResponseDTO> resultado = pagoService.obtenerPorOrden(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void procesar_ordenValidaSinPagoExistente_procesaPago() {
        when(ordenClient.obtenerPorId(1L)).thenReturn(
                Map.of("id", 1L, "usuarioId", 1L, "total", "599990")
        );
        when(pagoRepository.findByOrdenId(1L)).thenReturn(Optional.empty());
        when(notificacionClient.crear(any())).thenReturn(Map.of());
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponseDTO resultado = pagoService.procesar(requestDTO);

        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void procesar_ordenNoExiste_lanzaExcepcion() {
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(ordenClient.obtenerPorId(1L)).thenThrow(notFound);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.procesar(requestDTO));

        assertTrue(ex.getMessage().contains("no existe"));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void procesar_pagoYaExiste_lanzaExcepcion() {
        when(ordenClient.obtenerPorId(1L)).thenReturn(
                Map.of("id", 1L, "usuarioId", 1L, "total", "599990")
        );
        when(pagoRepository.findByOrdenId(1L)).thenReturn(Optional.of(pago));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pagoService.procesar(requestDTO));

        assertTrue(ex.getMessage().contains("Ya existe un pago"));
        verify(pagoRepository, never()).save(any());
    }
}
