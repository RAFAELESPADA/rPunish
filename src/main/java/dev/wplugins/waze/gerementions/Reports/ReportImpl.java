package dev.wplugins.waze.gerementions.Reports;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ReportImpl implements ReportServices {

    private Set<ReportsUtil> reports;

    public ReportImpl() {
        reports = new HashSet<>();
    }

    @Override
    public void create(ReportsUtil report) {
        reports.add(report);
    }

    @Override
    public void remove(String s) {
        reports.remove(get(s));
    }

    @Override
    public ReportsUtil get(String s) {
        return search(s).findFirst().orElse(null);
    }

    @Override
    public Stream<ReportsUtil> search(String s) {
        return reports.stream().filter(report -> report.getId().equals(s));
    }

    @Override
    public Set<ReportsUtil> getReports() {
        return reports;
    }
}
