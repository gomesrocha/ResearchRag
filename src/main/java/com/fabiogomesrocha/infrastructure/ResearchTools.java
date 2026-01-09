package com.fabiogomesrocha.infrastructure;


import com.fabiogomesrocha.application.ScholarIngestionService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.document.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fabiogomesrocha.application.ScholarFetchService;

import java.util.List;

@ApplicationScoped
public class ResearchTools {

    @Inject
    ScholarFetchService fetchService;

    @Inject
    ScholarIngestionService ingestionService;

    @Tool("""
        Executa uma nova coleta no Google Scholar (via SerpApi) e ingere os resultados no banco vetorial (pgvector).
        Use quando o usuário pedir para atualizar a base ou buscar por uma nova query.
        Retorna quantos documentos foram ingeridos.
    """)
    public String ingestScholar(String query, int pages) {
        // OBS: para suportar query dinâmica, vamos precisar que ScholarFetchService aceite query por parâmetro.
        // Abaixo eu assumo que você vai criar um método overload (mostro já já).
        List<Document> docs = fetchService.fetchAsDocuments_customQuery(query, pages);
        ingestionService.ingest(docs);
        return "Ingestão concluída. Documentos selecionados e ingeridos: " + docs.size();
    }
}