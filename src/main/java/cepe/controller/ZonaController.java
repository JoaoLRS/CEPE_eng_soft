package cepe.controller;

import cepe.dto.zona.ZonaDto;
import cepe.service.ZonaService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ZonaController {
    private final ZonaService service;

    public ZonaController(ZonaService service) {
        this.service = service;
    }

    @GetMapping("/listar-zonas")
    public String consultarZonaPorNumero(
            @RequestParam(name = "numeroZona", required = false) Integer numeroZona,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        if (numeroZona == null) {
            return "zona/consultarZona";
        }

        Page<ZonaDto> resultado = service.buscarPaginado(numeroZona, page, size);
        if (resultado.isEmpty()) {
            model.addAttribute("error", "Zona não encontrada.");
            return "zona/consultarZona";
        }

        model.addAttribute("dados", resultado.getContent());
        model.addAttribute("paginaAtual", resultado.getNumber());
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("numeroZona", numeroZona);
        return "zona/consultarZona";
    }

    @GetMapping("/listar-secoes-por-zona")
    public String listarSecoesPorZona(
            @RequestParam(name = "numeroZona", required = false) Integer numeroZona,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        if (numeroZona == null) {
            return "zona/consultarSecao";
        }

        var zona = service.buscar(numeroZona);
        if (zona.isEmpty()) {
            model.addAttribute("error", "Zona não encontrada.");
            return "zona/consultarSecao";
        }

        var secoes = service.listarSecoes(numeroZona, page, size);
        model.addAttribute("secoesPage", secoes);
        model.addAttribute("paginaAtual", secoes.getNumber());
        model.addAttribute("totalPaginas", secoes.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("numeroZona", numeroZona);
        return "zona/consultarSecao";
    }
}
