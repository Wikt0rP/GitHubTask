package org.example.githubtask.Controller;

import org.example.githubtask.Service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
public class GitHubApiController {
    private final GitHubService gitHubService;

    public GitHubApiController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getGitHubInfoByUser(@RequestParam String username){
        return gitHubService.handleGitHubRequest(username);
    }
}
