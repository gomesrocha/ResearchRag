package com.fabiogomesrocha.domain.model;

import java.time.LocalDate;

public record Paper(
        String resultId,          // do SerpApi (result_id)
        String title,
        String link,
        String publicationSummary,
        Integer citedByTotal,
        LocalDate year,           // opcional (se extrair)
        String query,             // query que trouxe o paper
        PaperStatus status
) {}
