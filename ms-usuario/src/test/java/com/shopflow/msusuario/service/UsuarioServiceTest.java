package com.shopflow.msusuario.service;

import com.shopflow.msusuario.client.AutenticacionClient;
import com.shopflow.msusuario.dto.UsuarioRequestDTO;
import com.shopflow.msusuario.dto.UsuarioResponseDTO;
import com.shopflow.msusuario.model.Usuario;
import com.shopflow.msusuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AutenticacionClient autenticacionClient;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Jorge");
        usuario.setApellido("González");
        usuario.setEmail("jorge@email.com");
        usuario.setTelefono("+56912345678");
        usuario.setActivo(true);

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombre("Jorge");
        requestDTO.setApellido("González");
        requestDTO.setEmail("jorge@email.com");
        requestDTO.setTelefono("+56912345678");
    }

    @Test
    void obtenerTodos_retornaListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Jorge", resultado.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodos_listaVaciaRetornaListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorId_usuarioExistente_retornaDTO() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(autenticacionClient.obtenerPorUsuarioId(1L)).thenReturn(java.util.Map.of("rol", "CLIENTE"));

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("jorge@email.com", resultado.get().getEmail());
    }

    @Test
    void obtenerPorId_usuarioNoExistente_retornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_emailNuevo_creaYRetornaUsuario() {
        when(usuarioRepository.existsByEmail("jorge@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals("Jorge", resultado.getNombre());
        assertEquals("jorge@email.com", resultado.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crear_emailDuplicado_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("jorge@email.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.crear(requestDTO));

        assertTrue(ex.getMessage().contains("jorge@email.com"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizar_usuarioExistente_actualizaYRetorna() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizar(1L, requestDTO);

        assertTrue(resultado.isPresent());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizar_usuarioNoExistente_retornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizar(99L, requestDTO);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminar_llamaAlRepositorio() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
