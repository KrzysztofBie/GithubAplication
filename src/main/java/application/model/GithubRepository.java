package application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepository(
        String name,
        Owner owner,
        boolean fork,
        @JsonProperty("branches_url") String branchesUrl
) {}

