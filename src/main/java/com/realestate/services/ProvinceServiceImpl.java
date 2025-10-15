package com.realestate.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.realestate.dto.ProvinceDto;
import com.realestate.dto.WardDto;
import com.realestate.repositories.ProvinceRepository;
import com.realestate.utilities.StringUtils;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    @Autowired
    private ProvinceRepository provinceRepository;

    public List<ProvinceDto> getAllProvinces() throws Exception {
        // Implementation code here
        return provinceRepository.findAll().stream()
                .sorted((p1, p2) -> p1.getOrder().compareTo(p2.getOrder()))
                .map(province -> {
                    ProvinceDto dto = ProvinceDto.builder()
                            .code(province.getCode())
                            .name(province.getName())
                            .nameEn(province.getNameEn())
                            .fullName(province.getFullName())
                            .fullNameEn(province.getFullNameEn())
                            .image(province.getImage())
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WardDto> getWardsFromProvinceCode(String cityCode) throws Exception {
        // Implementation code here
        return provinceRepository.findById(cityCode).stream()
                .flatMap(province -> province.getWards().stream())
                .sorted((w1, w2) -> {
                    String name1 = StringUtils.normalizeVietnameseName(w1.getName());
                    String name2 = StringUtils.normalizeVietnameseName(w2.getName());
                    return name1.compareTo(name2);
                })
                .map(ward -> {
                    WardDto dto = WardDto.builder()
                            .code(ward.getCode())
                            .name(ward.getName())
                            .nameEn(ward.getNameEn())
                            .fullName(ward.getFullName())
                            .fullNameEn(ward.getFullNameEn())
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
