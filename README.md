# GitHubTask

## Overview

This Spring Boot application exposes a REST endpoint to fetch all public, non-forked repositories for a given GitHub user. For each repository it retrieves the list of branches, along with the last commit SHA and commit message for each branch.

---

## Features

- Fetch public repositories for a specified user  
- Exclude forked repositories  
- List all branches per repository  
- Retrieve last commit SHA and commit message for each branch  
- Return structured JSON responses for success and error cases  

---

## Architecture

The code follows a layered design:

- **Controller**  
  - `GitHubApiController` handles `/github/get?username={username}` requests and delegates to service.  
- **Service**  
  - `GitHubService` builds and sends HTTP requests to GitHub’s REST API, handles errors, and maps JSON to DTOs.  
- **DTOs & Response Objects**  
  - `GitHubRepositoryDTO`, `GitHubBranchDTO` for data transfer  
  - `GitHubSuccessfulResponse`, `FailedResponse` for wrapping API results  

---

## API Endpoint

| Method | Path          | Query Parameter | Description                                            | Success Response           | Error Response           |
| ------ | ------------- | --------------- | ------------------------------------------------------ | -------------------------- | ------------------------ |
| GET    | `/github/get` | `username`      | Returns all public, non-forked repositories and branches for the user. | `GitHubSuccessfulResponse` | `FailedResponse` (404)   |

---

## Data Transfer Objects

**GitHubRepositoryDTO**

| Field            | Type                     | Description                |
| ---------------- | ------------------------ | -------------------------- |
| `repositoryName` | `String`                 | Name of the repository     |
| `ownerLogin`     | `String`                 | Owner’s GitHub login       |
| `gitBranches`    | `List<GitHubBranchDTO>`  | List of branches with commit details |

**GitHubBranchDTO**

| Field           | Type     | Description                         |
| --------------- | -------- | ----------------------------------- |
| `branchName`    | `String` | Name of the branch                  |
| `commitSha`     | `String` | SHA hash of the last commit         |
| `commitMessage` | `String` | Commit message of the last commit   |

---

## Response Models

**GitHubSuccessfulResponse**

| Field          | Type                         | Description                          |
| -------------- | ---------------------------- | ------------------------------------ |
| `repositories` | `List<GitHubRepositoryDTO>`  | List of repository DTOs in the response |

**FailedResponse**

| Field    | Type      | Description                         |
| -------- | --------- | ----------------------------------- |
| `status` | `Integer` | HTTP status code (e.g., 404)        |
| `message`| `String`  | Error message describing the issue  |

---

## Configuration

This application requires a GitHub API token. Set the `GITHUB_TOKEN` environment variable and bind it in your `application.properties`:

```properties
github.token=${GITHUB_TOKEN}
