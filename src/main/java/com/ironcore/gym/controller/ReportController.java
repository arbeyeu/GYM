package com.ironcore.gym.controller;

import com.ironcore.gym.dto.ReportFilterDto;
import com.ironcore.gym.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reportsView(@ModelAttribute("filter") ReportFilterDto filter, Model model) {
        Map<String, Object> data = reportService.generateReportData(filter);
        model.addAttribute("reportData", data);
        model.addAttribute("filter", filter);
        model.addAttribute("activeNav", "reports");
        return "reports/index";
    }

    @GetMapping("/export")
    public void exportCsv(@ModelAttribute ReportFilterDto filter, HttpServletResponse response) throws IOException {
        String csvContent = reportService.exportToCsv(filter);
        String filename = "ironcore_" + filter.getReportType().toLowerCase() + "_" + System.currentTimeMillis() + ".csv";

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.getWriter().write(csvContent);
    }
}
