package application.service;

import application.exception.GithubApiException;
import application.exception.GithubUserNotFoundException;
import application.model.GeneralResponse;
import application.model.GithubBranch;
import application.model.GithubRepository;
import application.model.RepositoryResponse;
import application.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class GithubService {

    private final WebClient webClient;

    @Autowired
    public GithubService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<GeneralResponse> getUserRepositories(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()

                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    HttpStatus status = HttpStatus.valueOf(response.statusCode().value());
                                    if (status == HttpStatus.NOT_FOUND) {
                                        return Mono.error(new GithubUserNotFoundException("User not found: " + username));
                                    }
                                    return Mono.error(new GithubApiException("GitHub API error: " + errorBody, status));
                                })
                )

                .bodyToFlux(GithubRepository.class)
                .filter(repo -> !repo.fork())
                .flatMap(this::enrichWithBranches)

                .doOnNext(response -> log.info("Successfully processed repo: {}", response))

                .onErrorContinue((throwable, o) -> log.error("Error processing repository: {}", throwable.getMessage()))

                .collectList()
                .flatMap(repositories -> {
                    if (repositories.isEmpty()) {
                        return ResponseWrapper.prepareErrorResponse(HttpStatus.NOT_FOUND, "No repositories found");
                    } else {
                        return ResponseWrapper.prepareSuccessResponse(repositories);
                    }
                })

                .onErrorResume(GithubUserNotFoundException.class, error -> {
                    log.warn("User not found: {}", username);
                    return ResponseWrapper.prepareErrorResponse(HttpStatus.NOT_FOUND, error.getMessage());
                })
                .onErrorResume(GithubApiException.class, error -> {
                    log.error("GitHub API returned an error: {}", error.getMessage());
                    return ResponseWrapper.prepareErrorResponse(error.getStatus(), error.getMessage());
                })
                .onErrorResume(error -> {
                    log.error("Unexpected error occurred: {}", error.getMessage());
                    return ResponseWrapper.prepareErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
                });
    }


    private Mono<RepositoryResponse> enrichWithBranches(GithubRepository repo) {
        if (repo.branchesUrl() == null || repo.branchesUrl().isBlank()) {
            return Mono.just(new RepositoryResponse(repo.name(), repo.owner().login(), List.of()));
        }

        return webClient.get()
                .uri(repo.branchesUrl().replace("{/branch}", ""))
                .retrieve()
                .bodyToFlux(GithubBranch.class)
                .collectList()
                .map(branches -> new RepositoryResponse(repo.name(), repo.owner().login(), branches))
                .onErrorReturn(new RepositoryResponse(repo.name(), repo.owner().login(), List.of()));
    }
}
