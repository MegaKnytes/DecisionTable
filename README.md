# MegaKnytes Package Repository
This repository contains muliple packages that the MegaKnytes have developed for season-to-season reuse. Each package is contained within its own gradle module, and all are published to the [Github Package Repository](https://github.com/orgs/MegaKnytes/packages).

Versioning is accomplished through the use of branches, tags, and releases. A [Github Action](.github/workflows/gradle-publish.yml) is run on every commit and created release, which will upload each generated package to the Github Package Repository. The version of each generated package will be either the branch name, or the tag name.


## Package List:
### DecisionTable (WIP):
This package contains all of the Java code required to run an XML Decision Table Processor.
### DecisionViewer (WIP):
This package contains all of the Java code required to host a Node.js webapp on the Control Hub, used to edit the XML Decision Table Processors.
### Utilities:
This package contains season-to-season utility classes
### KnyteVision:
This package was created during the CenterStage season as a custom vision algorhithm that can detect the ROI for blobs of color when given a template.


## Using these Packages:
To use these packages, you first must generate a Personal Access Token (Classic) with the ability to read this organization's packages.
> [!NOTE]
> Every user that will clone and use a repository to push code to the robot must complete these steps, or they will not have access to the dependencies.
- **Step 1**: Generating a Personal Access Token (Classic)
  -   Navigate to your Github account [token settings page](https://github.com/settings/tokens), and select `Generate New Token` in the upper-right hand corner of the page.
  -   Select `Generate new Token (Classic)` from the dropdown menu that appears
  -   Use the following settings to generate your token:
      - Note: `Gradle Repository PAT - <Machine Name>`
      - Exparation: `<Insert Chosen Exparation Here>`
        - Note: While setting up auto-exparation of keys is a good security practice, this key will not pose any security risks if set to `No exparation` and may pose an additional annoyance to replace on the keys exparation
      - Scope: `read:packages`
  - Copy the token for use in the next step of the process
- **Step 2**: Gradle Repository Setup
  - Add the following sections into your repositories `build.dependencies.grade` file:
      ```
      Properties properties = new Properties()
      properties.load(project.rootProject.file('local.properties').newDataInputStream())

        ...

      repositories {
            maven {
              url = uri("https://maven.pkg.github.com/MegaKnytes/MegaKnytes")
              credentials {
                  username = properties.getProperty('gpr.user')
                  password = properties.getProperty('gpr.token')
              }
        ...
      ```
  - Add the following into your repositories `local.properties' file (if none exists, create one) using the Personal Access Token (Classic) that you just generated:
    ```
    gpr.user=<GITHUB_USERNAME>
    gpr.token=<GITHUB_PERSONAL_ACCESS_TOKEN>
    ```
> [!CAUTION]
> You must ensure that your `local.properties` file is included in your `.gitignore`, otherwise you will run the risk of exposing your Personal Access token to the entire world (this is a really bad idea)
  - Add the following section into the dependencies section of your repositories `build.dependencies.grade` file:
      ```Groovy
      implementation 'com.megaknytes.ftc:<PACKAGE_NAME>:<VERSION_TAG>
      ```
      - `<PACKAGE_NAME>`: One of knytevision, utilites, decisiontable, or decisionviewer
      - `<VERSION_TAG>`: The tag or branch of the version that you would like to import (use `main` for the latest version, or the latest tag for stable)
