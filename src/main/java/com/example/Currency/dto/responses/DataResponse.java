package com.example.Currency.dto.responses;

import com.example.Currency.dto.CurrencyData;
import lombok.Data;

import java.util.List;

@Data
public class DataResponse {
    List<CurrencyData> currencyDataList;
}
