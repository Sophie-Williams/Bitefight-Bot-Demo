# Bitefight-Bot-Demo
An automated remote control program (so-called bot) for a popular browser game Bitefight, written in Java combined with Selenium library for automating tests. So far, only story mode and leveling aspects are supported. 

Unlike other bots that act as browser plugins to control your character, this bot opens new Google Chrome instance and it clicks through game just like a legit user, and users can actually see what's going on in game.

Users only need to download any of .zip files and follow instructions given in provided <instructions.txt> to configure and run bot.
I did not bother with creating GUI interface, and therefore all settings are simply changed in <config.txt> with text editor (e.g. wordpad, notepad) instead. Setting it up might appear a bit advanced at first glance, but it's not really all that complicated. Plus, it's free to use. 
## .zip packages contain:
* Java executable bot program
* Google Chrome WebDriver (chromedriver.exe v2.35 for Windows, can use any other version for any other OS) 
* few txt documents. 

## differences:
* BOT_Chrome.zip --> basic bot
* BOT_Chrome_Scrolling.zip --> added "manual" mousewheel scrolling in case the basic bot does not scroll on pages automatically
* BOT_Chrome_Aspects.zip --> helps you level or reorganize your wanted aspects
