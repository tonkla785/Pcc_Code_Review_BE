package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import pccth.code.review.Backend.DTO.Request.ReportGenerateRequestDTO;
import pccth.code.review.Backend.DTO.Response.ReportViewModel;
import pccth.code.review.Backend.DTO.Response.SecurityMetricsResponseDTO;
import pccth.code.review.Backend.Entity.IssueDetailEntity;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportDataAggregator {

    private static final List<String> REPORT_TYPES = List.of("BUG", "VULNERABILITY", "SECURITY_HOTSPOT");
    private static final BigDecimal DEFAULT_COST_PER_DAY = new BigDecimal("1000");
    private static final int MINUTES_PER_DAY = 480;

    private static final Map<String, Integer> SEVERITY_ORDER = Map.of(
            "BLOCKER", 0, "CRITICAL", 1, "MAJOR", 2, "MINOR", 3, "INFO", 4);

    private final ProjectRepository projectRepository;
    private final ScanRepository scanRepository;
    private final IssueRepository issueRepository;
    private final SecurityMetricsService securityMetricsService;

    public ReportDataAggregator(ProjectRepository projectRepository,
                                ScanRepository scanRepository,
                                IssueRepository issueRepository,
                                SecurityMetricsService securityMetricsService) {
        this.projectRepository = projectRepository;
        this.scanRepository = scanRepository;
        this.issueRepository = issueRepository;
        this.securityMetricsService = securityMetricsService;
    }

    public ReportViewModel build(ReportGenerateRequestDTO req) {
        ProjectEntity project = projectRepository.findById(req.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        LocalDate from = req.getDateFrom();
        LocalDate to = req.getDateTo();
        boolean rangeMode = (from != null && to != null);

        List<ScanEntity> successScans = scanRepository.findByProjectId(project.getId()).stream()
                .filter(s -> s.getStatus() == ScanStatusEnum.SUCCESS)
                .sorted(Comparator.comparing(ScanEntity::getStartedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        List<ScanEntity> scans;
        if (rangeMode) {
            scans = successScans.stream()
                    .filter(s -> withinRange(s.getStartedAt(), from, to))
                    .toList();
        } else {
            scans = successScans;
        }

        if (scans.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    rangeMode ? "No successful scan found in the selected date range"
                            : "No successful scan found for this project");
        }

        LocalDate displayFrom = from;
        LocalDate displayTo = to;
        if (!rangeMode) {
            displayTo = toLocalDate(scans.get(0).getStartedAt());
            displayFrom = toLocalDate(scans.get(scans.size() - 1).getStartedAt());
        }

        List<IssueEntity> openIssues = issueRepository.findByProjectIdWithDetail(project.getId()).stream()
                .filter(i -> REPORT_TYPES.contains(upper(i.getType())))
                .filter(this::isOpen)
                .toList();

        ReportGenerateRequestDTO.Sections sec = req.getSections();

        ReportViewModel vm = new ReportViewModel();
        vm.setProjectName(project.getName());
        vm.setDateFrom(displayFrom.toString());
        vm.setDateTo(displayTo.toString());
        vm.setGeneratedDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        vm.setGeneratedBy(req.getGeneratedBy() == null || req.getGeneratedBy().isBlank()
                ? "Unknown" : req.getGeneratedBy());

        vm.setShowQualityGate(sec.isQualityGate());
        vm.setShowIssueBreakdown(sec.isIssueBreakdown());
        vm.setShowSecurityAnalysis(sec.isSecurityAnalysis());
        vm.setShowTechnicalDebt(sec.isTechnicalDebt());
        vm.setShowRecommendations(sec.isRecommendations());

        if (sec.isQualityGate()) {
            vm.setQualityGate(buildQualityGate(scans));
        }
        if (sec.isIssueBreakdown()) {
            vm.setIssues(buildIssueRows(openIssues));
        }
        if (sec.isSecurityAnalysis()) {
            vm.setSecurity(buildSecurity(project.getId()));
        }
        if (sec.isTechnicalDebt()) {
            vm.setTechnicalDebt(buildDebtRows(scans, project));
        }
        if (sec.isRecommendations()) {
            vm.setRecommendations(buildRecommendations(openIssues));
        }

        return vm;
    }

    private ReportViewModel.QualityGate buildQualityGate(List<ScanEntity> scans) {
        ReportViewModel.QualityGate qg = new ReportViewModel.QualityGate();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

        int passed = 0;
        int failed = 0;
        for (ScanEntity s : scans) {
            String status = upper(s.getStatus() == null ? "" : s.getStatus().name());
            if (status.equals("SUCCESS") || status.equals("PASSED") || status.equals("OK")) {
                passed++;
            } else if (status.equals("FAILED") || status.equals("ERROR") || status.equals("FAIL")) {
                failed++;
            }

            ReportViewModel.ScanRow row = new ReportViewModel.ScanRow();
            row.setDate(s.getStartedAt() == null ? "N/A" : fmt.format(s.getStartedAt()));
            row.setStatus(s.getStatus() == null ? "N/A" : s.getStatus().name());
            row.setQualityGate(displayQualityGate(s.getQualityGate()));
            Map<String, Object> m = s.getMetrics();
            row.setReliability(strVal(m, "reliabilityRating"));
            row.setSecurity(strVal(m, "securityRating"));
            row.setMaintainability(strVal(m, "maintainabilityRating"));
            row.setBugs(intVal(m, "bugs"));
            row.setVulnerabilities(intVal(m, "vulnerabilities"));
            row.setCodeSmells(intVal(m, "codeSmells"));
            qg.getScans().add(row);
        }

        qg.setTotalScans(scans.size());
        qg.setPassed(passed);
        qg.setFailed(failed);
        return qg;
    }

    private List<ReportViewModel.IssueRow> buildIssueRows(List<IssueEntity> issues) {
        return sortBySeverity(issues).stream()
                .map(i -> new ReportViewModel.IssueRow(
                        typeLabel(i.getType()),
                        blankToDash(i.getSeverity()),
                        blankToDash(i.getComponent()),
                        i.getLine() == null ? "-" : String.valueOf(i.getLine()),
                        blankToDash(i.getMessage())))
                .toList();
    }

    private ReportViewModel.Security buildSecurity(UUID projectId) {
        SecurityMetricsResponseDTO m = securityMetricsService.getMetricsSecurityByProject(projectId);
        ReportViewModel.Security s = new ReportViewModel.Security();
        s.setScore(m.getScore());
        s.setRiskLevel(m.getRiskLevel());

        if (m.getVulnerabilities() != null) {
            m.getVulnerabilities().forEach(v ->
                    s.getVulnerabilities().add(new ReportViewModel.NameCount(v.getName(), v.getCount())));
        }
        if (m.getHotIssues() != null) {
            m.getHotIssues().stream().limit(5).forEach(h ->
                    s.getHotIssues().add(new ReportViewModel.NameCount(prettify(h.getName()), h.getCount())));
        }
        if (m.getOwaspCoverage() != null) {
            m.getOwaspCoverage().forEach(o ->
                    s.getOwasp().add(new ReportViewModel.OwaspRow(o.getName(), o.getCount(), statusLabel(o.getStatus()))));
        }
        return s;
    }

    private List<ReportViewModel.DebtRow> buildDebtRows(List<ScanEntity> scans, ProjectEntity project) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        BigDecimal costPerDay = project.getCostPerDay() == null ? DEFAULT_COST_PER_DAY : project.getCostPerDay();

        return scans.stream().map(s -> {
            int debtMinutes = intVal(s.getMetrics(), "technicalDebtMinutes");
            double cost = (debtMinutes / (double) MINUTES_PER_DAY) * costPerDay.doubleValue();
            return new ReportViewModel.DebtRow(
                    s.getStartedAt() == null ? "N/A" : fmt.format(s.getStartedAt()),
                    project.getName(),
                    formatDebt(debtMinutes),
                    formatCost(cost));
        }).toList();
    }

    private List<ReportViewModel.RecRow> buildRecommendations(List<IssueEntity> issues) {
        return sortBySeverity(issues).stream().map(i -> {
            IssueDetailEntity d = i.getDetail();
            String fix = d != null && d.getRecommendedFix() != null && !d.getRecommendedFix().isBlank()
                    ? d.getRecommendedFix()
                    : "No recommendation available";
            return new ReportViewModel.RecRow(
                    blankToDash(i.getSeverity()),
                    typeLabel(i.getType()),
                    blankToDash(i.getComponent()),
                    i.getLine() == null ? "-" : String.valueOf(i.getLine()),
                    blankToDash(i.getMessage()),
                    fix);
        }).toList();
    }

    private List<IssueEntity> sortBySeverity(List<IssueEntity> issues) {
        return issues.stream()
                .sorted(Comparator.comparingInt(i -> SEVERITY_ORDER.getOrDefault(upper(i.getSeverity()), 5)))
                .toList();
    }

    private boolean isOpen(IssueEntity i) {
        String s = upper(i.getStatus());
        return !s.equals("RESOLVED") && !s.equals("CLOSED");
    }

    private LocalDate toLocalDate(Date d) {
        return d == null ? LocalDate.now()
                : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private boolean withinRange(Date startedAt, LocalDate from, LocalDate to) {
        if (startedAt == null) return false;
        LocalDate d = startedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return !d.isBefore(from) && !d.isAfter(to);
    }

    private String displayQualityGate(String qg) {
        if (qg == null) return "-";
        String u = qg.toUpperCase();
        if (u.equals("OK") || u.equals("PASSED")) return "Pass";
        if (u.equals("ERROR") || u.equals("FAILED")) return "Fail";
        return qg;
    }

    private String typeLabel(String type) {
        String t = type == null ? "" : type.toUpperCase();
        return switch (t) {
            case "BUG" -> "Bug";
            case "VULNERABILITY" -> "Vulnerability";
            case "CODE_SMELL" -> "Code Smell";
            case "SECURITY_HOTSPOT" -> "Security Hotspot";
            default -> type == null ? "-" : type;
        };
    }

    private String statusLabel(String status) {
        if (status == null) return "-";
        return switch (status.toLowerCase()) {
            case "pass" -> "Pass";
            case "warning" -> "Warning";
            case "fail" -> "Fail";
            default -> status;
        };
    }

    private String prettify(String slug) {
        if (slug == null || slug.isBlank()) return "-";
        if (slug.equalsIgnoreCase("others")) return "Other";
        String[] parts = slug.split("[-_\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.length() == 0 ? slug : sb.toString();
    }

    private String formatDebt(int minutes) {
        int days = minutes / MINUTES_PER_DAY;
        int rem = minutes % MINUTES_PER_DAY;
        int hours = rem / 60;
        int mins = rem % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (mins > 0 || sb.length() == 0) sb.append(mins).append("m");
        return sb.toString().trim();
    }

    private String formatCost(double cost) {
        return String.format("THB %,.2f", cost);
    }

    private int intVal(Map<String, Object> m, String key) {
        if (m == null) return 0;
        Object v = m.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try {
                return (int) Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private String strVal(Map<String, Object> m, String key) {
        if (m == null) return "-";
        Object v = m.get(key);
        return v == null ? "-" : v.toString();
    }

    private String upper(String s) {
        return s == null ? "" : s.toUpperCase();
    }

    private String blankToDash(String s) {
        return s == null || s.isBlank() ? "-" : s;
    }
}
