package com.example.Currency.controller;

import com.example.Currency.dto.CurrencyData;
import com.example.Currency.dto.responses.DataResponse;
import com.example.Currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiController {
    private CurrencyService currencyService;

    public ApiController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DataResponse> uploadFile(MultipartFile file) {
        return ResponseEntity.ok(currencyService.processCsv(file));
    }
}
