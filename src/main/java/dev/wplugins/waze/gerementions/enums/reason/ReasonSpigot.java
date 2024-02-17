package dev.wplugins.waze.gerementions.enums.reason;


import dev.wplugins.waze.gerementions.BukkitMain;
import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.enums.punish.PunishType;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum ReasonSpigot {

    AMEACA("Sincronização de Punição", PunishType.TEMPBAN, 5);
    private final String text;
    private final PunishType punishType;
    private final long time;

    ReasonSpigot(String text, PunishType punishType, long time) {
        this.text = text;
        this.punishType = punishType;
        this.time = time;
    }
}

