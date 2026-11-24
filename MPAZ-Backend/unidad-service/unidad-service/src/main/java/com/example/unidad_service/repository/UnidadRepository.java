package com.example.unidad_service.repository;

import com.example.unidad_service.model.EstadoUnidad;
import com.example.unidad_service.model.Unidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    List<Unidad> findAllByOrderByCreadaEnDesc();

    List<Unidad> findByEstadoOrderByAsignaturaAscCodigoOAAsc(EstadoUnidad estado);

    List<Unidad> findByAsignaturaIgnoreCaseOrderByCodigoOAAsc(String asignatura);

    List<Unidad> findByEstadoAndAsignaturaIgnoreCaseOrderByCodigoOAAsc(
            EstadoUnidad estado, String asignatura);
}
