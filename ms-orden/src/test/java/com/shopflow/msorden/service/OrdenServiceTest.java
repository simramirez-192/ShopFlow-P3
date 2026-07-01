package com.shopflow.msorden.service;

import com.shopflow.msorden.client.InventarioClient;
import com.shopflow.msorden.client.NotificacionClient;
import com.shopflow.msorden.client.ProductoClient;
import com.shopflow.msorden.client.UsuarioClient;
import com.shopflow.msorden.dto.OrdenRequestDTO;
import com.shopflow.msorden.dto.OrdenResponseDTO;
import com.shopflow.msorden.model.Orden;
import com.shopflow.msorden.repository.OrdenRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private OrdenService ordenService;

    private Orden orden;
    private OrdenRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        orden = new Orden();
        orden.setId(1L);
        orden.setUsuarioId(1L);
        orden.setEstado("PENDIENTE");
        orden.setTotal(new BigDecimal("599990"));
        orden.setDireccionEnvio("Av. Principal 123");

        OrdenRequestDTO.Item item = new OrdenRequestDTO.Item();
        item.setProductoId(1L);
        item.setCantidad(1);

        requestDTO = new OrdenRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setDireccionEnvio("Av. Principal 123");
        requestDTO.setItems(List.of(item));
    }

    @Test
    void obtenerTodas_retornaListaDeOrdenes() {
        when(ordenRepository.findAll()).thenReturn(List.of(orden));

        List<OrdenResponseDTO> resultado = ordenService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
    }

    @Test
    void obtenerPorId_ordenExistente_retornaDTO() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        Optional<OrdenResponseDTO> resultado = ordenService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getUsuarioId());
    }

    @Test
    void obtenerPorId_ordenNoExistente_retornaVacio() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<OrdenResponseDTO> resultado = ordenService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_usuarioYProductoValidos_creaOrden() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(Map.of("id", 1L));
        when(productoClient.obtenerPorId(1L)).thenReturn(
                Map.of("id", 1L, "nombre", "Laptop Dell", "precio", "599990")
        );
        doNothing().when(inventarioClient).ajustarStock(anyLong(), any());
        when(notificacionClient.crear(any())).thenReturn(Map.of());
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        OrdenResponseDTO resultado = ordenService.crear(requestDTO);

        assertNotNull(resultado);
        verify(ordenRepository).save(any(Orden.class));
    }

    @Test
    void crear_usuarioNoExistente_lanzaExcepcion() {
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(usuarioClient.obtenerPorId(1L)).thenThrow(notFound);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ordenService.crear(requestDTO));

        assertTrue(ex.getMessage().contains("no existe"));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_ordenExistente_cambiaEstado() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        OrdenRequestDTO dto = new OrdenRequestDTO();
        dto.setEstado("CONFIRMADA");

        Optional<OrdenResponseDTO> resultado = ordenService.actualizarEstado(1L, dto);

        assertTrue(resultado.isPresent());
        verify(ordenRepository).save(any(Orden.class));
    }

    @Test
    void actualizarEstado_ordenNoExistente_retornaVacio() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        OrdenRequestDTO dto = new OrdenRequestDTO();
        dto.setEstado("CONFIRMADA");

        Optional<OrdenResponseDTO> resultado = ordenService.actualizarEstado(99L, dto);

        assertFalse(resultado.isPresent());
    }

    @Test
    void obtenerPorUsuario_retornaOrdenesDelUsuario() {
        when(ordenRepository.findByUsuarioIdOrderByCreadoEnDesc(1L)).thenReturn(List.of(orden));

        List<OrdenResponseDTO> resultado = ordenService.obtenerPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuarioId());
    }
}
