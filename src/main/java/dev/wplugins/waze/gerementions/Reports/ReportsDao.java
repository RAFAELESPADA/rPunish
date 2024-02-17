package dev.wplugins.waze.gerementions.Reports;


import dev.wplugins.waze.gerementions.Main;
import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportsDao {

    private final ReportThread thread;
    @Getter
    private final ReportServices reportService;
    @Getter
    private final List<ReportsUtil> lastHourReports;

    public ReportsDao() {
        this.reportService = new ReportImpl();
        this.lastHourReports = new ArrayList<>();
        this.thread = Main.getInstance().getReportThread();
    }

    public void createReport(String reporterPlayer, String reportedPlayer, ServerInfo serverInfo) {
        ReportsUtil report = ReportsUtil.builder().
                id(UUID.randomUUID().toString().substring(0, 6)).
                server(serverInfo).
                reportedPlayer(reportedPlayer).
                reporterPlayer(reporterPlayer).
                date(new Date().getTime()).
                build();

        CompletableFuture.runAsync(() -> {
            while (getReportService().getReports().stream().anyMatch(p -> p.getId().equals(report.getId()))) {
                report.setId(UUID.randomUUID().toString().substring(0, 6));
            }
            reportService.create(report);
            lastHourReports.add(report);
            Database.getInstance().execute("INSERT INTO VulcanthReports VALUES (?, ?, ?, ?, ?)", report.getId(), report.getReporterPlayer(), report.getReportedPlayer(), report.getServer().getName(), report.getDate());
        }, thread);
    }

    public void loadReports() {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("SELECT * FROM `VulcanthReports`;");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    reportService.create(ReportsUtil.builder().
                            id(resultSet.getString("id")).
                            server(Main.getInstance().getProxy().getServerInfo(resultSet.getString("serverName"))).
                            reportedPlayer(resultSet.getString("reportedPlayer")).
                            reporterPlayer(resultSet.getString("reporterPlayer")).
                            date(resultSet.getLong("date")).
                            build());
                }
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, thread);
    }

    public void deleteReport(String id) {
        CompletableFuture.runAsync(() -> {
            if (reportService.get(id) != null) {
                reportService.remove(id);
                Database.getInstance().execute("DELETE FROM VulcanthReports WHERE id = ?", String.valueOf(id));
            }
        }, thread);
    }
}