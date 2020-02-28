package com.sandeep.coronavirustracker.controller;

import com.sandeep.coronavirustracker.model.LocationStat;
import com.sandeep.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStat> allLocStats = coronaVirusDataService.getLocStatList();
        int totalNoCases = allLocStats.stream().mapToInt(LocationStat::getLatestTotalCases).sum();
        model.addAttribute("totalCases", totalNoCases);
        model.addAttribute("locStats", coronaVirusDataService.getLocStatList());
        return "home";
    }
}
