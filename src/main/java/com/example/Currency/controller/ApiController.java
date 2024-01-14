package com.example.Currency.controller;

import com.example.Currency.dto.CurrencyData;
import com.example.Currency.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private CurrencyService currencyService;

    public ApiController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<CurrencyData>> uploadFile(MultipartFile file) {
        return ResponseEntity.ok(currencyService.processCsv(file));
    }
}
