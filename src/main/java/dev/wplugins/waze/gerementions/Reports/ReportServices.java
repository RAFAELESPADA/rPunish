package dev.wplugins.waze.gerementions.Reports;


import dev.wplugins.waze.gerementions.model.Model;

import java.util.Set;

public interface ReportServices extends Model<String, ReportsUtil> {

    Set<ReportsUtil> getReports();

}
