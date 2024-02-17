package dev.wplugins.waze.gerementions.enums.reason;

import dev.wplugins.waze.gerementions.enums.punish.RevogarType;
import lombok.Getter;

@Getter
public enum ReasonRevogar {
    AJI("Aplicada ao jogador incorreto", RevogarType.ERRADA),
    MPI("Motivo de punição incorreto", RevogarType.INCORRETO),
    PI("Prova incorreta", RevogarType.PROVA),
    PAI("Punição aplicada injustamente", RevogarType.APLICADA),
    RA("Revisão aceita", RevogarType.ACEITA);

    private final String text;
    private final RevogarType punishType;

    ReasonRevogar(String text, RevogarType revogarType) {
        this.text = text;
        this.punishType = revogarType;
    }
}
