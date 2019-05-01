# Bitefight-Bot-Demo
A free-to-use program automates Bitefight browser game story. Written in Java, combined with Selenium libraries for automating web tests. Program launches a Google Chrome browser instance navigates through pages similar to a legit user. Demo preview at https://youtu.be/lTHZOEu7wuM

## Dependencies
You need:
- Google Chrome, can get latest version from https://www.google.com/chrome/,
- chromedriver is included in zip packages, can get latest version from https://sites.google.com/a/chromium.org/chromedriver/, <br>
- Java 8, can get latest version from https://java.com/en/download/

> !! IMPORTANT !! 
> make sure you have a proper chromedriver version for 
> your Google Chrome or you will get version mismatch error;
> if, for example, your Google Chrome gets updated from v73 to v74,
> simply get chromedriver v74 to match Google Chrome v74.

## Setup and run 
- download release zip package, 
- extract its content into a folder,
- navigate into folder and run 'bot.jar', 
- check 'instructions.txt' for detailed instructions
  
  * WINDOWS: double-click 'bot.jar'

## Additional steps for less detection
You can edit a certain string in 'chromedriver' file with a hex editor.
- download, install, run hex editor (e.g. https://mh-nexus.de/en/hxd/),
- open 'chromedriver' file, 
- search for string cdc, or even better, cdc_asdjflasutopfhvcZLmcfl_,
- replace it with the equal amount of random letters,
- save file

If you have trouble with saving changes to 'chromedriver' file, <br>
close or kill 'chromedriver' process with task manager and retry saving.

## Licensing
This software is licensed under Apache 2.0 license, <br>
but it also includes third party open source software components, <br>
which have their own licenses. Please see LICENSE/license for more details.

## Current versions:
v1.10 (26.04.2019) for Google Chrome v74 <br>
- added warning and error notices, <br>
- expanded to two story modes: custom priority order (original) and leveling wanted aspects (new), <br>
- ability to check for missing story actions, <br>
- more options with user settings <br>
