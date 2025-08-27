package com.realestate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.dto.ProvinceDto;
import com.realestate.dto.WardDto;
import com.realestate.services.ProvinceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {
    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/fetch-all")
    public ResponseEntity<List<ProvinceDto>> getAllProvinces() throws Exception {
        List<ProvinceDto> provinces = provinceService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/fetch-wards")
    public ResponseEntity<List<WardDto>> getWardsFromProvinceCode(@RequestParam String cityCode) throws Exception {
        List<WardDto> wards = provinceService.getWardsFromProvinceCode(cityCode);
        return ResponseEntity.ok(wards);
    }
    
    
}
