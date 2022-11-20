Copyright 2022 Dragusin Daniela-Alexandra (321 CA 2022/2023)

# POO Homework  - GwentStone

## General description
A card game was implemented, played by two players.
Each of the players enters the game with a specified deck of cards and
a hero that will represent him. At each new round, a card is taken from
the deck into the hand. Cards in a player's hand can be either minion or
environment. Environment cards represents effects that can be applied
to the playing field. The minion cards from the player's hand are placed
on the game table. They can interact with each other, but they can also
attack the heroes. The game ends when a hero dies.

Each player has their own metric that records how many games they have
won and how many times they have played a game.

## Implementation
* GamePlay class - handles the initialization of the list of games and
their execution. In addition, it deals with adding the output specific
to each action in the output array node.
* Player class - deals with actions related to players, such as adding
a card to their hand, placing a card on the board. It also deals with the
use of cards for attack or for their special effects.
