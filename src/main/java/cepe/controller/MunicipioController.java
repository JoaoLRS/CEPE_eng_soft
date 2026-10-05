package cepe.controller;

import cepe.dto.municipio.MunicipioDto;
import cepe.dto.secao.SecaoDto;
import cepe.service.MunicipioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MunicipioController {
    private final MunicipioService service;

    public MunicipioController(MunicipioService service) {
        this.service = service;
    }

    @GetMapping("/listar-municipios")
    public String listarMunicipios(@RequestParam(required = false) String search, Model model) {
        var municipios = service.listar(search);
        model.addAttribute("municipios", municipios);
        model.addAttribute("zonas", service.listarZonas(search));
        model.addAttribute("secoes", service.listarSecoes(search));
        return "municipio/listarMunicipio";
    }

    @GetMapping("/consultar-municipios-cod")
    public String consultarPorCodigo(@RequestParam(required = false) Integer codTse, Model model) {
        if (codTse == null) {
            return "municipio/codMunicipio";
        }

        var municipio = service.buscar(codTse);
        if (municipio.isPresent()) {
            model.addAttribute("municipio", municipio.get());
        } else {
            model.addAttribute("error", "Município não encontrado com o código TSE: " + codTse);
        }
        return "municipio/codMunicipio";
    }

    @GetMapping("/consultar-secoes-zonas")
    public String buscarPorCodTse(
            @RequestParam(name = "codTse", required = false) Integer codTse,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        if (codTse != null) {
            Optional<MunicipioDto> municipio = service.buscar(codTse);
            if (municipio.isPresent()) {
                Pageable pageable = PageRequest.of(page, size);
                Page<SecaoDto> secoes = service.listarSecoes(codTse, pageable);
                model.addAttribute("municipio", municipio.get().nome());
                model.addAttribute("dados", secoes.getContent());
                model.addAttribute("paginaAtual", page);
                model.addAttribute("totalPaginas", secoes.getTotalPages());
                model.addAttribute("size", size);
                model.addAttribute("codTse", codTse);
            } else {
                model.addAttribute("error", "Município não encontrado para o código informado.");
            }
        }
        return "municipio/consultarZonaSecao";
    }
}
