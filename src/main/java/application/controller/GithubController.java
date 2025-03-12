package application.controller;

import application.model.GeneralResponse;
import application.service.GithubService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/github")
public class GithubController {
    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}/repos")
    public Mono<GeneralResponse> getRepositories(
        @PathVariable("username") String username
    ) {
        return githubService.getUserRepositories(username);
    }
}

