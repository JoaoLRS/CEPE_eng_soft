package cepe.service;

import cepe.domain.entity.Municipio;
import cepe.domain.entity.Polo;
import cepe.domain.entity.Secao;
import cepe.domain.entity.Zona;
import cepe.dto.municipio.MunicipioDto;
import cepe.dto.polo.PoloDto;
import cepe.dto.zona.ZonaDto;
import cepe.repository.MunicipioRepository;
import cepe.repository.PoloRepository;
import cepe.repository.SecaoRepository;
import cepe.repository.ZonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PoloService {
    private final PoloRepository polos;

    private final MunicipioRepository municipios;

    private final SecaoRepository secoes;

    private final ZonaRepository zonas;

    public PoloService(PoloRepository polos, MunicipioRepository municipios, SecaoRepository secoes, ZonaRepository zonas) {
        this.polos = polos;
        this.municipios = municipios;
        this.secoes = secoes;
        this.zonas = zonas;
    }

    public Optional<PoloDto> buscar(Integer numero) {
        return polos.findById(numero).map(PoloService::paraDto);
    }

    public List<MunicipioDto> listarMunicipios(Integer numero) {
        return municipios.findByPolo_Numero(numero).stream()
                .map(PoloService::paraDto)
                .toList();
    }

    public List<ZonaDto> listarZonas(Integer numero) {
        return secoes.findByPolo_Numero(numero).stream()
                .map(Secao::getZona)
                .filter(Objects::nonNull)
                .distinct()
                .map(PoloService::paraDto).toList();
    }

    public Optional<MunicipioDto> buscarMunicipio(Integer codigo) {
        return municipios.findById(codigo).map(PoloService::paraDto);
    }

    public Integer buscarNumeroPolo(Integer codigo) {
        return municipios.findById(codigo)
                .map(municipio -> municipio.getPolo() != null
                        ? municipio.getPolo().getNumero()
                        : municipio.getNumeroPolo())
                .orElse(null);
    }

    public Optional<ZonaDto> buscarZona(Integer numero) {
        return zonas.findByNumero(numero).map(PoloService::paraDto);
    }

    public List<Integer> listarPolosDaZona(Integer numeroZona) {
        return secoes.findByZona_Numero(numeroZona).stream()
                .map(Secao::getPolo)
                .filter(Objects::nonNull)
                .distinct()
                .map(Polo::getNumero).toList();
    }

    private static MunicipioDto paraDto(Municipio municipio) {
        return new MunicipioDto(
                municipio.getCodTse(),
                municipio.getNome()
        );
    }

    private static PoloDto paraDto(Polo polo) {
        return new PoloDto(
                polo.getNumero(),
                polo.getMunicipioSede() == null ? null : paraDto(polo.getMunicipioSede())
        );
    }

    private static ZonaDto paraDto(Zona zona) {
        return new ZonaDto(
                zona.getNumero(),
                zona.getMunicipioSede() == null ? null : paraDto(zona.getMunicipioSede()),
                zona.getMunicipios().stream().map(PoloService::paraDto).toList()
        );
    }
}
