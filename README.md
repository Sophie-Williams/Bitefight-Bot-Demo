# Bitefight-Bot-Demo
A free-to-use program to automate the never-ending story mode in Bitefight, written in Java and combined with Selenium library for automating web tests.
It opens a new Google Chrome browser instance and it clicks through pages similar to a legit user.

## Dependencies
You need:
- Google Chrome, can get it from here: https://www.google.com/chrome/,
- Java 8, can get it from here: https://java.com/en/download/,
- chromedriver is included in .zip package, can get its latest version from here: https://sites.google.com/a/chromium.org/chromedriver/

## Setup and run 
- download 'bot_v1.07.zip' package ('win' for Windows OS, 'linux' for Linux OS), 
- extract its content into a folder
- run 'bot.jar', check 'instructions.txt' for instructions
  
  * WINDOWS: double-click bot.jar
  * LINUX: open terminal and use command 'java -jar bot.jar'


## Current version:
24.8.2018 --> v1.07
* improved bot and added user interface
* now also available for linux OS

19.8.2018 --> v1.06
* updated bot and resources (ChromeDriver 2.41, Selenium 3.14.0)

25.5.2018 --> v1.05
* bot now detects EU cookies compliance message (introduced on 23.5.2018)
* fixed sudden randomness (now bot follows user-defined story order instead of randomly choosing actions)
