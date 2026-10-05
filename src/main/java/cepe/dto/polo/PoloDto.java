package cepe.dto.polo;

import cepe.dto.municipio.MunicipioDto;

public record PoloDto(
        Integer numero,
        MunicipioDto municipioSede
) {
}
