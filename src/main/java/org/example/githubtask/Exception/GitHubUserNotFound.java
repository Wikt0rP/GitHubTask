package org.example.githubtask.Exception;

public class GitHubUserNotFound extends RuntimeException {
    public GitHubUserNotFound(String message) {
        super(message);
    }
}
