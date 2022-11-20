package main;

import fileio.GameInput;

import java.util.ArrayList;

public final class Game {
    private ArrayList<Action> actions = new ArrayList<>();
    private int playerOneDeckIdx;
    private int playerTwoDeckIdx;
    private int shuffleSeed;
    private Card playerOneHero;
    private Card playerTwoHero;
    private int startingPlayer;

    /**
     * Constructor for Game class
     * @param game contains the game information extracted from input
     */
    public Game(final GameInput game) {
        this.playerOneDeckIdx = game.getStartGame().getPlayerOneDeckIdx();
        this.playerTwoDeckIdx = game.getStartGame().getPlayerTwoDeckIdx();
        this.shuffleSeed = game.getStartGame().getShuffleSeed();
        this.playerOneHero = new Card(game.getStartGame().getPlayerOneHero());
        this.playerTwoHero = new Card(game.getStartGame().getPlayerTwoHero());

        for (int i = 0; i < game.getActions().size(); i++) {
            Action action = new Action(game.getActions().get(i));
            actions.add(action);
        }
        this.startingPlayer = game.getStartGame().getStartingPlayer();
    }

    /**
     * Getter for actions member
     * @return an arraylist of action
     */
    public ArrayList<Action> getActions() {
        return actions;
    }

    /**
     * Setter for actions member
     * @param actions arraylist of action
     */
    public void setActions(final ArrayList<Action> actions) {
        this.actions = actions;
    }

    /**
     * Getter for playerOneDeckIdx member
     * @return the index of the player one deck
     */
    public int getPlayerOneDeckIdx() {
        return playerOneDeckIdx;
    }

    /**
     * Setter for playerOneDeckIdx member
     * @param playerOneDeckIdx int number
     */
    public void setPlayerOneDeckIdx(final int playerOneDeckIdx) {
        this.playerOneDeckIdx = playerOneDeckIdx;
    }

    /**
     * Getter for playerTwoIdx member
     * @return the index of the player two deck
     */
    public int getPlayerTwoDeckIdx() {
        return playerTwoDeckIdx;
    }

    /**
     * Setter for playerTwoDeckIdx
     * @param playerTwoDeckIdx int number
     */
    public void setPlayerTwoDeckIdx(final int playerTwoDeckIdx) {
        this.playerTwoDeckIdx = playerTwoDeckIdx;
    }

    /**
     * Getter for the shuffleSeed member
     * @return the value of the shuffle seed
     */
    public int getShuffleSeed() {
        return shuffleSeed;
    }

    /**
     * Setter for the shuffleSeed member
     * @param shuffleSeed int number
     */
    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }

    /**
     * Getter for playerOneHero member
     * @return the hero type card of the player one
     */
    public Card getPlayerOneHero() {
        return playerOneHero;
    }

    /**
     * Setter for playerOneHero
     * @param playerOneHero card
     */
    public void setPlayerOneHero(final Card playerOneHero) {
        this.playerOneHero = playerOneHero;
    }

    /**
     * Getter for playerTwoHero member
     * @return the hero type card of the player two
     */
    public Card getPlayerTwoHero() {
        return playerTwoHero;
    }

    /**
     * Setter for playerTwoHero
     * @param playerTwoHero card
     */
    public void setPlayerTwoHero(final Card playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }

    /**
     * Getter for startingPlayer member
     * @return the number of the player that starts the game
     */
    public int getStartingPlayer() {
        return startingPlayer;
    }

    /**
     * Setter for startingPlayer member
     * @param startingPlayer int number
     */
    public void setStartingPlayer(final int startingPlayer) {
        this.startingPlayer = startingPlayer;
    }

    @Override
    public String toString() {
        return "GameInput{"
                + "startGame="
                + "StartGameInput{"
                + "playerOneDeckIdx="
                + playerOneDeckIdx
                + ", playerTwoDeckIdx="
                + playerTwoDeckIdx
                + ", shuffleSeed="
                + shuffleSeed
                + ", playerOneHero="
                + playerOneHero
                + ", playerTwoHero="
                + playerTwoHero
                + ", startingPlayer="
                + startingPlayer
                + '}'
                + ", actions="
                + actions
                + '}';
    }
}

