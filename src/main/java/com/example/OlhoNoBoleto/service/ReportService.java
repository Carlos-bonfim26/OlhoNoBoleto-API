package com.example.OlhoNoBoleto.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.model.Report;
import com.example.OlhoNoBoleto.repository.ReportRepository;

public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Report registrarReport(Report report) {
        return null;
    }

    public ReportResponseDTO createReport(ReportResponseDTO report) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createReport'");
    }
}
