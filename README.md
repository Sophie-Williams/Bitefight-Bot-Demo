# Bitefight-Bot-Demo
A free-to-use program to automate the never-ending story mode in Bitefight, written in Java and combined with Selenium library for automating web tests.
It opens a new Google Chrome browser instance and it clicks through pages similar to a legit user.

## Dependencies
You need:
- Google Chrome, can get it from here: https://www.google.com/chrome/,
- Java 8, can get it from here: https://java.com/en/download/,
- chromedriver is already included in .zip package, but can get its latest version from here: https://sites.google.com/a/chromium.org/chromedriver/

## Setup and run 
- download 'bot_v1.08.zip' package (win = Windows OS / linux = Linux OS / mac = Mac OS), 
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

If you have trouble with saving changes to 'chromedriver' file, close 'chromedriver' process (e.g. with Task Manager in Windows).

## Current version:
19.10.2018 --> v1.08
* added option to select former (expired) servers from dropdown menu at login page
* need volunteers to test it and report as I have no experience with accounts on expired servers

24.8.2018 --> v1.07a
* fixed user interface layouts
* now also available for MacOS

24.8.2018 --> v1.07
* improved bot and added user interface
* now also available for linux OS

19.8.2018 --> v1.06
* updated bot and resources (ChromeDriver 2.41, Selenium 3.14.0)

25.5.2018 --> v1.05
* bot now detects EU cookies compliance message (introduced on 23.5.2018)
* fixed sudden randomness (now bot follows user-defined story order instead of randomly choosing actions)
