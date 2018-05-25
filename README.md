# Bitefight-Bot-Demo
Bitefight bot to automate the never-ending story mode, written in Java and combined with Selenium library for automating web tests. 
It opens a new Google Chrome browser instance and it clicks through pages similar to a legit user.

Ignore source code if you're not developer and you just want to use bot.
- Download 'bot_v1.03b.zip' package, 
- extract content into any folder (recommended to extract package to a place where no administrator permissions are required, e.g. Desktop), 
- and follow instructions given in 'instructions.txt' to setup and run bot.

I did not bother with creating graphical user interface, and therefore all settings are simply changed in 'config.txt' with text editor (e.g. wordpad, notepad). Setting up bot might appear a bit advanced at first glance, but it's not really all that complicated. Plus, it's free to use it and modify my spaghetti code.

I removed 'bot_manual_scrolling' package since it's redundant to original bot package, and I also removed 'bot_aspects' package for leveling aspects, since it appears to do more harm than good. If you still hold a copy of bot_aspects package, abandon it.

## Current version:
* 25.5.2018 --> v1.03b --> bot now detects EU cookies compliance message (introduced on 23.5.2018)
                       --> fixed randomness (now bot follows user's order of story actions instead of randomly choosing actions)
                       --> removed bot with additional mouse scrolling (it was redundant to original bot)
                       --> removed bot for raising aspects as it seems to cause more harm than good
