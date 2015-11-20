# Contribution guide to SeaClouds

Great to have you here. If you want to help us with the development of this project please read this document carefully first. Here are a few ways you can help make this project better!

## Learn & listen

If you want to learn more about the project status, or asking some question about our work feel free to gather more information about us here:

* **Mailing list**: *To include*
* **GitHub project repository**: [https://github.com/SeaCloudsEU/](https://github.com/SeaCloudsEU/)
* **Project site**: [http://www.seaclouds-project.eu/](http://www.seaclouds-project.eu/) 
* **Working demo video**: [https://scenic.uma.es/projects/seaclouds/demo.html](https://scenic.uma.es/projects/seaclouds/demo.html)  


Also we encourage to learn more about the project by reading the  [project deliverables](http://www.seaclouds-project.eu/deliverables.html) related with the core module you want to study in order to understand better the decision made to achieve the project objetives.

## Project structure

The project is divided in the following components referred in the documentation of the project:
 
* **dashboard**: user interface as a web application that integrates all other SeaClouds components. [More information](./dashboard/readme.md).
* **deployer**: The deployer is in charge of following the instructions coming as a deployment plan coming from the Planner. [More information](./deployer/readme.md)
* **discoverer**: This sub-system is in charge of identifying the available capabilities offered by cloud providers that will be used by the Planner sub-system to perform a matchmaking process. [More information](./discoverer/readme.md).
* **monitor**: [More information](./monitor/readme.md).
* **planner**: in charge of determining a distribution of application components onto multiple available clouds so that the QoS properties and other technology requirements needed for individual application components are not violated. [More information](./planner/readme.md).
* **api**: TODO.
* **sla**: is in charge of mapping the low level information gathered from the Monitor into business level information about the fulfillment of the SLA defined for a SeaClouds application. [More information](.sla/README.md).

# How to contribute
## Create an Issue in Github
The first step is usually to create or find an issue in SeaClouds' Github for your feature request or fix. For small changes this isn’t necessary, but it’s good to see if your change fixes an existing issue anyway.

Some good references for working with GitHub are below. We ask that you keep your change rebased to master as much as possible, and we will ask you to rebase again if master has moved before accepting your patch.

- [Setting Up Git with GitHub](https://help.github.com/articles/set-up-git)
- [Forking a Repository](https://help.github.com/articles/fork-a-repo)
- [Submitting Pull Requests](https://help.github.com/articles/using-pull-requests)
- [Rebasing your Branch](https://help.github.com/articles/interactive-rebase)
- [Pushing to a remote](https://help.github.com/articles/pushing-to-a-remote/)

For more information, please look at [github FAQ](https://help.github.com/)

## Before creating the PR
Some best practices to submit a good PR:
- create a feature branch (feature/SEACLOUDS-2345) or a fix branch (fix/SEACLOUDS-123) on your forked repository 
- write clean code that follows Effective Java guidelines
- create unit tests for your changes.
- test your patch locally! Using `mvn verify -pl qa -Pintegration`
- commit your code and push it to a remote
- create the PR from Github website. Pull request descriptions should be as clear as possible and include a reference
to all the issues that they address.

Your commit messages must properly describes the changes that have been made and their purpose (here are some [guidelines](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)). If your contributions fix a Github issue, then ensure that you follow our rules about the commit message.

Commit messages must start with a capitalized and short summary (max. 50 chars)
written in the imperative, followed by an optional, more detailed explanatory
text which is separated from the summary by an empty line. 
```<type>(<scope>): <subject>```
( e.g. “FIX (dashboard): Modified resizing on app-monitoring section” )

### Type
* **FEATURE** : A new feature.
* **FIX**: A bug fix.
* **DOCS**: Documentation only changes.
* **STYLE**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc).
* **TEST**: Adding missing tests.

### Code Review
Code review comments may be added to your pull request. Discuss, then make the suggested modifications and push additional commits to your feature branch. Post a comment after pushing. New commits show up in the pull request automatically,
but the reviewers are notified only when you comment.
At the end of the review, pull requests must be cleanly rebased on top of master without multiple branches mixed into the PR.

**Git tip**: If your PR no longer merges cleanly, use `rebase master` in your
feature branch to update your pull request rather than `merge master`.

### Merge approval
Maintainers use LGTM (Looks Good To Me) in comments on the code review to indicate acceptance.

## Some Words of Advice

* Include documentation and tests.
* Keep your code clean and readable (avoid pushing commented code!)
* Try to imitate the existing code style. New code added to Git is expected to match
the overall style of existing code.

Don’t get discouraged! We estimate that the response time from the
maintainers is between 5 nanoseconds and when-we-can-answer-you.

# Bug triage
If you find a bug while using or contributing to SeaClouds feel free to create a new issue on GitHub and label it as “bug”. Some advices when reporting:

* Make sure your bug is not already reported before posting it.
* Provide all the error logs and information that can help us understand what’s going on (using a snippet tool like [Gist](https://gist.github.com/) or [Pastebin](http://pastebin.com/) is recommended when handling large logs).
* Is the bug reproducible? List the steps and platforms used necessary to make it visible (which browser are you using, which Java version, …)

# Documentation

Please have a look at the Readme.md file inside each project module. It contains  the necessary information to understand the basic functionality and how it works in conjunction with the other parts of the projects.

We will provide the [public deliverables](http://www.seaclouds-project.eu/deliverables.html) related with each of the project components in the main project [website](http://www.seaclouds-project.eu).


