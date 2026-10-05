package cepe.repository;

import cepe.domain.entity.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {
    Optional<Municipio> findByNome(String nome);

    List<Municipio> findByNomeContainingIgnoreCase(String nome);

    List<Municipio> findByPolo_Numero(Integer poloNumero);
}
