package com.fabiogomesrocha.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScholarResponse {
    public SearchMetadata search_metadata;
    public SearchInformation search_information;
    public List<OrganicResult> organic_results;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchMetadata {
        public String status; // "Success" etc
        public String id;
        public String created_at;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchInformation {
        public Long total_results;
        public String query_displayed;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrganicResult {
        public Integer position;
        public String title;
        public String link;
        public String snippet;
        public PublicationInfo publication_info;
        public InlineLinks inline_links;
        public String result_id;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PublicationInfo {
            public String summary;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class InlineLinks {
            public CitedBy cited_by;

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class CitedBy {
                public Integer total;
                public String cites_id;
            }
        }
    }
}
