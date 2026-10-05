package cepe.service.importacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ImportacaoService importacao;

    public DataInitializer(ImportacaoService importacao) {
        this.importacao = importacao;
    }

    @Override
    public void run(String... args) {
        if (importacao.importacaoInicialNecessaria()) {
            importacao.importarPolos("csv/polos-sedes.csv");
            importacao.importarZonasSedes("csv/zonas-sedes.csv");
            importacao.importarSecoes("csv/secoes.csv");
            log.info("Importação inicial concluída com sucesso.");
        } else {
            log.info("Dados já existentes; importação inicial ignorada.");
        }
    }
}
