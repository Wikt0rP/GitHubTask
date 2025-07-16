package org.example.githubtask.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GitHubRepositoryDTO {
    private String repositoryName;
    private String ownerLogin;
    private List<GitHubBranchDTO> gitBranches;
}
