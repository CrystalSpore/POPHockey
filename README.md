

# Cache Data Versioning
Check [CACHE_FORMAT.md](https://github.com/CrystalSpore/POPHockey/blob/main/CACHE_FORMAT.md) for format of the `nhl_cache` file. Should only need to reference this if the bot states it needs updating on start up. The bot will also state the version to update to.

On v1.0.1 & prior, this file would always be located in the home directory (on unix environments `~/.nhl_cache`, & on Windows `C:\Users\<Username>\.nhl_cache`). On v1.1 & later, the config file has been moved to a new location (on unix environments `~/.config/POPHockey/nhl_cache`, & on Windows `C:\Users\<Username>\POPHockey\nhl_cache`). On upgrading to a new version, the POPHockey application will check the old location & migrate the file to the new location automatically.

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

<br/>

-------

## log4j-1.2 warnings at runtime:

In release 1.0 (now named 1.0.0), `slf4j-log4j12` was an included dependency, which uses `log4j 1.2` as it's backend. I have since switched out the backend for `slf4j-api`. While I wasn't doing any logging using `slf4j-log4j12`, one of my dependencies (RestEasy), was using this logging framework for logging.

**At this time, for best security & peace of mind, I advise to upgrade to release `v1.0.1` (or greater)**

