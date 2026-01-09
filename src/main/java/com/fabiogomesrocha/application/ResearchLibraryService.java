package com.fabiogomesrocha.application;


import com.fabiogomesrocha.domain.model.Paper;
import com.fabiogomesrocha.domain.model.PaperStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class ResearchLibraryService {

    // memoryId -> (resultId -> Paper)
    private final Map<String, Map<String, Paper>> libraries = new HashMap<>();

    public List<Paper> listSaved(String memoryId) {
        return new ArrayList<>(libraries.getOrDefault(memoryId, Map.of()).values());
    }

    public Optional<Paper> savePaper(String memoryId, Paper paper) {
        libraries.computeIfAbsent(memoryId, k -> new HashMap<>());
        libraries.get(memoryId).put(paper.resultId(), paper);
        return Optional.of(paper);
    }

    public boolean dismissPaper(String memoryId, String resultId) {
        Map<String, Paper> lib = libraries.get(memoryId);
        if (lib == null) return false;
        Paper p = lib.get(resultId);
        if (p == null) return false;

        lib.put(resultId, new Paper(
                p.resultId(), p.title(), p.link(), p.publicationSummary(),
                p.citedByTotal(), p.year(), p.query(), PaperStatus.DISMISSED
        ));
        return true;
    }

    public Optional<Paper> findSaved(String memoryId, String resultId) {
        return Optional.ofNullable(libraries.getOrDefault(memoryId, Map.of()).get(resultId));
    }
}