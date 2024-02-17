package dev.wplugins.waze.gerementions.enums.reason;

import lombok.Getter;

@Getter
public enum ReasonsPlayer {

    CHEATING("Hack"),
    ANT_CHAT("Anti-chat"),
    ANT_GAME("Anti-jogo"),
    INAPROPRIATE_SKIN("Skin inapropriada"),
    OFENSA("Ofensa a Jogador"),
    ABUSE_BUGS("Abuso de bugs");

    private final String text;

    ReasonsPlayer(String text) {
        this.text = text;
    }

}
