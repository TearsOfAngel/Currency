package com.example.Currency.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    @JsonProperty(namespace = "gainsAndLosses")
    private List<GainAndLossData> gainAndLossData;
    @JsonProperty(namespace = "currencyData")
    private List<CurrencyData> currencyData;
}
