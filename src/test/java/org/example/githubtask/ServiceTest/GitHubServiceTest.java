package org.example.githubtask.ServiceTest;

import org.example.githubtask.DTO.GitHubBranchDTO;
import org.example.githubtask.DTO.GitHubRepositoryDTO;
import org.example.githubtask.Response.GitHubSuccessfulResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGitHubServiceSuccessfully(){
        String username = "Wikt0rP";
        ResponseEntity<GitHubSuccessfulResponse> response = restTemplate.getForEntity(
                "/github/get?username=" + username, GitHubSuccessfulResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GitHubSuccessfulResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        List<GitHubRepositoryDTO> reposFromBody = responseBody.getRepositories();
        assertThat(reposFromBody).isNotNull();

        boolean isValid = reposFromBody.stream().allMatch(repo ->
                !repo.getRepositoryName().isEmpty() &&
                !repo.getOwnerLogin().isEmpty() &&
                repo.getGitBranches().stream().allMatch(branch ->
                    !branch.getBranchName().isEmpty() &&
                    !branch.getCommitMessage().isEmpty() &&
                    !branch.getCommitSha().isEmpty()
                ));

        assertTrue(isValid);
    }
}
