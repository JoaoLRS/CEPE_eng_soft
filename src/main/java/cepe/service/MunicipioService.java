package cepe.service;

import cepe.domain.entity.Municipio;
import cepe.domain.entity.Secao;
import cepe.domain.entity.Zona;
import cepe.dto.municipio.MunicipioDto;
import cepe.dto.secao.SecaoDto;
import cepe.dto.zona.ZonaDto;
import cepe.repository.MunicipioRepository;
import cepe.repository.SecaoRepository;
import cepe.repository.ZonaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MunicipioService {
    private final MunicipioRepository municipios;

    private final ZonaRepository zonas;

    private final SecaoRepository secoes;

    public MunicipioService(MunicipioRepository municipios, ZonaRepository zonas, SecaoRepository secoes) {
        this.municipios = municipios;
        this.zonas = zonas;
        this.secoes = secoes;
    }

    public List<MunicipioDto> listar(String busca) {
        var resultados = busca != null && !busca.isEmpty()
                ? municipios.findByNomeContainingIgnoreCase(busca)
                : municipios.findAll();
        return resultados
                .stream().map(MunicipioService::paraDto).toList();
    }

    public List<ZonaDto> listarZonas(String busca) {
        var lista = buscarEntidades(busca);
        return zonas.findAllByMunicipiosIn(lista).stream().map(MunicipioService::paraDto).toList();
    }

    public List<SecaoDto> listarSecoes(String busca) {
        var lista = buscarEntidades(busca);
        return secoes.findAllByMunicipioIn(lista).stream().map(MunicipioService::paraDto).toList();
    }

    public Optional<MunicipioDto> buscar(Integer codigo) {
        return municipios.findById(codigo).map(MunicipioService::paraDto);
    }

    public Page<SecaoDto> listarSecoes(Integer codigo, Pageable pageable) {
        return municipios.findById(codigo)
                .map(municipio -> secoes.findByMunicipio(municipio, pageable).map(MunicipioService::paraDto))
                .orElse(Page.empty(pageable));
    }

    private List<Municipio> buscarEntidades(String busca) {
        if (busca != null && !busca.isEmpty()) {
            return municipios.findByNomeContainingIgnoreCase(busca);
        }
        return municipios.findAll();
    }

    private static MunicipioDto paraDto(Municipio municipio) {
        return new MunicipioDto(municipio.getCodTse(), municipio.getNome());
    }

    private static ZonaDto paraDto(Zona zona) {
        var sede = zona.getMunicipioSede() == null ? null : paraDto(zona.getMunicipioSede());
        var municipios = zona.getMunicipios().stream()
                .map(MunicipioService::paraDto)
                .toList();
        return new ZonaDto(zona.getNumero(), sede, municipios);
    }

    private static SecaoDto paraDto(Secao secao) {
        return new SecaoDto(
                secao.getId(),
                secao.getNumero(),
                secao.getMunicipio() == null ? null : secao.getMunicipio().getNome(),
                secao.getZona() == null ? null : secao.getZona().getNumero()
        );
    }
}
