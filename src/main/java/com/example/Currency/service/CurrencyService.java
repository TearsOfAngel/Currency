package com.example.Currency.service;

import com.example.Currency.dto.CurrencyData;
import com.example.Currency.dto.GainAndLossData;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CurrencyService {

    public List<CurrencyData> processCsv(MultipartFile file) {

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<CurrencyData> currencyDataList = parseCSV(records);
            for (CurrencyData data : currencyDataList) {
                dataset.addValue(data.getUsd(), "USD", data.getDate().toString());
                dataset.addValue(data.getEur(), "EUR", data.getDate().toString());
            }

            GainAndLossData maxUsdLoss = findMaxLoss(currencyDataList, "USD");
            GainAndLossData maxUsdGain = findMaxGain(currencyDataList, "USD");
            GainAndLossData maxEurLoss = findMaxLoss(currencyDataList, "EUR");
            GainAndLossData maxEurGain = findMaxGain(currencyDataList, "EUR");

            generateChart(dataset, "Currency Exchange Rates");

            System.out.println(maxUsdGain);
            System.out.println(maxUsdLoss);
            System.out.println(maxEurGain);
            System.out.println(maxEurLoss);
            return currencyDataList;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String generateChart(DefaultCategoryDataset dataset, String title) {
        JFreeChart lineChart = ChartFactory.createLineChart(
                title,
                "Date",
                "Exchange Rate",
                dataset
        );

        try {
            File chartFile = new File("src/main/resources/static/images/chart.png");
            ChartUtils.saveChartAsPNG(chartFile, lineChart, 1000, 800);
            return chartFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<CurrencyData> parseCSV (List < String[]>records){
        List<CurrencyData> currencyDataList = new ArrayList<>();

        for (int i = 1; i < records.size(); i++) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            try {
                String[] fields = records.get(i)[0].split(";");
                LocalDate date = LocalDate.parse(fields[0], dateFormatter);
                double usd = Double.parseDouble(fields[1]);
                double eur = Double.parseDouble(fields[2]);

                CurrencyData currencyData = new CurrencyData();
                currencyData.setDate(date);
                currencyData.setUsd(usd);
                currencyData.setEur(eur);

                currencyDataList.add(currencyData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return currencyDataList;
    }

    //TODO: Подумать над тем чтобы возвращать отрицательные числа. Сейчас возвращается просто объект из которого не понятно подъем это или падение.
    public GainAndLossData findMaxLoss(List<CurrencyData> data, String currency) {
        double maxLoss = Double.MIN_VALUE;
        LocalDate maxLossDate = null;
        GainAndLossData lossDTO = new GainAndLossData();

        for (int i = 1; i < data.size(); i++) {
            double currentDayValue = getValue(data.get(i), currency);
            double previousDayValue = getValue(data.get(i - 1), currency);
            double loss = previousDayValue - currentDayValue;

            if (loss > maxLoss) {
                maxLoss = loss;
                maxLossDate = data.get(i).getDate();
            }
        }

        lossDTO.setCurrency(currency);
        lossDTO.setValue(roundTo4Decimals(maxLoss));
        lossDTO.setDate(maxLossDate);

        if (maxLossDate != null) {
            return lossDTO;
        } else {
            return null;
        }
    }

    private GainAndLossData findMaxGain(List<CurrencyData> data, String currency) {
        double maxGain = 0.0;
        LocalDate maxGainDate = null;
        GainAndLossData gainDTO = new GainAndLossData();

        for (int i = 1; i < data.size(); i++) {
            double currentDayValue = getValue(data.get(i), currency);
            double previousDayValue = getValue(data.get(i - 1), currency);
            double gain = currentDayValue - previousDayValue;

            if (gain > maxGain) {
                maxGain = gain;
                maxGainDate = data.get(i).getDate();

            }
        }

        gainDTO.setCurrency(currency);
        gainDTO.setValue(roundTo4Decimals(maxGain));
        gainDTO.setDate(maxGainDate);

        if (maxGainDate != null) {
            return gainDTO;
        } else {
            return null;
        }
    }

    private double getValue(CurrencyData data, String currency) {
        return "USD".equals(currency) ? data.getUsd() : data.getEur();
    }

    private double roundTo4Decimals(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
