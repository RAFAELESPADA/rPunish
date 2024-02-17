package dev.wplugins.waze.gerementions.enums.punish;

import lombok.Getter;

public enum RevogarType {

    ERRADA("Aplicada ao jogador incorreto"),
    INCORRETO("Motivo de punição incorreto"),
    PROVA("Prova incorreta"),
    APLICADA("Punição aplicada injustamente"),
    ACEITA("Revisão aceita");

    @Getter
    private final String text;

    RevogarType(String text){
        this.text = text;
    }

}

