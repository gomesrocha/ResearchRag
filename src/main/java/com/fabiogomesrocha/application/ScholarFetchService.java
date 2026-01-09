package com.fabiogomesrocha.application;

import com.fabiogomesrocha.domain.model.ScholarResponse;
import com.fabiogomesrocha.infrastructure.SerpApiClient;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@ApplicationScoped
public class ScholarFetchService {

    private static final String ENGINE = "google_scholar";

    // Ajuste os termos conforme sua estratégia
    private static final Set<String> MICROservice_TERMS = Set.of(
            "microservice", "microservices",
            "service mesh", "istio", "envoy",
            "kubernetes", "docker",
            "distributed system", "cloud native",
            "saga", "cqrs"
    );

    @RestClient
    SerpApiClient client;

    @ConfigProperty(name = "serpapi.api-key")
    String apiKey;

    @ConfigProperty(name = "scholar.query", defaultValue = "microservices")
    String query;

    @ConfigProperty(name = "scholar.hl", defaultValue = "en")
    String hl;

    @ConfigProperty(name = "scholar.num", defaultValue = "20")
    int num;

    public List<Document> fetchAsDocuments(int pages) {
        List<Document> docs = new ArrayList<>();

        for (int page = 0; page < pages; page++) {
            int start = page * num;

            ScholarResponse resp = client.searchScholar(
                    ENGINE,
                    query,
                    hl,
                    num,
                    start,
                    apiKey
            );

            if (resp == null || resp.organic_results == null) continue;
            if (resp.search_metadata != null && resp.search_metadata.status != null
                    && !"Success".equalsIgnoreCase(resp.search_metadata.status)) {
                continue;
            }

            for (ScholarResponse.OrganicResult r : resp.organic_results) {
                if (!looksLikeMicroservices(r)) continue;

                String text = buildText(r);

                Metadata md = new Metadata();
                md.put("source", "serpapi_google_scholar");
                md.put("query", query);
                md.put("title", safe(r.title));
                md.put("link", safe(r.link));
                md.put("result_id", safe(r.result_id));
                md.put("position", r.position == null ? "" : r.position.toString());

                if (r.publication_info != null) {
                    md.put("publication_summary", safe(r.publication_info.summary));
                }
                if (r.inline_links != null && r.inline_links.cited_by != null) {
                    md.put("cited_by_total", r.inline_links.cited_by.total == null ? "" : r.inline_links.cited_by.total.toString());
                    md.put("cites_id", safe(r.inline_links.cited_by.cites_id));
                }

                docs.add(Document.from(text, md));
            }
        }

        return docs;
    }
    public List<Document> fetchAsDocuments_customQuery(String customQuery, int pages) {
        String original = this.query;
        try {
            this.query = customQuery;
            return fetchAsDocuments(pages);
        } finally {
            this.query = original;
        }
    }
    private boolean looksLikeMicroservices(ScholarResponse.OrganicResult r) {
        String blob = (safe(r.title) + " " + safe(r.snippet) + " " +
                (r.publication_info != null ? safe(r.publication_info.summary) : ""))
                .toLowerCase(Locale.ROOT);

        for (String term : MICROservice_TERMS) {
            if (blob.contains(term)) return true;
        }
        return false;
    }

    private String buildText(ScholarResponse.OrganicResult r) {
        String pub = (r.publication_info != null) ? safe(r.publication_info.summary) : "";
        return """
                TITLE: %s
                PUBLICATION: %s
                LINK: %s

                ABSTRACT/SNIPPET:
                %s
                """.formatted(
                safe(r.title),
                pub,
                safe(r.link),
                safe(r.snippet)
        );
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
