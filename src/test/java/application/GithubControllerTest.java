package application;

import application.model.GeneralResponse;
import application.model.RepositoryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class GithubApiIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    /* getRepositories test**/
    @Test
    void shouldReturnRepositoriesBy() {
        //execute positive request to get response
        GeneralResponse response = webTestClient.get()
                .uri("/api/github/octocat/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GeneralResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(HttpStatus.OK.toString());

        assertThat(response.object()).isInstanceOf(List.class);
        Object object =  response.object();

        ObjectMapper mapper = new ObjectMapper();
        List<RepositoryResponse> repositories = mapper.convertValue(object, new TypeReference<List<RepositoryResponse>>() {});
        assertThat(repositories).isNotEmpty();

        String ownerLogin = repositories.getFirst().ownerLogin();

        repositories.forEach(repo -> {
            assertThat(repo.name()).isNotBlank();
            assertThat(repo.ownerLogin())
                    .isNotBlank()
                    .isEqualTo(ownerLogin);

            repo.branches().forEach(branch -> {
                assertThat(branch.name()).isNotBlank();
                assertThat(branch.commit().sha()).isNotBlank();
            });
        });
    }

}