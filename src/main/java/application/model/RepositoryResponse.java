package application.model;

import java.util.List;

public record RepositoryResponse(
        String name,
        String ownerLogin,
        List<GithubBranch> branches) {}
