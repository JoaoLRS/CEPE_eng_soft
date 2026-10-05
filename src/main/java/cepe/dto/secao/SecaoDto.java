package cepe.dto.secao;

public record SecaoDto(
        Long id,
        Integer numero,
        String municipioNome,
        Integer zonaNumero
) {
}
