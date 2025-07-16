package org.example.githubtask.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
public class GitHubApiController {

    @GetMapping
    public ResponseEntity<?> getGitHubInfoByUser(@RequestParam String username){

    }
}
