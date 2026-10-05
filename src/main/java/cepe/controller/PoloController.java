package cepe.controller;

import cepe.service.PoloService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/polos")
public class PoloController {
    private final PoloService service;

    public PoloController(PoloService service) {
        this.service = service;
    }

    @GetMapping("/consultar-polo-detalhes")
    public String detalhes(@RequestParam(name = "numeroPolo", required = false) Integer numero, Model model) {
        model.addAttribute("pageTitle", "Consultar Detalhes do Polo");
        model.addAttribute("numeroPoloConsultado", numero);

        if (numero != null && service.buscar(numero).isEmpty()) {
            model.addAttribute("error", "Polo não encontrado com o número: " + numero);
        }
        return "polo/formConsultarPoloDetalhes";
    }

    @GetMapping("/{poloNumero}/municipios")
    public String municipios(@PathVariable Integer poloNumero, Model model) {
        var polo = service.buscar(poloNumero);
        if (polo.isPresent()) {
            model.addAttribute("polo", polo.get());
            model.addAttribute("municipios", service.listarMunicipios(poloNumero));
            model.addAttribute("pageTitle", "Municípios do Polo " + poloNumero);
        } else {
            model.addAttribute("error", "Polo não encontrado: " + poloNumero);
            model.addAttribute("pageTitle", "Erro ao buscar Polo");
        }
        return "polo/listarMunicipiosPorPolo";
    }

    @GetMapping("/{poloNumero}/zonas")
    public String zonas(@PathVariable Integer poloNumero, Model model) {
        var polo = service.buscar(poloNumero);
        if (polo.isPresent()) {
            model.addAttribute("polo", polo.get());
            model.addAttribute("zonas", service.listarZonas(poloNumero));
            model.addAttribute("pageTitle", "Zonas do Polo " + poloNumero);
        } else {
            model.addAttribute("error", "Polo não encontrado: " + poloNumero);
            model.addAttribute("pageTitle", "Erro ao buscar Polo");
        }
        return "polo/listarZonasPorPolo";
    }

    @GetMapping("/consultar-polo-por-municipio")
    public String porMunicipio(@RequestParam(required = false) Integer codTse, Model model) {
        model.addAttribute("pageTitle", "Consultar Polo por Município");
        if (codTse != null) {
            service.buscarMunicipio(codTse).ifPresentOrElse(municipio -> {
                model.addAttribute("municipio", municipio);
                var poloNumero = service.buscarNumeroPolo(codTse);
                if (poloNumero != null) {
                    model.addAttribute("poloNumero", poloNumero);
                } else {
                    model.addAttribute("poloError", "Polo não definido para este município.");
                }
            }, () -> model.addAttribute("error", "Município não encontrado com o código TSE: " + codTse));
        }
        return "polo/consultarPoloPorMunicipio";
    }

    @GetMapping("/consultar-polo-por-zona")
    public String porZona(@RequestParam(required = false) Integer numeroZona, Model model) {
        model.addAttribute("pageTitle", "Consultar Polo por Zona");
        if (numeroZona != null) {
            service.buscarZona(numeroZona).ifPresentOrElse(zona -> {
                model.addAttribute("zona", zona);
                var polos = service.listarPolosDaZona(numeroZona);
                if (polos.isEmpty()) {
                    model.addAttribute("poloError", "Nenhum polo associado às seções desta zona, ou esta zona não possui seções cadastradas com polo definido.");
                } else if (polos.size() == 1) {
                    model.addAttribute("poloNumero", polos.get(0));
                } else {
                    String numerosPolos = polos.stream().map(String::valueOf).collect(Collectors.joining(", "));
                    model.addAttribute("poloError", "Múltiplos polos (" + numerosPolos + ") associados às seções desta zona. Verifique a configuração.");
                }
            }, () -> model.addAttribute("error", "Zona não encontrada com o número: " + numeroZona));
        }
        return "polo/consultarPoloPorZona";
    }
}
