# Bitefight-Bot-Demo
A free-to-use program automates Bitefight browser game story. Written in Java, combined with Selenium libraries for automating web tests. Program launches a Google Chrome browser instance navigates through pages similar to a legit user. Demo preview at https://youtu.be/SDad1_7WW8w

## Dependencies
You need:
- Google Chrome (versions 70-72 for bot 1.09, version 73 for bot 1.10), can get latest version from https://www.google.com/chrome/,
- chromedriver is included in zip packages (version 2.4.5 for bot 1.09 and version 73 for bot 1.10), can get latest version from https://sites.google.com/a/chromium.org/chromedriver/,
- Java 8, can get from https://java.com/en/download/

## Setup and run 
- download one of zip packages, 
- extract its content into a folder,
- navigate into folder and run 'bot.jar', 
- check 'instructions.txt' for detailed instructions
  
  * WINDOWS: double-click 'bot.jar'
  * LINUX: open terminal, navigate to extracted folder, run commands 'chmod +x chromedriver' and then 'java -jar bot.jar'
  * MAC: double-click 'bot.jar', then confirm security exception via Apple icon -> System Preferences ... -> Security and Privacy

## Additional steps for less detection
You can edit a certain string in 'chromedriver' file with a hex editor.
- download, install, run hex editor (e.g. https://mh-nexus.de/en/hxd/),
- open 'chromedriver' file, 
- search for string cdc, or even better, cdc_asdjflasutopfhvcZLmcfl_,
- replace it with the equal amount of random letters,
- save file

If you have trouble with saving changes to 'chromedriver' file, close or kill 'chromedriver' process with task manager and retry saving.

## Licensing
This software is licensed under Apache 2.0 license, but it also includes third party open source software components, which have their own licenses. Please see LICENSE/license for more details.

## Current versions:
v1.10 (20.03.2019)
v1.09 (30.01.2019)
