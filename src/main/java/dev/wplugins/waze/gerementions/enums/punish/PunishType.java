package dev.wplugins.waze.gerementions.enums.punish;

import lombok.Getter;

public enum PunishType {

    BAN("Banimento permanente"),
    TEMPBAN("Banimento temporário"),
    MUTE("Mute permanente"),
    TEMPMUTE("Mute temporário");

    @Getter
    private final String text;

    PunishType(String text){
        this.text = text;
    }

}
