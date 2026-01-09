package com.fabiogomesrocha.api;


import com.fabiogomesrocha.application.ResearchAgent;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/research")
public class ResearchAgentResource {

    @Inject
    ResearchAgent agent;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String ask(@HeaderParam("X-Memory-Id") String memoryId, String question) {
        String mid = (memoryId == null || memoryId.isBlank()) ? "session-123" : memoryId;
        return agent.chat(mid, question);
    }
}