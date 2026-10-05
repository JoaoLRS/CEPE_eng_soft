package cepe.controller;

import cepe.dto.usuario.UsuarioDto;
import cepe.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/cadastrar-usuario")
    public String cadastrar(Model model) {
        model.addAttribute("usuario", new UsuarioDto("", "", ""));
        return "usuario/cadastrarUsuario";
    }

    @PostMapping("/cadastrar-usuario/salvar")
    public String salvar(@ModelAttribute("usuario") UsuarioDto dto, Model model) {
        try {
            service.saveUsuario(dto);
            return "redirect:/consultar-usuario";
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("erro", "CPF ou e-mail já cadastrado.");
            model.addAttribute("usuario", dto);
            return "usuario/cadastrarUsuario";
        }
    }

    @GetMapping("/consultar-usuario")
    public String consultar(Model model) {
        model.addAttribute("usuarios", service.listarTodos());
        return "usuario/consultarUsuario";
    }
}
