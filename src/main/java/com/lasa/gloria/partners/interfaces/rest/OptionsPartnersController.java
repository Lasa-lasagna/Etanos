package com.lasa.gloria.partners.interfaces.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lasa.gloria.partners.application.service.OptionsService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionsPartnersController {

    private final OptionsService optionsService;

    @GetMapping
    public List<String> getOptionsPartners() {
        return optionsService.getOptionsDocuments();
    }
    
}
