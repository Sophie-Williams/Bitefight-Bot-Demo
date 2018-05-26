# Bitefight-Bot-Demo
Bitefight bot was made to automate the never-ending story mode, written in Java and combined with Selenium library for automating web tests.
It opens a new Google Chrome browser instance and it clicks through pages similar to a legit user.

## Download and setup
Ignore source code if you're not developer and you only want to use bot.
- Download 'bot_v1.05.zip' package, 
- extract its content into any folder (recommended to extract package to a place where administrator permissions are not required, e.g. Desktop), 
- and follow instructions given in 'instructions.txt' to setup and run bot.

Removed aspect-related bot since it apparently started doing more harm than good. If you have trouble with it, abandon it and use normal bot instead.

## Additional protection for paranoids
There are rumours that selenium+chromedriver can be detected by certain websites, and folks seem to have found a workaround to it:
- Download and install hex editor, e.g. HxD (https://mh-nexus.de/en/hxd/),
- choose 'chromedriver.exe' for editing,
- search for string '$cdc', or even better, '$cdc_asdjflasutopfhvcZLmcfl', and replace it with random letters.

## Current version:
25.5.2018 --> v1.05
* bot now detects EU cookies compliance message (introduced on 23.5.2018)
* fixed sudden randomness (now bot follows user-defined story order instead of randomly choosing actions)
