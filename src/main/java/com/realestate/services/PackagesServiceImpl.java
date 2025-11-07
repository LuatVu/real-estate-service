package com.realestate.services;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.realestate.dto.UserPackagesDTO;
import com.realestate.repositories.UserPackagesRepository;

@Service
public class PackagesServiceImpl implements PackagesService {
    private final UserPackagesRepository userPackagesRepository;

    public PackagesServiceImpl(UserPackagesRepository userPackagesRepository) {
        this.userPackagesRepository = userPackagesRepository;
    }

    @Override
    public List<UserPackagesDTO> getUserPackages(String userId){
        return userPackagesRepository.findByUserId(userId).stream()
                    .sorted((up1, up2) -> up1.getPackages().getOrder().compareTo(up2.getPackages().getOrder()))
                .map(userPackage -> UserPackagesDTO.builder()
                        .userPackageId(userPackage.getUserPackageId())
                        .userId(userId)
                        .packageId(userPackage.getPackages().getPackageId())
                        .packageName(userPackage.getPackages().getPackageName())
                        .packageDescription(userPackage.getPackages().getDescription())
                        .remainingDiamondPosts(userPackage.getRemainingDiamondPosts())
                        .remainingGoldPosts(userPackage.getRemainingGoldPosts())
                .remainingSilverPosts(userPackage.getRemainingSilverPosts())
                .remainingNormalPosts(userPackage.getRemainingNormalPosts())
                .activeDate(userPackage.getActiveDate())
                .expiredDate(userPackage.getExpiredDate())
                .status(userPackage.getStatus().name())
                .image(userPackage.getPackages().getImage())
                .build()).collect(Collectors.toList());
    }
}
