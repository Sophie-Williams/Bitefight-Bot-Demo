# Bitefight-Bot-Demo
A free-to-use program to automate the never-ending story mode in Bitefight, written in Java and combined with Selenium library for automating web tests.
It opens a new Google Chrome browser instance and it clicks through pages similar to a legit user.

## Download and setup
- Download 'bot_v1.06.zip' package, 
- extract its content into any folder (recommended to extract package to a place where administrator permissions are NOT required, e.g. Desktop), 
- and follow instructions given in 'instructions.txt' to setup and run bot.

## Additional protection for paranoids
There are rumours that selenium+chromedriver can be detected by certain websites, and folks seem to have found a workaround to it:
- Download and install hex editor, e.g. HxD (https://mh-nexus.de/en/hxd/),
- open hex editor and choose 'chromedriver.exe' for editing,
- search for string '$cdc', or even better, '$cdc_asdjflasutopfhvcZLmcfl', and replace it with random letters.

## Current version:
19.8.2018 --> v1.06
* updated bot and resources (chromedriver.exe v2.41, Selenium v3.14.0)
25.5.2018 --> v1.05
* bot now detects EU cookies compliance message (introduced on 23.5.2018)
* fixed sudden randomness (now bot follows user-defined story order instead of randomly choosing actions)
