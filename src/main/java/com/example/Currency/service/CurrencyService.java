package com.example.Currency.service;

import com.example.Currency.dto.CurrencyData;
import com.example.Currency.dto.responses.DataResponse;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyService {

    public DataResponse processCsv(MultipartFile file) {
        DataResponse dataResponse = new DataResponse();

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //TODO: we must return max gain and loss. Not a value. Fix me later
            double maxUsdGain = Double.MIN_VALUE;
            double maxUsdLoss = Double.MAX_VALUE;
            double maxEurGain = Double.MIN_VALUE;
            double maxEurLoss = Double.MAX_VALUE;

            List<CurrencyData> currencyDataList = parseCSV(records);
            //TODO: fix response. It must be only list.
            dataResponse.setCurrencyDataList(currencyDataList);
            for (CurrencyData data : currencyDataList) {
                dataset.addValue(data.getUsd(), "USD", data.getDate().toString());
                dataset.addValue(data.getEur(), "EUR", data.getDate().toString());

                maxUsdGain = Math.max(maxUsdGain, data.getUsd());
                maxUsdLoss = Math.min(maxUsdLoss, data.getUsd());
                maxEurGain = Math.max(maxEurGain, data.getEur());
                maxEurLoss = Math.min(maxEurLoss, data.getEur());
            }
            generateChart(dataset, "Currency Exchange Rates");

            System.out.println(maxUsdGain);
            System.out.println(maxUsdLoss);
            System.out.println(maxEurGain);
            System.out.println(maxEurLoss);
            return dataResponse;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return new DataResponse();
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
}
