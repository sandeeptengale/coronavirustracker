package com.sandeep.coronavirustracker.services;

import com.sandeep.coronavirustracker.model.LocationStat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {
    Logger logger = LoggerFactory.getLogger(CoronaVirusDataService.class.getName());

    private static final String DATA_SOURCE_URI =  "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private List<LocationStat> locStatList = new ArrayList<>();

    public List<LocationStat> getLocStatList() {
        return locStatList;
    }

    @PostConstruct
    @Scheduled(cron = "* * * 1 * *")
    public void fetchVirusData() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DATA_SOURCE_URI))
                .build();

        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            readCsvData(httpResponse.body());
        } catch (InterruptedException e) {
           logger.error("Error sending request data: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error processing the request: " + e.getMessage());
        }
    }

    public void readCsvData(String responseBody) throws IOException {
        List<LocationStat> locStats = new ArrayList<>();
        StringReader reader = new StringReader(responseBody);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord csvRecord: records) {
            LocationStat ls = new LocationStat();
            ls.setState(csvRecord.get("Province/State"));
            ls.setCountry(csvRecord.get("Country/Region"));
            ls.setLatestTotalCases(Integer.parseInt(csvRecord.get(csvRecord.size() - 1)));
            locStats.add(ls);
            System.out.println(ls);
        }
        locStatList = locStats;
    }
}
