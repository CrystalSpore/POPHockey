# Cache Data Versioning
Check [CACHE_FORMAT.md](https://github.com/CrystalSpore/POPHockey/blob/main/CACHE_FORMAT.md) for format of the `.nhl_cache` file in your home directory. Should only need to reference this if the bot states it needs updating on start up. The bot will also state the version to update to.

# Setup Dev Environment
The project is currently set up for IntelliJ. Before working on code with a different IDE, please contact [Crystal](https://github.com/CrystalSpore) to modify the gitignore. Terminal editing should have no issue.

### IntelliJ
1) Clone project
2) In IntelliJ chose `New > Project from Existing Sources... > select build.gradle of project`
3) Happy coding

# Build App
*nix environment: `./gradlew clean shadowJar`

Windows environment `./gradlew.bat clean shadowJar`

App is built to `./build/libs`

use `build` instead of `shadowJar` to build minimized jar file (longer build time)

# Run App from build
`java -jar POPHockey-<version>-all.jar <discord bot token>`

# Contribute
Look in issues for "Good first issue" if you are new & want a suggestion of where to start. Otherwise, feel free to tackle any issue, or suggest/implement a new feature.