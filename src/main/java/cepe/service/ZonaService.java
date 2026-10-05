package cepe.service;

import cepe.domain.entity.Secao;
import cepe.domain.entity.Zona;
import cepe.dto.municipio.MunicipioDto;
import cepe.dto.secao.SecaoDto;
import cepe.dto.zona.ZonaDto;
import cepe.repository.SecaoRepository;
import cepe.repository.ZonaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ZonaService {
    private final ZonaRepository zonas;

    private final SecaoRepository secoes;

    public ZonaService(ZonaRepository zonas, SecaoRepository secoes) {
        this.zonas = zonas;
        this.secoes = secoes;
    }

    public Optional<ZonaDto> buscar(Integer numero) {
        return zonas.findByNumero(numero).map(ZonaService::paraDto);
    }

    public Page<ZonaDto> buscarPaginado(Integer numero, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").ascending());
        var zona = zonas.findByNumero(numero);

        if (zona.isEmpty()) {
            return Page.empty(pageable);
        }

        return new PageImpl<>(List.of(paraDto(zona.get())), pageable, 1);
    }

    public Page<SecaoDto> listarSecoes(Integer numeroZona, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").ascending());
        return zonas.findByNumero(numeroZona)
                .map(zona -> secoes.findAllByZona(zona, pageable).map(ZonaService::paraDto))
                .orElse(Page.empty(pageable));
    }

    private static ZonaDto paraDto(Zona zona) {
        var sede = zona.getMunicipioSede() == null
                ? null
                : new MunicipioDto(zona.getMunicipioSede().getCodTse(), zona.getMunicipioSede().getNome());
        var municipios = zona.getMunicipios().stream()
                .map(municipio -> new MunicipioDto(municipio.getCodTse(), municipio.getNome()))
                .toList();
        return new ZonaDto(
                zona.getNumero(),
                sede,
                municipios
        );
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
