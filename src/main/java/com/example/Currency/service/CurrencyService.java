package com.example.Currency.service;

import com.example.Currency.dto.CurrencyData;
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

            double maxUsdLoss = findMaxLoss(currencyDataList, "USD");
            double maxUsdGain = findMaxGain(currencyDataList, "USD");
            double maxEurLoss = findMaxLoss(currencyDataList, "EUR");
            double maxEurGain = findMaxGain(currencyDataList, "EUR");

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

    private double findMaxLoss(List<CurrencyData> data, String currency) {
        double maxLoss = 0.0;

        for (int i = 1; i < data.size(); i++) {
            double currentDayValue = getValue(data.get(i), currency);
            double previousDayValue = getValue(data.get(i - 1), currency);
            double loss = previousDayValue - currentDayValue;
            maxLoss = Math.max(maxLoss, loss);
        }
        return maxLoss;
    }

    private double findMaxGain(List<CurrencyData> data, String currency) {
        double maxGain = 0.0;

        for (int i = 1; i < data.size(); i++) {
            double currentDayValue = getValue(data.get(i), currency);
            double previousDayValue = getValue(data.get(i - 1), currency);
            double gain = currentDayValue - previousDayValue;
            maxGain = Math.max(maxGain, gain);
        }

        return maxGain;
    }

    private double getValue(CurrencyData data, String currency) {
        return "USD".equals(currency) ? data.getUsd() : data.getEur();
    }
}
