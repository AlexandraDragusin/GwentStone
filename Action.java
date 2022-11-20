package main;

import fileio.ActionsInput;

public final class Action {
    private String command;
    private int handIdx;
    private Position cardAttacker = null;
    private Position cardAttacked = null;
    private int affectedRow;
    private int playerIdx;
    private int x;
    private int y;

    /**
     * Constructor for Action class
     * @param action contains the action information extracted from input
     */
    public Action(final ActionsInput action) {
        this.command = action.getCommand();
        this.handIdx = action.getHandIdx();

        if (action.getCardAttacker() != null) {
            this.cardAttacker = new Position(action.getCardAttacker());
        }

        if (action.getCardAttacked() != null) {
            this.cardAttacked = new Position(action.getCardAttacked());
        }

        this.affectedRow = action.getAffectedRow();
        this.playerIdx = action.getPlayerIdx();
        this.x = action.getX();
        this.y = action.getY();
    }

    /**
     * Getter for cardAttacker member
     * @return the position (coordinates) of the attacker card
     */
    public Position getCardAttacker() {
        return cardAttacker;
    }

    /**
     * Setter for cardAttacker member
     * @param cardAttacker position (coordinates) of the attacker card
     */
    public void setCardAttacker(final Position cardAttacker) {
        this.cardAttacker = cardAttacker;
    }

    /**
     * Getter for cardAttacked member
     * @return the position (coordinates) of the attacked card
     */
    public Position getCardAttacked() {
        return cardAttacked;
    }

    /**
     * Setter for cardAttacked member
     * @param cardAttacked position (coordinates) of the attacked card
     */
    public void setCardAttacked(final Position cardAttacked) {
        this.cardAttacked = cardAttacked;
    }

    /**
     * Getter for command member
     * @return string that contains a command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Setter for command member
     * @param command string that contains a command
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Getter for handIdx member
     * @return the index of hand
     */
    public int getHandIdx() {
        return handIdx;
    }

    /**
     * Setter for handIdx member
     * @param handIdx the index of hand
     */
    public void setHandIdx(final int handIdx) {
        this.handIdx = handIdx;
    }

    /**
     * Getter for affectedRow member
     * @return the index of row that is affected
     */
    public int getAffectedRow() {
        return affectedRow;
    }

    /**
     * Setter for affectedRow member
     * @param affectedRow index of row that is affected
     */
    public void setAffectedRow(final int affectedRow) {
        this.affectedRow = affectedRow;
    }

    /**
     * Getter for playerIdx member
     * @return the index of the player
     */
    public int getPlayerIdx() {
        return playerIdx;
    }

    /**
     * Setter for playerIdx member
     * @param playerIdx the index of the player
     */
    public void setPlayerIdx(final int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "ActionsInput{"
                + "command='"
                + command + '\''
                + ", handIdx="
                + handIdx
                + ", cardAttacker="
                + cardAttacker
                + ", cardAttacked="
                + cardAttacked
                + ", affectedRow="
                + affectedRow
                + ", playerIdx="
                + playerIdx
                + ", x="
                + x
                + ", y="
                + y
                + '}';
    }
}
