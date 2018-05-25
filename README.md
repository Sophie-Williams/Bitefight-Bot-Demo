# NOTICE
Please do not use bot for a while as users report that bot randomly chooses story actions.

# Bitefight-Bot-Demo
An automated remote control program (so-called bot) for a popular browser game Bitefight, written in Java combined with Selenium library for automating tests. Only story mode and managing aspects are supported, tested it on x4 speedservers 202 and 23. 

Unlike other bots which are programmed as browser plugins to control your Bitefight Hero and introduce their own interface and logs, this bot opens a new Google Chrome instance and it clicks through game similar to a legit user, and users can actually see what's happening inside game.

Ignore source code if you're not developer and you just want to bot. In this case, you only have to:
- Download one of .zip packages, 
- extract content into any folder (recommended to extract package to a place where no administrator permissions are required, e.g. Desktop), 
- and follow instructions given in provided #instructions.txt to setup and run bot.

I did not bother with creating GUI interface, and therefore all settings are simply changed in #config.txt with text editor (e.g. wordpad, notepad). Setting up this bot might appear a bit advanced at first glance, but it's not really all that complicated. Plus, it's free to use and modify my spaghetti code. 

## All .zip packages contain:
* Java executable bot program, need Java from https://java.com/en/download/,
* Google Chrome WebDriver (included chromedriver.exe v2.38 for Windows, can use any other version for any other OS from https://sites.google.com/a/chromium.org/chromedriver/),
* few txt documents. 

## differences:
* BOT.zip --> a basic story bot
* BOT_Manual_Scroll.zip --> added "manual" mousewheel scrolling in case basic story bot (#BOT.zip) does not scroll on pages automatically 
    (custom request for one of users who reported problem with scrolling in browser)
* BOT_Aspects.zip --> a modified version of story bot which helps you level or reorganize your wanted aspects at accelerated rate
