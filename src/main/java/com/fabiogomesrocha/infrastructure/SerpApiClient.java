package com.fabiogomesrocha.infrastructure;


import com.fabiogomesrocha.domain.model.ScholarResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/search.json")
@RegisterRestClient(configKey = "serpapi")
public interface SerpApiClient {

    @GET
    ScholarResponse searchScholar(
            @QueryParam("engine") String engine,
            @QueryParam("q") String q,
            @QueryParam("hl") String hl,
            @QueryParam("num") Integer num,
            @QueryParam("start") Integer start,
            @QueryParam("api_key") String apiKey
    );
}
