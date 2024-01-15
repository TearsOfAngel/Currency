package com.example.Currency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GainAndLossData {
    private double value;
    private LocalDate date;
    private String currency;

}
