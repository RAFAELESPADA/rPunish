package dev.wplugins.waze.gerementions.punish.service;


import dev.wplugins.waze.gerementions.enums.reason.Reason;
import dev.wplugins.waze.gerementions.enums.reason.ReasonSpigot;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PunishSpigot {

    @Setter
    private String id;
    private final String playerName, stafferName, proof, type;
    private final long date, expire;
    private final ReasonSpigot reason;

    public boolean isLocked() {
        if (expire != 0) {
            return (System.currentTimeMillis() < expire);
        }
        return true;
    }
}
