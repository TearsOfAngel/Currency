package com.example.Currency.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CurrencyData {
    private LocalDate date;
    private double usd;
    private double eur;
}
