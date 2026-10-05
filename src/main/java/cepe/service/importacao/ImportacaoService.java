package cepe.service.importacao;

import cepe.domain.entity.Municipio;
import cepe.domain.entity.Polo;
import cepe.domain.entity.Secao;
import cepe.domain.entity.Zona;
import cepe.repository.MunicipioRepository;
import cepe.repository.PoloRepository;
import cepe.repository.SecaoRepository;
import cepe.repository.ZonaRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class ImportacaoService {
    private static final Logger log = LoggerFactory.getLogger(ImportacaoService.class);

    private final MunicipioRepository municipios;

    private final ZonaRepository zonas;

    private final PoloRepository polos;

    private final SecaoRepository secoes;

    public ImportacaoService(
            MunicipioRepository municipios,
            ZonaRepository zonas,
            PoloRepository polos,
            SecaoRepository secoes) {
        this.municipios = municipios;
        this.zonas = zonas;
        this.polos = polos;
        this.secoes = secoes;
    }

    @Transactional(readOnly = true)
    public boolean importacaoInicialNecessaria() {
        return municipios.count() == 0 && polos.count() == 0 && zonas.count() == 0;
    }

    @Transactional
    public void importarZonasSedes(String caminho) {
        processar(caminho, linha -> {
            if (linha.length < 3) {
                return;
            }

            int numero = Integer.parseInt(linha[0].trim());
            int codigo = Integer.parseInt(linha[1].trim());
            String nome = linha[2].trim();

            Municipio municipio = municipios.findById(codigo).orElseGet(() -> {
                Municipio novo = new Municipio();
                novo.setCodTse(codigo);
                novo.setNome(nome);
                return municipios.save(novo);
            });

            Zona zona = zonas.findById(numero).orElseGet(() -> {
                Zona nova = new Zona();
                nova.setNumero(numero);
                nova.setMunicipioSede(municipio);
                return zonas.save(nova);
            });

            zona.setMunicipioSede(municipio);
            zonas.save(zona);
        });
    }

    @Transactional
    public void importarPolos(String caminho) {
        processar(caminho, linha -> {
            if (linha.length < 3) {
                return;
            }

            int numero = Integer.parseInt(linha[0].trim());
            int codigo = Integer.parseInt(linha[1].trim());
            String nome = linha[2].trim();

            Municipio municipio = municipios.findById(codigo).orElseGet(() -> {
                Municipio novo = new Municipio();
                novo.setCodTse(codigo);
                novo.setNome(nome);
                return municipios.save(novo);
            });

            Polo polo = polos.findById(numero).orElseGet(() -> {
                Polo novo = new Polo();
                novo.setNumero(numero);
                novo.setMunicipioSede(municipio);
                return polos.save(novo);
            });

            polo.setMunicipioSede(municipio);
            polos.save(polo);
        });
    }

    @Transactional
    public void importarSecoes(String caminho) {
        processar(caminho, linha -> {
            if (linha.length < 5) {
                return;
            }

            int codigo = Integer.parseInt(linha[0].trim());
            String nome = linha[1].trim();
            int numeroSecao = Integer.parseInt(linha[2].trim());
            int numeroZona = Integer.parseInt(linha[3].trim());
            int numeroPolo = Integer.parseInt(linha[4].trim());

            Municipio municipio = municipios.findById(codigo).orElseGet(() -> {
                Municipio novo = new Municipio();
                novo.setCodTse(codigo);
                novo.setNome(nome);
                novo.setNumeroPolo(numeroPolo);
                return municipios.save(novo);
            });

            Polo polo = polos.findById(numeroPolo).orElseGet(() -> {
                Polo novo = new Polo();
                novo.setNumero(numeroPolo);
                novo.setMunicipioSede(municipio);
                return polos.save(novo);
            });

            if (municipio.getPolo() == null) {
                municipio.setPolo(polo);
                municipios.save(municipio);
            }

            Zona zona = zonas.findById(numeroZona).orElseGet(() -> {
                Zona nova = new Zona();
                nova.setNumero(numeroZona);
                nova.setMunicipioSede(municipio);
                return zonas.save(nova);
            });

            if (!zona.getMunicipios().contains(municipio)) {
                zona.getMunicipios().add(municipio);
                zonas.save(zona);
            }

            if (!municipio.getZonas().contains(zona)) {
                municipio.getZonas().add(zona);
                municipios.save(municipio);
            }

            Secao secao = new Secao();
            secao.setNumero(numeroSecao);
            secao.setMunicipio(municipio);
            secao.setZona(zona);
            secao.setPolo(polo);
            secoes.save(secao);
        });
    }

    private void processar(String caminho, LinhaProcessor processor) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                new ClassPathResource(caminho).getInputStream(),
                StandardCharsets.UTF_8
        ))) {
            reader.readNext();
            String[] linha;

            while ((linha = reader.readNext()) != null) {
                processor.processar(linha);
            }

            log.info("Importação do recurso {} concluída.", caminho);
        } catch (IOException | CsvValidationException | RuntimeException exception) {
            log.error("Falha ao importar o recurso {}", caminho, exception);
            throw new IllegalStateException("Falha ao importar " + caminho, exception);
        }
    }

    @FunctionalInterface
    private interface LinhaProcessor {
        void processar(String[] linha);
    }
}
