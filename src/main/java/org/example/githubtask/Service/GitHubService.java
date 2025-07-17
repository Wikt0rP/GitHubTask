package org.example.githubtask.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.githubtask.DTO.GitHubBranchDTO;
import org.example.githubtask.DTO.GitHubRepositoryDTO;
import org.example.githubtask.Exception.GitHubUserNotFound;
import org.example.githubtask.Response.FailedResponse;
import org.example.githubtask.Response.GitHubSuccessfulResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
public class GitHubService {
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @Value("${github.token}")
    private String githubToken;
    private final String baseUrl = "https://api.github.com";


    public ResponseEntity<?> handleGitHubRequest(String username){
        String fullUrl = getStringUrlUsersRepos(username);

        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(sendHttpRequestForGit(fullUrl));
        } catch (GitHubUserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }

    }
    private String getStringUrlUsersRepos(String username) {
        URI baseUri = URI.create(baseUrl);
        return UriComponentsBuilder.fromUri(baseUri)
                .path("/users/{username}/repos")
                .buildAndExpand(username)
                .toUriString();
    }


    /*

    Doesn't handle pagination, therefore it returns 30 repos as default (could handle up to 100) with one page

    https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    DOCS:
    Query parameters
        per_page integer
        The number of results per page (max 100). For more information, see "Using pagination in the REST API."
        Default: 30

     */
    private GitHubSuccessfulResponse sendHttpRequestForGit(String fullUrl) throws GitHubUserNotFound {
        HttpRequest httpRequest = BuildRequest(fullUrl);

        try {
            HttpResponse<String> responseJsonString = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(responseJsonString.statusCode() == HttpStatus.NOT_FOUND.value())
                throw new GitHubUserNotFound("User with given name does not exist");
            return new GitHubSuccessfulResponse(mapRepos(responseJsonString.body()));
        }
        catch (IOException | InterruptedException e) {
            throw new GitHubUserNotFound("Can not connect with api");
        }
    }

    private List<GitHubRepositoryDTO> mapRepos(String json) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        List<GitHubRepositoryDTO> result = new ArrayList<>();

        for(JsonNode repoNode : root){
            String repoName = repoNode.get("name").asText();
            String ownerLogin = repoNode.get("owner").get("login").asText();
            if(repoNode.get("fork").asBoolean()) continue;

            String branchesJson = sendBranchesRequestForGit(ownerLogin, repoName);
            List<GitHubBranchDTO> branches = new ArrayList<>();
            if(branchesJson != null){
                JsonNode branchesNode = mapper.readTree(branchesJson);
                for(JsonNode branchNode : branchesNode) {
                    String branchName = branchNode.get("name").asText();
                    String commitSha = branchNode.get("commit").get("sha").asText();
                    String commitUrl = branchNode.get("commit").get("url").asText();

                    String commitMessage = getCommitMessage(commitUrl);
                    branches.add(new GitHubBranchDTO(branchName, commitMessage, commitSha));
                }
            }
            GitHubRepositoryDTO repo = new GitHubRepositoryDTO(repoName, ownerLogin, branches);
            result.add(repo);
        }
        return result;
    }
    private String sendBranchesRequestForGit(String owner, String repoName){
        URI baseUri = URI.create(baseUrl);
        String fullUrl = UriComponentsBuilder.fromUri(baseUri)
                .path("/repos/{owner}/{repo}/branches")
                .buildAndExpand(owner, repoName)
                .toUriString();

        HttpRequest httpRequest = BuildRequest(fullUrl);

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private String getCommitMessage(String commitUrl){
        HttpRequest httpRequest = BuildRequest(commitUrl);
        try{
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200){
                ObjectMapper mapper = new ObjectMapper();
                JsonNode commitNode = mapper.readTree(response.body()).get("commit");
                if(commitNode != null && commitNode.has("message")){
                    return commitNode.get("message").asText();
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private HttpRequest BuildRequest(String fullUrl) {
        return HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + githubToken)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }
}
