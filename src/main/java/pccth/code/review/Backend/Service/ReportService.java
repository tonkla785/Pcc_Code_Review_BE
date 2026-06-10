package pccth.code.review.Backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pccth.code.review.Backend.DTO.Request.MarkdownReportRequestDTO;
import pccth.code.review.Backend.DTO.Request.ReportGenerateRequestDTO;
import pccth.code.review.Backend.DTO.Request.ReportHistoryRequestDTO;
import pccth.code.review.Backend.DTO.Response.ProjectSummaryDTO;
import pccth.code.review.Backend.DTO.Response.ReportGenerateResponseDTO;
import pccth.code.review.Backend.DTO.Response.ReportViewModel;
import pccth.code.review.Backend.Repository.ProjectRepository;

import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportDataAggregator aggregator;
    private final SpringTemplateEngine templateEngine;
    private final HtmlToPdfConverter pdfConverter;
    private final ReportMarkdownRenderer markdownRenderer;
    private final ReportHistoryService reportHistoryService;
    private final ProjectRepository projectRepository;

    public ReportService(ReportDataAggregator aggregator,
                         SpringTemplateEngine templateEngine,
                         HtmlToPdfConverter pdfConverter,
                         ReportMarkdownRenderer markdownRenderer,
                         ReportHistoryService reportHistoryService,
                         ProjectRepository projectRepository) {
        this.aggregator = aggregator;
        this.templateEngine = templateEngine;
        this.pdfConverter = pdfConverter;
        this.markdownRenderer = markdownRenderer;
        this.reportHistoryService = reportHistoryService;
        this.projectRepository = projectRepository;
    }

    public List<ProjectSummaryDTO> listProjects() {
        return projectRepository.findAll().stream()
                .map(p -> new ProjectSummaryDTO(p.getId(), p.getName(), p.getSonarProjectKey()))
                .sorted(Comparator.comparing(p -> p.getName() == null ? "" : p.getName().toLowerCase()))
                .toList();
    }

    public ReportGenerateResponseDTO generate(ReportGenerateRequestDTO req) {
        String format = req.getFormat() == null ? "pdf" : req.getFormat().toLowerCase();
        if (!format.equals("pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only 'pdf' format is currently supported");
        }

        if (req.getSections() == null || !req.getSections().hasAny()) {
            req.setSections(ReportGenerateRequestDTO.Sections.all());
        }

        ReportViewModel vm = aggregator.build(req);

        Context ctx = new Context();
        ctx.setVariable("report", vm);
        String html = templateEngine.process("report", ctx);

        byte[] pdf = pdfConverter.convert(html);
        String base64 = Base64.getEncoder().encodeToString(pdf);
        String fileName = buildFileName(vm, "pdf");

        maybeSaveHistory(req, vm, pdf.length, "PDF", "backend-pdf");

        return new ReportGenerateResponseDTO(
                fileName, "application/pdf", base64, pdf.length, Instant.now());
    }

    public String generateMarkdown(MarkdownReportRequestDTO request) {
        ReportGenerateRequestDTO req = new ReportGenerateRequestDTO();
        req.setProjectId(request.getProjectId());
        req.setSections(ReportGenerateRequestDTO.Sections.all());

        ReportViewModel vm = aggregator.build(req);
        return markdownRenderer.render(vm);
    }

    private void maybeSaveHistory(ReportGenerateRequestDTO req, ReportViewModel vm, int sizeBytes,
                                 String format, String generatedVia) {
        if (req.getUserId() == null || req.getDateFrom() == null || req.getDateTo() == null) {
            return;
        }
        try {
            ReportHistoryRequestDTO history = new ReportHistoryRequestDTO();
            history.setProjectId(req.getProjectId());
            history.setProjectName(vm.getProjectName());
            history.setDateFrom(req.getDateFrom());
            history.setDateTo(req.getDateTo());
            history.setFormat(format);
            history.setGeneratedBy(vm.getGeneratedBy());
            history.setIncludeQualityGate(req.getSections().isQualityGate());
            history.setIncludeIssueBreakdown(req.getSections().isIssueBreakdown());
            history.setIncludeSecurityAnalysis(req.getSections().isSecurityAnalysis());
            history.setIncludeTechnicalDebt(req.getSections().isTechnicalDebt());
            history.setIncludeRecommendations(req.getSections().isRecommendations());
            history.setFileSizeBytes((long) sizeBytes);

            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("generatedVia", generatedVia);
            snapshot.put("fileSizeBytes", sizeBytes);
            history.setSnapshotData(snapshot);

            reportHistoryService.createReportHistory(req.getUserId(), history);
        } catch (Exception e) {
            log.warn("Failed to save report history: {}", e.getMessage());
        }
    }

    private String buildFileName(ReportViewModel vm, String ext) {
        String safeProject = vm.getProjectName() == null ? "report"
                : vm.getProjectName().replaceAll("[^a-zA-Z0-9-_ก-๙]+", "_");
        return "Report_" + safeProject + "_" + vm.getDateFrom() + "_to_" + vm.getDateTo() + "." + ext;
    }
}
