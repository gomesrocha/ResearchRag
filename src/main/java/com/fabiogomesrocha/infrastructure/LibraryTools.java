package com.fabiogomesrocha.infrastructure;


import com.fabiogomesrocha.application.ResearchLibraryService;
import com.fabiogomesrocha.domain.model.Paper;
import com.fabiogomesrocha.domain.model.PaperStatus;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.MemoryId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.stream.Collectors;

@ApplicationScoped
public class LibraryTools {

    @Inject
    ResearchLibraryService library;

    @Tool("Lista os papers salvos na biblioteca do usuário para esta sessão (memoryId).")
    public String listSaved(@MemoryId String memoryId) {
        var items = library.listSaved(memoryId);
        if (items.isEmpty()) return "Nenhum paper salvo ainda.";

        return items.stream()
                .map(p -> "- [" + p.resultId() + "] " + p.title() + " (" + p.link() + ") status=" + p.status())
                .collect(Collectors.joining("\n"));
    }

    @Tool("""
        Salva um paper na biblioteca do usuário.
        Use quando o usuário disser algo como "salva esse paper", informando resultId, título e link.
    """)
    public String savePaper(@MemoryId String memoryId, String resultId, String title, String link) {
        var saved = library.savePaper(memoryId, new Paper(
                resultId, title, link, "", null, null, "", PaperStatus.SAVED
        ));
        return "Paper salvo: " + saved.get().title() + " (id=" + saved.get().resultId() + ")";
    }

    @Tool("Marca um paper como descartado (dismissed) na biblioteca do usuário.")
    public String dismiss(@MemoryId String memoryId, String resultId) {
        boolean ok = library.dismissPaper(memoryId, resultId);
        return ok ? "Paper " + resultId + " marcado como descartado." : "Paper não encontrado na biblioteca.";
    }
}
