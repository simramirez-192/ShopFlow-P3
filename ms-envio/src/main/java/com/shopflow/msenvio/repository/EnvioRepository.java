package com.shopflow.msenvio.repository;
import com.shopflow.msenvio.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByOrdenId(Long ordenId);
    List<Envio> findByEstado(String estado);
}
