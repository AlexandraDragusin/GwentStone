package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public final class Player {
    private final ArrayList<Card> hand;
    private int mana;
    private boolean endTurn;

    /**
     * Constructor for class Player
     */
    public Player() {
        hand = new ArrayList<>();
        endTurn = false;
        this.mana = 0;
    }

    /**
     * Add a card in player's hand
     * @param card the card that will be added in hand
     */
    public void addCardInHand(final Card card) {
        hand.add(card);
    }

    /**
     * Place a card on the table
     * @param table the table where the card will be placed
     * @param playerIdx the index of the player who will place the card
     * @param handIdx the index of the card
     * @param node the node where the output will be added
     * @return  1 if the action causes an error
     *          2 if the action is executed without error
     */
    public int placeCardOnTable(final ArrayList<ArrayList<Card>> table, final int playerIdx,
                                final int handIdx, final ObjectNode node) {
        if (hand.get(handIdx).isEnvironmentType()) {
            node.put("error", "Cannot place environment card on table.");
            node.put("handIdx", 0);
            return 1;
        }

        if (hand.get(handIdx).getMana() > this.mana) {
            node.put("error", "Not enough mana to place card on table.");
            node.put("handIdx", 0);
            return 1;
        }

        int rowNumber;

        if (playerIdx == 2 && hand.get(handIdx).isBackRow()) {
            rowNumber = PLAYER_TWO_BACK_ROW;
        } else if (playerIdx == 2 && hand.get(handIdx).isFrontRow()) {
            rowNumber = PLAYER_TWO_FRONT_ROW;
        } else if (playerIdx == 1 && hand.get(handIdx).isBackRow()) {
            rowNumber = PLAYER_ONE_BACK_ROW;
        } else {
            rowNumber = PLAYER_ONE_FRONT_ROW;
        }

        if (table.get(rowNumber).size() < TABLE_MAX_COLS) {
            table.get(rowNumber).add(hand.get(handIdx));
            setMana(getMana() - hand.get(handIdx).getMana());
            this.hand.remove(handIdx);
            node.remove("command");
            return 2;
        } else {
            node.put("error", "Cannot place card on table since row is full.");
            node.put("handIdx", 0);
            return 1;
        }
    }

    /**
     * Attack a card from the table
     * @param table the table where the cards are placed
     * @param playerIdx the index of the player who will attack with a card
     * @param cardAttacker the card that will attack
     * @param cardAttacked the card that will be attacked
     * @param node the node where the output will be added
     * @return 1 if the action causes an error
     *         2 if the action is executed without error
     */
    public int cardAttack(final ArrayList<ArrayList<Card>> table, final int playerIdx,
                          final Position cardAttacker, final Position cardAttacked,
                          final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode coordinatesNode1 = objectMapper.createObjectNode();
        coordinatesNode1.put("x", cardAttacker.getX());
        coordinatesNode1.put("y", cardAttacker.getY());
        node.put("cardAttacker", coordinatesNode1);

        ObjectNode coordinatesNode2 = objectMapper.createObjectNode();
        coordinatesNode2.put("x", cardAttacked.getX());
        coordinatesNode2.put("y", cardAttacked.getY());
        node.put("cardAttacked", coordinatesNode2);

        if ((playerIdx == 1 && (cardAttacked.getX() == PLAYER_ONE_FRONT_ROW
                || cardAttacked.getX() == PLAYER_ONE_BACK_ROW))
                || (playerIdx == 2 && (cardAttacked.getX() == PLAYER_TWO_BACK_ROW
                || cardAttacked.getX() == PLAYER_TWO_FRONT_ROW))) {
            node.put("error", "Attacked card does not belong to the enemy.");
            return 1;
        }

        if (table.get(cardAttacker.getX()).get(cardAttacker.getY()).isHasAttacked()
                || table.get(cardAttacker.getX()).get(cardAttacker.getY()).isUsedAbility()) {
            node.put("error", "Attacker card has already attacked this turn.");
            return 1;
        }

        if (table.get(cardAttacker.getX()).get(cardAttacker.getY()).isFrozen()) {
            node.put("error", "Attacker card is frozen.");
            return 1;
        }

        for (Card card : table.get(playerIdx)) {
                if (card.isTank() && !table.get(cardAttacked.getX()).get(cardAttacked.getY()).
                        isTank()) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                    return 1;
                }
            }

        int actualHealth = table.get(cardAttacked.getX()).get(cardAttacked.getY()).getHealth();
        int damage = table.get(cardAttacker.getX()).get(cardAttacker.getY()).getAttackDamage();

        table.get(cardAttacked.getX()).get(cardAttacked.getY()).setHealth(actualHealth - damage);
        table.get(cardAttacker.getX()).get(cardAttacker.getY()).setHasAttacked(true);

        if (table.get(cardAttacked.getX()).get(cardAttacked.getY()).getHealth() <= 0) {
            table.get(cardAttacked.getX()).remove(cardAttacked.getY());
        }

        return 2;
    }

    /**
     * Use ability on a card from the table
     * @param table the table where the cards are placed
     * @param playerIdx the index of the player who will use the ability of the card
     * @param cardAttacker the card that will use the ability
     * @param cardAttacked the card on which the ability will be used
     * @param node the node where the output will be added
     * @return  1 if the action causes an error
     *          2 if the action is executed without error
     */
    public int cardUsesAbility(final ArrayList<ArrayList<Card>> table, final int playerIdx,
                               final Position cardAttacker, final Position cardAttacked,
                               final ObjectNode node) {
        ObjectMapper newObjectMapper = new ObjectMapper();

        ObjectNode coordinatesNode1 = newObjectMapper.createObjectNode();
        coordinatesNode1.put("x", cardAttacker.getX());
        coordinatesNode1.put("y", cardAttacker.getY());
        node.put("cardAttacker", coordinatesNode1);

        ObjectNode coordinatesNode2 = newObjectMapper.createObjectNode();
        coordinatesNode2.put("x", cardAttacked.getX());
        coordinatesNode2.put("y", cardAttacked.getY());
        node.put("cardAttacked", coordinatesNode2);

        if (table.get(cardAttacker.getX()).get(cardAttacker.getY()).isFrozen()) {
            node.put("error", "Attacker card is frozen.");
            return 1;
        }

        if (table.get(cardAttacker.getX()).get(cardAttacker.getY()).isHasAttacked()
                || table.get(cardAttacker.getX()).get(cardAttacker.getY()).isUsedAbility()) {
            node.put("error", "Attacker card has already attacked this turn.");
            return 1;
        }

        String attackerName = table.get(cardAttacker.getX()).get(cardAttacker.getY()).getName();

        if (attackerName.equals("Disciple")) {
            if ((playerIdx == 1 && (cardAttacked.getX() == PLAYER_TWO_BACK_ROW
                    || cardAttacked.getX() == PLAYER_TWO_FRONT_ROW))
                    || (playerIdx == 2 && (cardAttacked.getX() == PLAYER_ONE_FRONT_ROW
                    || cardAttacked.getX() == PLAYER_ONE_BACK_ROW))) {
                node.put("error", "Attacked card does not belong to the current player.");
                return 1;
            }
        }

        if (attackerName.equals("The Ripper") || attackerName.equals("Miraj")
                || attackerName.equals("The Cursed One")) {
            if ((playerIdx == 1 && (cardAttacked.getX() == PLAYER_ONE_FRONT_ROW
                    || cardAttacked.getX() == PLAYER_ONE_BACK_ROW))
                    || (playerIdx == 2 && (cardAttacked.getX() == PLAYER_TWO_BACK_ROW
                    || cardAttacked.getX() == PLAYER_TWO_FRONT_ROW))) {
                node.put("error", "Attacked card does not belong to the enemy.");
                return 1;
            }

            for (Card card : table.get(playerIdx)) {
                if (card.isTank() && !table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .isTank()) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                    return 1;
                }
            }
        }

        switch (attackerName) {
            case "Disciple" -> {
                int currentHealth = table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .getHealth();
                table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .setHealth(currentHealth + 2);
            }
            case "The Ripper" -> {
                int currentAttack = table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .getAttackDamage();
                table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .setAttackDamage(Math.max(currentAttack - 2, 0));
            }
            case "Miraj" -> {
                int attackedHealth = table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .getHealth();
                int attackerHealth = table.get(cardAttacker.getX()).get(cardAttacker.getY())
                        .getHealth();
                table.get(cardAttacked.getX()).get(cardAttacked.getY()).setHealth(attackerHealth);
                table.get(cardAttacker.getX()).get(cardAttacker.getY()).setHealth(attackedHealth);
            }
            case "The Cursed One" -> {
                int health = table.get(cardAttacked.getX()).get(cardAttacked.getY()).getHealth();
                int attackDamage = table.get(cardAttacked.getX()).get(cardAttacked.getY())
                        .getAttackDamage();
                table.get(cardAttacked.getX()).get(cardAttacked.getY()).setHealth(attackDamage);
                table.get(cardAttacked.getX()).get(cardAttacked.getY()).setAttackDamage(health);
                if (attackDamage == 0) {
                    table.get(cardAttacked.getX()).remove(cardAttacked.getY());
                }
            }
            default -> {
            }
        }

        table.get(cardAttacker.getX()).get(cardAttacker.getY()).setUsedAbility(true);
        return 2;
    }

    /**
     * Attack a hero card
     * @param table the table where the cards are placed
     * @param playerIdx the index of the player who will attack
     * @param cardAttacker the card that will attack the hero
     * @param hero the hero that will be attacked
     * @param node the node where the output will pe added
     * @return 1 if the action causes an error
     *         2 if the action is executed without error
     */
    public int useAttackHero(final ArrayList<ArrayList<Card>> table, final int playerIdx,
                             final Position cardAttacker, final Card hero, final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode coordinatesNode = objectMapper.createObjectNode();
        coordinatesNode.put("x", cardAttacker.getX());
        coordinatesNode.put("y", cardAttacker.getY());
        node.put("cardAttacker", coordinatesNode);

        if (table.get(cardAttacker.getX()).get(cardAttacker.getY()).isFrozen()) {
            node.put("error", "Attacker card is frozen.");
            return 1;
        }

        if (table.get(cardAttacker.getX()).get(cardAttacker.getY()).isHasAttacked()
                || table.get(cardAttacker.getX()).get(cardAttacker.getY()).isUsedAbility()) {
            node.put("error", "Attacker card has already attacked this turn.");
            return 1;
        }

        for (Card card : table.get(playerIdx)) {
            if (card.isTank()) {
                node.put("error", "Attacked card is not of type 'Tank'.");
                return 1;
            }
        }

        int attackDamage = table.get(cardAttacker.getX()).get(cardAttacker.getY())
                .getAttackDamage();
        hero.setHealth(hero.getHealth() - attackDamage);
        table.get(cardAttacker.getX()).get(cardAttacker.getY()).setHasAttacked(true);

        if (hero.getHealth() <= 0) {
            node.remove("command");
            node.remove("x");
            node.remove("y");
            node.remove("cardAttacker");

            if (playerIdx == 1) {
                node.put("gameEnded", "Player one killed the enemy hero.");
                GamePlay.setPlayerOneWins(GamePlay.getPlayerOneWins() + 1);
            } else {
                node.put("gameEnded", "Player two killed the enemy hero.");
                GamePlay.setPlayerTwoWins(GamePlay.getPlayerTwoWins() + 1);
            }
            return 1;
        }

        return 2;
    }

    /**
     * Use ability of a hero card
     * @param table the table where the cards are placed
     * @param affectedRow the row that will be affected
     * @param playerIdx the index of the player who will use the hero's ability
     * @param hero the hero card that will use the ability
     * @param node the node where the output will be placed
     * @return 1 if the action causes an error
     *         2 if the action is executed without error
     */
    public int useHeroAbility(final ArrayList<ArrayList<Card>> table, final int affectedRow,
                              final int playerIdx, final Card hero, final ObjectNode node) {
        if (hero.getMana() > this.getMana()) {
            node.put("affectedRow", affectedRow);
            node.put("error", "Not enough mana to use hero's ability.");
            return 1;
        }

        if (hero.isHasAttacked() || hero.isUsedAbility()) {
            node.put("affectedRow", affectedRow);
            node.put("error", "Hero has already attacked this turn.");
            return 1;
        }

        if (hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) {
            if (playerIdx == 1 && (affectedRow == PLAYER_ONE_FRONT_ROW
                    || affectedRow == PLAYER_ONE_BACK_ROW)
                    || (playerIdx == 2 && (affectedRow == PLAYER_TWO_BACK_ROW
                    || affectedRow == PLAYER_TWO_FRONT_ROW))) {
                node.put("affectedRow", affectedRow);
                node.put("error", "Selected row does not belong to the enemy.");
                return 1;
            }
        }

        if (hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) {
            if (playerIdx == 1 && (affectedRow == PLAYER_TWO_BACK_ROW
                    || affectedRow == PLAYER_TWO_FRONT_ROW)
                    || (playerIdx == 2 && (affectedRow == PLAYER_ONE_FRONT_ROW
                    || affectedRow == PLAYER_ONE_BACK_ROW))) {
                node.put("affectedRow", affectedRow);
                node.put("error", "Selected row does not belong to the current player.");
                return 1;
            }
        }

        switch (hero.getName()) {
            case "Lord Royce":
                int maxAttack = 0;
                int maxIdx = 0;
                for (Card card : table.get(affectedRow)) {
                    if (card.getAttackDamage() > maxAttack) {
                        maxIdx = table.get(affectedRow).indexOf(card);
                        maxAttack = card.getAttackDamage();
                    }
                }
                table.get(affectedRow).get(maxIdx).setFrozen(true);
                break;
            case "Empress Thorina":
                int maxHealth = 0;
                maxIdx = 0;
                for (Card card : table.get(affectedRow)) {
                    if (card.getHealth() > maxHealth) {
                        maxIdx = table.get(affectedRow).indexOf(card);
                        maxHealth = card.getHealth();
                    }
                }
                table.get(affectedRow).remove(maxIdx);
                break;
            case "General Kocioraw":
                for (Card card : table.get(affectedRow)) {
                    card.setAttackDamage(card.getAttackDamage() + 1);
                }
                break;
            case "King Mudface":
                for (Card card : table.get(affectedRow)) {
                    card.setHealth(card.getHealth() + 1);
                }
                break;
            default:
        }

        this.setMana(this.getMana() - hero.getMana());
        hero.setUsedAbility(true);
        return 2;
    }

    /**
     * Use ability of an environment card
     * @param handIdx the index of the environment card
     * @param table the table where the cards are placed
     * @param affectedRow the row that will be affected
     * @param playerIdx the index of the player who will use the environment card
     * @param node the node where the output will be placed
     * @return 1 if the action causes an error
     *         2 if the action is executed without error
     */
    public int useEnvironmentCard(final int handIdx, final ArrayList<ArrayList<Card>> table,
                                  final int affectedRow, final int playerIdx,
                                  final ObjectNode node) {
        node.put("handIdx", handIdx);
        node.put("affectedRow", affectedRow);

        if (!hand.get(handIdx).isEnvironmentType()) {
            node.put("error", "Chosen card is not of type environment.");
            return 1;
        }

        if (hand.get(handIdx).getMana() > this.mana) {
            node.put("error", "Not enough mana to use environment card.");
            return 1;
        }

        if ((playerIdx == 1 && (affectedRow == PLAYER_ONE_FRONT_ROW
                || affectedRow == PLAYER_ONE_BACK_ROW)
                || (playerIdx == 2 && (affectedRow == PLAYER_TWO_BACK_ROW
                || affectedRow == PLAYER_TWO_FRONT_ROW)))) {
            node.put("error", "Chosen row does not belong to the enemy.");
            return 1;
        }

        if (hand.get(handIdx).getName().equals("Heart Hound")
                && table.get(PLAYER_ONE_BACK_ROW - affectedRow).size() == TABLE_MAX_COLS) {
            node.put("error", "Cannot steal enemy card since the player's row is full.");
            return 1;
        } else if (hand.get(handIdx).getName().equals("Firestorm")) {
            for (Card card : table.get(affectedRow)) {
                card.setHealth(card.getHealth() - 1);
            }
            table.get(affectedRow).removeIf(card -> card.getHealth() <= 0);
        } else if (hand.get(handIdx).getName().equals("Winterfell")) {
            for (Card card : table.get(affectedRow)) {
                card.setFrozen(true);
            }
        } else if (hand.get(handIdx).getName().equals("Heart Hound")) {
            int maxHealth = 0;
            int maxIdx = 0;
            for (Card card : table.get(affectedRow)) {
                if (card.getHealth() > maxHealth) {
                    maxIdx = table.get(affectedRow).indexOf(card);
                    maxHealth = card.getHealth();
                }
            }
            table.get(PLAYER_ONE_BACK_ROW - affectedRow).add(table.get(affectedRow).get(maxIdx));
            table.get(affectedRow).remove(maxIdx);
        }

        setMana(getMana() - hand.get(handIdx).getMana());
        hand.remove(handIdx);
        return 2;
    }

    /**
     * Get the environment type cards in hand
     * @return arraylist that contains the environment type cards
     */
    public ArrayList<Card> getEnvironmentCardsInHand() {
        ArrayList<Card> environmentCardsInHand = new ArrayList<>();

        for (Card card : hand) {
            if (card.isEnvironmentType()) {
                environmentCardsInHand.add(card);
            }
        }
        return environmentCardsInHand;
    }

    /**
     * Getter for endTurn member
     * @return true if the player has ended his turn and false otherwise
     */
    public boolean isEndTurn() {
        return endTurn;
    }

    /**
     * Setter for endTurn member
      * @param endTurn boolean
     */
    public void setEndTurn(final boolean endTurn) {
        this.endTurn = endTurn;
    }

    /**
     * Getter for hand member
     * @return an arraylist that contains the cards the player has in hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Getter for mana member
     * @return amount of mana the player hah
     */
    public int getMana() {
        return mana;
    }

    /**
     * Setter for mana member
     * @param mana int number
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    static final int PLAYER_ONE_FRONT_ROW = 2;
    static final int PLAYER_ONE_BACK_ROW = 3;
    static final int PLAYER_TWO_FRONT_ROW = 1;
    static final int PLAYER_TWO_BACK_ROW = 0;
    static final int TABLE_MAX_COLS = 5;
}
