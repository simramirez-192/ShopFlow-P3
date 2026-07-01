package com.shopflow.msorden.repository;
import com.shopflow.msorden.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
    List<Orden> findByEstado(String estado);
}
