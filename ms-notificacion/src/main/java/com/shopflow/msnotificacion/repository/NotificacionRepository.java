package com.shopflow.msnotificacion.repository;
import com.shopflow.msnotificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndLeida(Long usuarioId, Boolean leida);
    long countByUsuarioIdAndLeida(Long usuarioId, Boolean leida);
}
