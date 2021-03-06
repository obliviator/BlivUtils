BlivUtils
==================

BlivUtils is a plugin made for AusCraft (aus-craft.net) which adds simple menus and helpful commands
which were made to improve and ease the Admin/Playerbase.

###Commands:

##Operator Commands:

**/bu**
* Usage:
    * /bu version
        * Shows the version of BlivUtils that is running
    * /bu reload
        * Reloads a few things (mainly the rank checker)

**/say**
* Usage:
    * /say <message>
    * /say I hate you all
        * Prints the message put after /say

**/wstop**
* Usage:
    * /wstop
        * No Parameters
        * Prints a message in chat (fancy and nice), then stops the server after 10 seconds.

**/promoadmin**
* Usage:
    * /promoadmin <player> <#> <day(s)/month(s)>
        * Promotes a player to the Admin rank for the specified time.

**/updatetime**
* Usage:
    * /updatetime <player> <rank> <#> <day(s)/month(s)>
        * Adds/Removes time from an player's rank for the specified time.

##Admin Commands:

**/prefix** (this will change later)
* Usage:
    * /prefix <prefix>
        * For Admin (or anyone else with the blivutils.prefix permission) and up to change their prefix.
        
##Player Commands:

**/rank**
* Usage:
    * /rank
        * Prints the list of all ranks
    * /rank <rank>
    * /rank EnderDragon
        * Prints a simple explanation of EnderDragon rank, and a link to the wiki page.

**/buyrank**
* Usage:
    * /buyrank
        * Opens the rank purchasing menu (Nether and Ender Ranks)
    * /buyrank <rank
    * /buyrank MagmaSlime
        * Sends confirmation to player that they want to buy the rank.

**/promoteme**
* Usage:
    * /promoteme
        * No Parameters.
        * Confirmation command that deducts money and promotes user to the rank they chose.



**/timeleft**
* Usage:
    * /timeleft
        * Checks the timeleft on the highest priority rank that the user has.
    * /timeleft <name>
        * Checks the timeleft on the highest priority rank that the chosen player has.


**/chat**
* Usage:
    * /chat
        * Prints the possible colours that the player can choose from.
    * /chat <colour>
    * /chat red
        * Sends confirmation to the player that they want to buy the chat colour.

**/colourme**
* Usage:
    * /colourme
        * Confirmation command that deducts money and adds chat colour that they chose.
