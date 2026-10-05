package cepe.dto.zona;

import cepe.dto.municipio.MunicipioDto;

import java.util.List;

public record ZonaDto(
        Integer numero,
        MunicipioDto municipioSede,
        List<MunicipioDto> municipios
) {
}
