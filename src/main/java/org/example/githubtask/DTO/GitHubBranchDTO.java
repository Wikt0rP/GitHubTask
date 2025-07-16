package org.example.githubtask.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GitHubBranchDTO {
    private String branchName;
    private String commitMessage;
    private String commitSha;


}
