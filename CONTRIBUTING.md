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

## Adding new features


If you've built something which you think others could use, or are interested in doing so it's easy to give back to the community. Just:

* Tell the SeaClouds mailing list about your work or interest.
* Create your fork of the project on GitHub.
* Clone it to your local machine and do your work on it.
* Push it, and tell everyone about it.
* Issue a pull request from your GitHub repo.
#### Git Commit Guidelines

We have very precise rules over how our git commit messages can be formatted. This leads to more readable messages that are easy to follow when looking through the project history.

Each commit must have the following structure (since 17/12/2014):
```<type>(<scope>): <subject>```
( e.g. “FIX (dashboard): Modified resizing on app-monitoring section” )

###### Type

* **FEATURE** : A new feature.
* **FIX**: A bug fix.
* **DOCS**: Documentation only changes.
* **STYLE**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc).
* **TEST**: Adding missing tests.

###### Scope

Scope of the changes, often it will be the name of the module involved in this commit (planner, monitor, deployer…).


#### Some Words of Advice

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


