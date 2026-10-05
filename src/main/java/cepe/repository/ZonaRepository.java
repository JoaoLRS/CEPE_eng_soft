package cepe.repository;

import cepe.domain.entity.Municipio;
import cepe.domain.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZonaRepository extends JpaRepository<Zona, Integer> {
    Optional<Zona> findByNumero(Integer numero);

    List<Zona> findAllByMunicipios(List<Municipio> municipios);

    List<Zona> findAllByMunicipiosIn(List<Municipio> municipios);
}
