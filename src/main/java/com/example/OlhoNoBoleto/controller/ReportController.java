package com.example.OlhoNoBoleto.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;


import com.example.OlhoNoBoleto.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

      @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDTO> registrar(@RequestBody ReportResponseDTO report) {
        ReportResponseDTO response = reportService.createReport(report);
        return ResponseEntity.ok(response);
    }
}
