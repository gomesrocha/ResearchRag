package com.fabiogomesrocha.infrastructure;


import com.fabiogomesrocha.application.ScholarFetchService;
import com.fabiogomesrocha.application.ScholarIngestionService;
import dev.langchain4j.data.document.Document;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class DailyScholarJob {

    private static final Logger LOG = Logger.getLogger(DailyScholarJob.class);

    @Inject
    ScholarFetchService fetchService;

    @Inject
    ScholarIngestionService ingestionService;

    // Exemplo: 3 páginas x 20 resultados = até 60 resultados brutos (antes do filtro)
    private static final int PAGES = 3;

    @Scheduled(cron = "{ingestor.cron}")
    void runDaily() {
        LOG.info("Starting daily Google Scholar ingestion (SerpApi)...");
        List<Document> docs = fetchService.fetchAsDocuments(PAGES);
        LOG.infof("Documents selected for ingestion: %d", docs.size());
        ingestionService.ingest(docs);
        LOG.info("Daily ingestion finished.");
    }
}