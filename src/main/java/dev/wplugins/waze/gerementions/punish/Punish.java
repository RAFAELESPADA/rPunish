package dev.wplugins.waze.gerementions.punish;

import dev.wplugins.waze.gerementions.enums.reason.Reason;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Punish {


    @Setter
    private String id;
    private Integer idpenis;
    private String ip;
    private final String playerName, stafferName, proof, type;
    private final long date, expire;
    private final Reason reason;

    public boolean isLocked() {
        if (expire != 0) {
            return (System.currentTimeMillis() < expire);
        }
        return true;
    }

    public void setIdpenis(Integer newa) {
        idpenis = newa;
    }
}
