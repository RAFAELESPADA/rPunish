package dev.wplugins.waze.gerementions.Reports;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;

@Getter
@Builder
public class ReportsUtil {
    @Setter
    private String id;
    private String reporterPlayer, reportedPlayer;
    private ServerInfo server;
    private long date;
}