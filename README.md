# Bitefight-Bot-Demo
A free-to-use program to automate the never-ending story mode in Bitefight, written in Java and combined with Selenium library for automating web tests.
It opens a new Google Chrome browser instance and it clicks through pages similar to a legit user.

## Dependencies
You need:
- Google Chrome (v70-v72 needed), can get it from here: https://www.google.com/chrome/,
- Java 8, can get it from here: https://java.com/en/download/,
- chromedriver v2.4.5 is included in .zip package, but can get its other versions from here: https://sites.google.com/a/chromium.org/chromedriver/

## Setup and run 
- download 'bot109.zip' package (either for Windows or Linux), 
- extract its content into a folder,
- run 'bot.jar', 
- check 'instructions.txt' for detailed instructions
  
  * WINDOWS: double-click 'bot.jar'
  * LINUX: open terminal, navigate to extracted folder, run commands 'chmod +x chromedriver' and then 'java -jar bot.jar'
  * MAC: double-click 'bot.jar', then confirm security exception via Apple icon -> System Preferences ... -> Security and Privacy

## Additional steps for less detection
You can edit a certain string in 'chromedriver' file with a hex editor, but make a backup copy of it just in case.
- download, install, run hex editor (e.g. https://mh-nexus.de/en/hxd/),
- open 'chromedriver' file, 
- search for string cdc (or even better) cdc_asdjflasutopfhvcZLmcfl_,
- replace it with the equal amount of random letters,
- save file

If you have trouble with saving changes to 'chromedriver' file, close 'chromedriver' process first if it is running (e.g. with Task Manager in Windows OS).

## Current version:
v1.09 (30.01.2019)
