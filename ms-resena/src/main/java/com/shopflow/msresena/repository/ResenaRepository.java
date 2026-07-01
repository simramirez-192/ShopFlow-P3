package com.shopflow.msresena.repository;
import com.shopflow.msresena.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoIdAndActivaTrue(Long productoId);
    List<Resena> findByUsuarioId(Long usuarioId);
    boolean existsByProductoIdAndUsuarioIdAndOrdenId(Long productoId, Long usuarioId, Long ordenId);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.productoId = :productoId AND r.activa = true")
    Double promedioCalificacion(@Param("productoId") Long productoId);

    @Query("SELECT COUNT(r) FROM Resena r WHERE r.productoId = :productoId AND r.activa = true")
    Long totalResenas(@Param("productoId") Long productoId);
}
