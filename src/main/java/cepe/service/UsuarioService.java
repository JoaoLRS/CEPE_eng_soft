package cepe.service;

import cepe.domain.entity.Usuario;
import cepe.dto.usuario.UsuarioDto;
import cepe.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listarTodos() {
        return repository.findAll().stream()
                .map(usuario -> new UsuarioDto(usuario.getCpf(), usuario.getNome(), usuario.getEmail()))
                .toList();
    }

    @Transactional
    public UsuarioDto saveUsuario(UsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setCpf(dto.cpf());
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());

        Usuario salvo = repository.save(usuario);
        return new UsuarioDto(salvo.getCpf(), salvo.getNome(), salvo.getEmail());
    }
}
