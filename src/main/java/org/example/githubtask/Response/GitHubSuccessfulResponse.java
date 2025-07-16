package org.example.githubtask.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.githubtask.DTO.GitHubRepositoryDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GitHubSuccessfulResponse {
    private List<GitHubRepositoryDTO> repositories;
}
