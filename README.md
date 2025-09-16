# DecisionTable Package Repository
This repository contains muliple packages that the MegaKnytes have developed. Each package is contained within its own gradle module, and all are published to the [Github Package Repository](https://github.com/orgs/MegaKnytes/packages).

Versioning is accomplished through the use of branches, tags, and releases. A [Github Action](.github/workflows/gradle-publish.yml) is run on every commit and created release, which will upload each generated package to the Github Package Repository. The version of each generated package will be either the branch name, or the tag name.


## Package List:
### Core (WIP):
This package contains all of the core Java code required to run an XML Decision Table Processor.
### Editor (WIP):
This package contains all of the Java code required to host a Node.js webapp on the Control Hub, which is used to edit the XML Decision Table Processors.


## Using these Packages:
To use these packages, you must add a repository to your gradle project.

- **Step 1**: Gradle Repository Setup
    - Add the following sections into your repositories `build.dependencies.grade` file:
        ```
          ...
  
        repositories {
              maven {
                url = uri("https://maven.megaknytes.org/releases")
              }
          ...
        ```
- **Step 2**: Gradle Dependency Setup
    - Add the following section into the dependencies section of your repositories `build.dependencies.grade` file:
        ```
        dependencies {
          ...
        
          implementation 'org.megaknytes.ftc.decisiontable:core:<VERSION_TAG>
          implementation 'org.megaknytes.ftc.decisiontable:editor:<VERSION_TAG>
        
          ...
        ```
        - `<VERSION_TAG>`: The tag or branch of the version that you would like to import (use `main` for the latest version, or the latest tag for stable)