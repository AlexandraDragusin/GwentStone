package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class GamePlay {
    private ArrayList<ArrayList<Card>> playerOneDecks;
    private ArrayList<ArrayList<Card>> playerTwoDecks;
    private ArrayList<Game> games = new ArrayList<>();
    private ArrayList<ArrayList<Card>> table;
    private Player playerOne;
    private Player playerTwo;
    private Player currentPlayer;
    private int currentPlayerIndex;
    private int numberOfRounds;
    private int playerOneDeckIdx;
    private int playerTwoDeckIdx;
    private Card playerOneHero;
    private Card playerTwoHero;

    private static int playerOneWins;
    private static int playerTwoWins;
    private static int gamesPlayed;

    /**
     * Constructor for GamePlay class
     * @param input contains the information extracted from input
     */
    public GamePlay(final Input input) {
        for (int i = 0; i < input.getGames().size(); i++) {
            Game newGame = new Game(input.getGames().get(i));
            games.add(newGame);
        }

        playerOneWins = 0;
        playerTwoWins = 0;
        gamesPlayed = 0;
    }

    /**
     * Start a new game: initialize the two players, theirs decks, the table
     * extract the deck for every player, shuffle it and iterate the actions.
     * @param game information about the game
     * @param output the array where the output of the actions will be placed
     * @param input contains the information extracted from input
     */
    public void play(final Game game, final ArrayNode output, final Input input) {
        gamesPlayed++;
        numberOfRounds = 1;

        playerOne = new Player();
        playerTwo = new Player();

        playerOneDecks = new ArrayList<>();
        playerTwoDecks = new ArrayList<>();

        for (int i = 0; i < input.getPlayerOneDecks().getNrDecks(); i++) {
            ArrayList<Card> deckOne = new ArrayList<>();
            playerOneDecks.add(deckOne);

            for (int j = 0; j < input.getPlayerOneDecks().getNrCardsInDeck(); j++) {
                Card cardOne = new Card(input.getPlayerOneDecks().getDecks().get(i).get(j));
                deckOne.add(cardOne);
            }
        }

        for (int i = 0; i < input.getPlayerTwoDecks().getNrDecks(); i++) {
            ArrayList<Card> deckTwo = new ArrayList<>();
            playerTwoDecks.add(deckTwo);

            for (int j = 0; j < input.getPlayerOneDecks().getNrCardsInDeck(); j++) {
                Card cardTwo = new Card(input.getPlayerTwoDecks().getDecks().get(i).get(j));
                deckTwo.add(cardTwo);
            }
        }

        this.table = new ArrayList<>();
        for (int i = 0; i < TABLE_MAX_COLS; i++) {
            table.add(new ArrayList<>());
        }

        this.playerOneDeckIdx = game.getPlayerOneDeckIdx();
        this.playerTwoDeckIdx = game.getPlayerTwoDeckIdx();

        this.playerOneHero = game.getPlayerOneHero();
        this.playerTwoHero = game.getPlayerTwoHero();

        playerOne.setMana(playerOne.getMana() + numberOfRounds);
        playerTwo.setMana(playerTwo.getMana() + numberOfRounds);

        ArrayList<Card> playerOneDeck = playerOneDecks.get(playerOneDeckIdx);
        ArrayList<Card> playerTwoDeck = playerTwoDecks.get(playerTwoDeckIdx);

        Collections.shuffle(playerOneDeck, new Random(game.getShuffleSeed()));
        Collections.shuffle(playerTwoDeck, new Random(game.getShuffleSeed()));

        playerOne.addCardInHand(playerOneDeck.get(0));
        playerOneDeck.remove(0);

        playerTwo.addCardInHand(playerTwoDeck.get(0));
        playerTwoDeck.remove(0);

        currentPlayerIndex = game.getStartingPlayer();
        setCurrentPlayer(game.getStartingPlayer());

        for (Action action : game.getActions()) {
            if (action.getCommand().equals("endPlayerTurn")) {
                changeTurn(currentPlayerIndex);
                setCurrentPlayer(currentPlayerIndex);
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode node = addActionToNode(objectMapper, action);
                if (node != null) {
                    output.add(node);
                }
            }
        }
    }

    /**
     * Add the specific output of every action in the object node
     * @param objectMapper object mapper
     * @param action the action to be performed
     * @return an object node that contains the output of the action
     */
    public ObjectNode addActionToNode(final ObjectMapper objectMapper, final Action action) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());

        if (action.getPlayerIdx() != 0) {
            node.put("playerIdx", action.getPlayerIdx());
        }

        ObjectMapper newObjectMapper = new ObjectMapper();

        switch (action.getCommand()) {
            case "getCardsInHand":
                ArrayNode arrayNode;
                if (action.getPlayerIdx() == 1) {
                    arrayNode = createListNode(newObjectMapper, playerOne.getHand());
                } else {
                    arrayNode = createListNode(newObjectMapper, playerTwo.getHand());
                }
                node.put("output", arrayNode);
                break;
            case "getPlayerDeck":
                if (action.getPlayerIdx() == 1) {
                    arrayNode = createListNode(newObjectMapper, playerOneDecks
                            .get(playerOneDeckIdx));
                } else {
                    arrayNode = createListNode(newObjectMapper, playerTwoDecks
                            .get(playerTwoDeckIdx));
                }
                node.put("output", arrayNode);
                break;
            case "getCardsOnTable":
                arrayNode = newObjectMapper.createArrayNode();
                ArrayNode newArrayNode;
                for (int i = 0; i < TABLE_MAX_ROWS; i++) {
                    newArrayNode = createListNode(newObjectMapper, table.get(i));
                    arrayNode.add(newArrayNode);
                }
                node.put("output", arrayNode);
                break;
            case "getPlayerTurn":
                node.put("output", currentPlayerIndex);
                break;
            case "getPlayerHero":
                ObjectNode cardNode;
                if (action.getPlayerIdx() == 1) {
                    cardNode = createCardNode(newObjectMapper, playerOneHero);
                } else {
                    cardNode = createCardNode(newObjectMapper, playerTwoHero);
                }
                node.put("output", cardNode);
                break;
            case "getCardAtPosition":
                node.put("x", action.getX());
                node.put("y", action.getY());
                if (table.get(action.getX()).size() > action.getY()) {
                    newObjectMapper = new ObjectMapper();
                    cardNode = createCardNode(newObjectMapper, table.get(action.getX())
                            .get(action.getY()));
                    node.put("output", cardNode);
                } else {
                    node.put("output", "No card available at that position.");
                }
                break;
            case "getPlayerMana":
                if (action.getPlayerIdx() == 1) {
                    node.put("output", playerOne.getMana());
                } else {
                    node.put("output", playerTwo.getMana());
                }
                break;
            case "getEnvironmentCardsInHand":
                if (action.getPlayerIdx() == 1) {
                    arrayNode = createListNode(newObjectMapper, playerOne
                            .getEnvironmentCardsInHand());
                } else {
                    arrayNode = createListNode(newObjectMapper, playerTwo
                            .getEnvironmentCardsInHand());
                }
                node.put("output", arrayNode);
                break;
            case "getFrozenCardsOnTable":
                arrayNode = createListNode(newObjectMapper, getFrozenCardsOnTable());
                node.put("output", arrayNode);
                break;
            case "placeCard":
                if (currentPlayer.placeCardOnTable(table, currentPlayerIndex,
                        action.getHandIdx(), node) == 2) {
                    return null;
                }
                break;
            case "useEnvironmentCard":
                if (currentPlayer.useEnvironmentCard(action.getHandIdx(), table,
                        action.getAffectedRow(), currentPlayerIndex, node) == 2) {
                    return null;
                }
                break;
            case "cardUsesAttack":
                if (currentPlayer.cardAttack(table, currentPlayerIndex,
                        action.getCardAttacker(), action.getCardAttacked(), node) == 2) {
                    return null;
                }
                break;
            case "cardUsesAbility":
                if (currentPlayer.cardUsesAbility(table, currentPlayerIndex,
                        action.getCardAttacker(), action.getCardAttacked(), node) == 2) {
                    return null;
                }
                break;
            case "useAttackHero":
                Card attackedHero;

                if (currentPlayerIndex == 1) {
                    attackedHero = playerTwoHero;
                } else {
                    attackedHero = playerOneHero;
                }

                if (currentPlayer.useAttackHero(table, currentPlayerIndex,
                        action.getCardAttacker(), attackedHero, node) == 2) {
                    return null;
                }
                break;
            case "useHeroAbility":
                Card hero;
                if (currentPlayerIndex == 1) {
                    hero = playerOneHero;
                } else {
                    hero = playerTwoHero;
                }

                if (currentPlayer.useHeroAbility(table, action.getAffectedRow(),
                        currentPlayerIndex, hero, node) == 2) {
                    return null;
                }
                break;
            case "getTotalGamesPlayed":
                node.put("output", gamesPlayed);
                break;
            case "getPlayerOneWins":
                node.put("output", playerOneWins);
                break;
            case "getPlayerTwoWins":
                node.put("output", playerTwoWins);
                break;
            default :
                return null;
        }

        return node;
    }

    /**
     * Create an array node that contains cards
     * @param objectMapper object mapper
     * @param cards the list of cards that want to be added to the array node
     * @return an array node that contains cards
     */
    public ArrayNode createListNode(final ObjectMapper objectMapper, final ArrayList<Card> cards) {
        ArrayNode listNode = objectMapper.createArrayNode();

        for (Card card : cards) {
            ObjectNode cardNode = createCardNode(objectMapper, card);
            listNode.add(cardNode);
        }

        return listNode;
    }

    /**
     * Create an object node that contains information about a card
     * @param objectMapper object mapper
     * @param card the card that want to be added to the object node
     * @return an object node
     */
    public ObjectNode createCardNode(final ObjectMapper objectMapper, final Card card) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("mana", card.getMana());
        if (!card.isEnvironmentType()) {
            if (!card.isHeroType()) {
                cardNode.put("attackDamage", card.getAttackDamage());
            }

            cardNode.put("health", card.getHealth());
        }
        cardNode.put("description", card.getDescription());

        ArrayNode colors = cardNode.putArray("colors");
        for (String color : card.getColors()) {
            colors.add(color);
        }

        cardNode.put("name", card.getName());
        return cardNode;
    }

    /**
     * Sets the current player
     * @param currentPlayerIndex the index of the current player
     */
    public void setCurrentPlayer(final int currentPlayerIndex) {
        if (currentPlayerIndex == 1) {
            this.currentPlayer = playerOne;
        } else {
            this.currentPlayer = playerTwo;
        }
    }

    /**
     * Change the turn
     * @param currentPlayerIndex the index of tue current player
     */
    public void changeTurn(final int currentPlayerIndex) {
        if (currentPlayerIndex == 1) {
            playerOne.setEndTurn(true);
            this.currentPlayerIndex = 2;

            for (int i = 2; i < TABLE_MAX_ROWS; i++) {
                for (Card card : table.get(i)) {
                    card.setFrozen(false);
                }
            }
        } else {
            playerTwo.setEndTurn(true);
            this.currentPlayerIndex = 1;

            for (int i = 0; i < 2; i++) {
                for (Card card : table.get(i)) {
                        card.setFrozen(false);
                }
            }
        }

        if (playerOne.isEndTurn() && playerTwo.isEndTurn()) {
            playerOne.setEndTurn(false);
            playerTwo.setEndTurn(false);

            if (numberOfRounds < MAX_ROUNDS) {
                numberOfRounds++;
            }

            playerOne.setMana(playerOne.getMana() + numberOfRounds);
            playerTwo.setMana(playerTwo.getMana() + numberOfRounds);

            Card card;
            if (playerOneDecks.get(playerOneDeckIdx).size() > 0) {
                card = playerOneDecks.get(playerOneDeckIdx).get(0);
                playerOne.addCardInHand(card);
                playerOneDecks.get(playerOneDeckIdx).remove(0);
            }

            playerOneHero.setHasAttacked(false);
            playerOneHero.setUsedAbility(false);

            if (playerTwoDecks.get(playerTwoDeckIdx).size() > 0) {
                card = playerTwoDecks.get(playerTwoDeckIdx).get(0);
                playerTwo.addCardInHand(card);
                playerTwoDecks.get(playerTwoDeckIdx).remove(0);
            }

            playerTwoHero.setHasAttacked(false);
            playerTwoHero.setUsedAbility(false);
        }

        for (int i = 0; i < TABLE_MAX_ROWS; i++) {
            for (Card card : table.get(i)) {
                card.setHasAttacked(false);
                card.setUsedAbility(false);
            }
        }
    }

    /**
     * Get the frozen cards that are on the table
     * @return an array list of frozen cards
     */
    public ArrayList<Card> getFrozenCardsOnTable() {
        ArrayList<Card> frozenCards = new ArrayList<>();

        for (int i = 0; i < TABLE_MAX_ROWS; i++) {
            for (Card card : table.get(i)) {
                if (card.isFrozen()) {
                    frozenCards.add(card);
                }
            }
        }

        return frozenCards;
    }

    /**
     * Get the player one decks
     * @return an array list of array list that contains player one decks
     */
    public ArrayList<ArrayList<Card>> getPlayerOneDecks() {
        return playerOneDecks;
    }

    /**
     * Set the player one decks
     * @param playerOneDecks array list of array list of cards
     */
    public void setPlayerOneDecks(final ArrayList<ArrayList<Card>> playerOneDecks) {
        this.playerOneDecks = playerOneDecks;
    }

    /**
     * Get the player two decks
     * @return an array list of array list that contains player two decks
     */
    public ArrayList<ArrayList<Card>> getPlayerTwoDecks() {
        return playerTwoDecks;
    }

    /**
     * Set the player two decks
     * @param playerTwoDecks array list of array list of cards
     */
    public void setPlayerTwoDecks(final ArrayList<ArrayList<Card>> playerTwoDecks) {
        this.playerTwoDecks = playerTwoDecks;
    }

    /**
     * Getter for games member
     * @return the games
     */
    public ArrayList<Game> getGames() {
        return games;
    }

    /**
     * Setter for games member
     * @param games array list of games
     */
    public void setGames(final ArrayList<Game> games) {
        this.games = games;
    }

    /**
     * Getter for playerOneWins member
     * @return the number of wins the player one has
     */
    public static int getPlayerOneWins() {
        return playerOneWins;
    }

    /**
     * Setter for playerOneWins member
     * @param playerOneWins int number
     */
    public static void setPlayerOneWins(final int playerOneWins) {
        GamePlay.playerOneWins = playerOneWins;
    }

    /**
     * Getter for playerTwoWins member
     * @return the number of wins the player two has
     */
    public static int getPlayerTwoWins() {
        return playerTwoWins;
    }

    /**
     * Setter for playerTwoWins member
     * @param playerTwoWins int number
     */
    public static void setPlayerTwoWins(final int playerTwoWins) {
        GamePlay.playerTwoWins = playerTwoWins;
    }

    static final int TABLE_MAX_COLS = 5;
    static final int  TABLE_MAX_ROWS = 4;
    static final int MAX_ROUNDS = 10;
}
