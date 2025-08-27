package com.realestate.services;

import java.util.List;

import com.realestate.dto.ProvinceDto;
import com.realestate.dto.WardDto;

public interface ProvinceService {
    List<ProvinceDto> getAllProvinces() throws Exception;

    List<WardDto> getWardsFromProvinceCode(String cityCode) throws Exception;
}
