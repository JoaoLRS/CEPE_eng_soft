package cepe.repository;

import cepe.domain.entity.Municipio;
import cepe.domain.entity.Secao;
import cepe.domain.entity.Zona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SecaoRepository extends JpaRepository<Secao, Long> {
    Optional<Secao> findByNumeroAndMunicipio(Integer numero, Municipio municipio);

    List<Secao> findByMunicipio(Municipio municipio);

    Page<Secao> findByMunicipio(Municipio municipio, Pageable pageable);

    Page<Secao> findAllByZona(Zona zona, Pageable pageable);

    List<Secao> findAllByMunicipioIn(List<Municipio> municipios);

    List<Secao> findByPolo_Numero(Integer poloNumero);

    List<Secao> findByZona_Numero(Integer numeroZona);
}
