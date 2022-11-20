package main;

import fileio.CardInput;

import java.util.ArrayList;

public final class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean isFrozen;
    private boolean isEnvironmentType;
    private boolean isHeroType;
    private boolean isBackRow;
    private boolean isFrontRow;
    private boolean isTank;
    private boolean hasAttacked;
    private boolean usedAbility;

    /**
     * Constructor for class Card
     * @param card contains the card information extracted from input
     */
    public Card(final CardInput card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
        this.isEnvironmentType = false;
        this.isHeroType = false;
        this.isBackRow = false;
        this.isFrontRow = false;
        this.isTank = false;
        this.hasAttacked = false;
        this.usedAbility = false;

        if (card.getName().equals("Lord Royce") || card.getName().equals("Empress Thorina")
                || card.getName().equals("King Mudface")
                || card.getName().equals("General Kocioraw")) {

            setHeroType(true);
            setHeroCard();
        }

        if (card.getName().equals("Firestorm") || card.getName().equals("Winterfell")
                || card.getName().equals("Heart Hound")) {
            setEnvironmentType(true);
            setEnvironmentCard();
        }

        if (card.getName().equals("Sentinel") || card.getName().equals("Berserker")) {
            setBackRow(true);
        }

        if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
            setFrontRow(true);
            setTank(true);
        }

        if (card.getName().equals("The Cursed One") || card.getName().equals("Disciple")) {
            setAttackDamage(0);
            setBackRow(true);
        }

        if (card.getName().equals("The Ripper") || card.getName().equals("Miraj")) {
            setFrontRow(true);
        }
    }

    /**
     * Getter for hasAttacked member
     * @return true if the card has attacked and false otherwise
     */
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    /**
     * Setter for hasAttacked member
     * @param hasAttacked boolean
     */
    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /**
     * Getter for usedAbility member
     * @return true if the card has used its ability and false otherwise
     */
    public boolean isUsedAbility() {
        return usedAbility;
    }

    /**
     * Setter for usedAbility member
     * @param usedAbility boolean
     */
    public void setUsedAbility(final boolean usedAbility) {
        this.usedAbility = usedAbility;
    }

    /**
     * Getter for isTank member
     * @return true if the card is tank type and false otherwise
     */
    public boolean isTank() {
        return isTank;
    }

    /**
     * Setter for isTank member
     * @param tank boolean
     */
    public void setTank(final boolean tank) {
        isTank = tank;
    }

    /**
     * Getter for isBackRow member
     * @return true if the card can be placed on the back row and false otherwise
     */
    public boolean isBackRow() {
        return isBackRow;
    }

    /**
     * Setter for isBackRow member
     * @param backRow boolean
     */
    public void setBackRow(final boolean backRow) {
        isBackRow = backRow;
    }

    /**
     * Getter for isFrontRow member
     * @return true if the card can be placed on the front row and false otherwise
     */
    public boolean isFrontRow() {
        return isFrontRow;
    }

    /**
     * Setter for frontRow member
     * @param frontRow boolean
     */
    public void setFrontRow(final boolean frontRow) {
        isFrontRow = frontRow;
    }

    /**
     * Sets details for a hero type card
     */
    public void setHeroCard() {
        setHealth(HERO_INITIAL_HEALTH);
        setAttackDamage(0);
        setFrozen(false);
    }

    /**
     * Sets details for an environment type card
     */
    public void setEnvironmentCard() {
        setHealth(0);
        setAttackDamage(0);
        setFrozen(false);
    }

    /**
     * Setter for isEnvironmentType member
     * @param environmentType boolean
     */
    public void setEnvironmentType(final boolean environmentType) {
        isEnvironmentType = environmentType;
    }

    /**
     * Setter for isHeroType member
     * @param heroType boolean
     */
    public void setHeroType(final boolean heroType) {
        isHeroType = heroType;
    }

    /**
     * Getter for isEnvironmentType member
     * @return true if the card is environment type and false otherwise
     */
    public boolean isEnvironmentType() {
        return isEnvironmentType;
    }

    /**
     * Getter for isHeroType member
     * @return true if the card is hero type and false otherwise
     */
    public boolean isHeroType() {
        return isHeroType;
    }

    /**
     * Getter for isFrozen member
     * @return true if the card is frozen and false otherwise
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Setter for isFrozen member
     * @param frozen boolean
     */
    public void setFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    /**
     * Getter for mana member
     * @return amount of mana the card has
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

    /**
     * Getter for attackDamage member
     * @return value of damage the card has
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * Setter for attackDamage member
     * @param attackDamage int number
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * Getter for health member
     * @return amount of health the card has
     */
    public int getHealth() {
        return health;
    }

    /**
     * Setter for health member
     * @param health int number
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * Getter for description member
     * @return description of the card
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description member
     * @param description string
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Getter for colors member
     * @return the colors the card has
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * Setter for colors member
     * @param colors arraylist of strings
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    /**
     * Getter for name member
     * @return the name of the card
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name member
     * @param name string
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CardInput{"
                + "mana="
                + mana
                + ", attackDamage="
                + attackDamage
                + ", health="
                + health
                + ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                + ""
                + name
                + '\''
                + '}';
    }

    static final int HERO_INITIAL_HEALTH = 30;
}
