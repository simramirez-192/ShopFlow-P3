package com.shopflow.msproducto.service;

import com.shopflow.msproducto.client.InventarioClient;
import com.shopflow.msproducto.dto.ProductoRequestDTO;
import com.shopflow.msproducto.dto.ProductoResponseDTO;
import com.shopflow.msproducto.model.Categoria;
import com.shopflow.msproducto.model.Producto;
import com.shopflow.msproducto.repository.CategoriaRepository;
import com.shopflow.msproducto.repository.ProductoRepository;
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
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Tecnología");
        categoria.setActivo(true);

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse Logitech");
        producto.setDescripcion("Mouse inalámbrico");
        producto.setPrecio(new BigDecimal("15990"));
        producto.setImagenUrl("http://img/mouse.png");
        producto.setActivo(true);
        producto.setCategoria(categoria);

        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Mouse Logitech");
        requestDTO.setDescripcion("Mouse inalámbrico");
        requestDTO.setPrecio(new BigDecimal("15990"));
        requestDTO.setImagenUrl("http://img/mouse.png");
        requestDTO.setCategoriaId(1L);
    }

    // ---------- obtenerTodos ----------

    @Test
    void obtenerTodos_retornaListaDeProductosActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(producto));

        List<ProductoResponseDTO> resultado = productoService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Mouse Logitech", resultado.get(0).getNombre());
    }

    // ---------- obtenerPorId ----------

    @Test
    void obtenerPorId_productoExistente_retornaDTO() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<ProductoResponseDTO> resultado = productoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Mouse Logitech", resultado.get().getNombre());
    }

    @Test
    void obtenerPorId_noExiste_retornaVacio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProductoResponseDTO> resultado = productoService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    // ---------- buscarPorNombre ----------

    @Test
    void buscarPorNombre_encuentraCoincidencias() {
        when(productoRepository.buscarPorNombre("mouse")).thenReturn(List.of(producto));

        List<ProductoResponseDTO> resultado = productoService.buscarPorNombre("mouse");

        assertEquals(1, resultado.size());
        assertEquals("Mouse Logitech", resultado.get(0).getNombre());
    }

    // ---------- obtenerPorCategoria ----------

    @Test
    void obtenerPorCategoria_retornaProductosDeEsaCategoria() {
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(producto));

        List<ProductoResponseDTO> resultado = productoService.obtenerPorCategoria(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getCategoriaId());
    }

    // ---------- crear ----------

    @Test
    void crear_categoriaValida_creaProductoYRegistraInventario() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(inventarioClient.crear(any())).thenReturn(Map.of());

        ProductoResponseDTO resultado = productoService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals("Mouse Logitech", resultado.getNombre());
        verify(productoRepository).save(any(Producto.class));
        verify(inventarioClient).crear(any());
    }

    @Test
    void crear_categoriaNoExiste_lanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setCategoriaId(99L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.crear(requestDTO));

        assertTrue(ex.getMessage().contains("no existe"));
        verify(productoRepository, never()).save(any());
    }

    // ---------- actualizar ----------

    @Test
    void actualizar_productoExistente_actualizaDatos() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        requestDTO.setNombre("Mouse Logitech G203");
        Optional<ProductoResponseDTO> resultado = productoService.actualizar(1L, requestDTO);

        assertTrue(resultado.isPresent());
        verify(productoRepository).save(any(Producto.class));
    }

    // ---------- eliminar ----------

    @Test
    void eliminar_productoExistente_marcaComoInactivo() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.eliminar(1L);

        assertFalse(producto.getActivo());
        verify(productoRepository).save(producto);
    }
}
