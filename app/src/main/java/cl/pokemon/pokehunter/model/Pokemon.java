package cl.pokemon.pokehunter.model;

import java.io.Serializable;

public class Pokemon implements Serializable {
    private int id;
    private String name;
    private String spriteUrl;
    private String cryUrl;
    private int hp;
    private int attack;
    private int defense;
    private boolean captured;

    public Pokemon() {}

    public Pokemon(int id, String name, String spriteUrl, String cryUrl, int hp, int attack, int defense) {
        this.id = id;
        this.name = name;
        this.spriteUrl = spriteUrl;
        this.cryUrl = cryUrl;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.captured = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpriteUrl() { return spriteUrl; }
    public String getCryUrl() { return cryUrl; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public boolean isCaptured() { return captured; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpriteUrl(String spriteUrl) { this.spriteUrl = spriteUrl; }
    public void setCryUrl(String cryUrl) { this.cryUrl = cryUrl; }
    public void setHp(int hp) { this.hp = hp; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setCaptured(boolean captured) { this.captured = captured; }

    public String getFormattedName() {
        if (name == null) return "";
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
