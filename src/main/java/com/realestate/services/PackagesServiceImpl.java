package com.realestate.services;

import org.hibernate.ObjectNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.realestate.dto.UserPackagesDTO;
import com.realestate.models.Posts;
import com.realestate.models.UserBalances;
import com.realestate.models.UserPackages;
import com.realestate.models.UserBalances.BalanceType;
import com.realestate.dto.PackagesDTO;
import com.realestate.dto.RegisterPackageDTO;
import com.realestate.repositories.UserPackagesRepository;
import com.realestate.repositories.UserRepository;
import com.realestate.utilities.EnumUtils;
import com.realestate.models.Packages;
import jakarta.transaction.Transactional;
import java.util.Optional;
import com.realestate.repositories.PackagesRepository;
import com.realestate.repositories.UserBalancesRepository;
import com.realestate.models.User;
import java.time.LocalDateTime;

@Service
public class PackagesServiceImpl implements PackagesService {
    private final UserPackagesRepository userPackagesRepository;
    private final PackagesRepository packagesRepository;
    private final UserBalancesRepository userBalancesRepo;
    private final UserRepository userRepository;


    public PackagesServiceImpl(UserPackagesRepository userPackagesRepository, 
        PackagesRepository packagesRepository, 
        UserBalancesRepository userBalancesRepo,
        UserRepository userRepository) {
        this.userPackagesRepository = userPackagesRepository;
        this.packagesRepository = packagesRepository;
        this.userBalancesRepo = userBalancesRepo;
        this.userRepository = userRepository;
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

    @Override
    public List<PackagesDTO> getAllPackages() {
        return packagesRepository.findAllActivePackages().stream()
                .map(packageEntity -> PackagesDTO.builder()
                        .packageId(packageEntity.getPackageId())
                        .packageName(packageEntity.getPackageName())
                        .packageDescription(packageEntity.getDescription())
                        .diamondPosts(packageEntity.getMaxDiamondPosts())
                        .goldPosts(packageEntity.getMaxGoldPosts())
                        .silverPosts(packageEntity.getMaxSilverPosts())
                        .normalPosts(packageEntity.getMaxNormalPosts())
                        .price(packageEntity.getPrice().doubleValue())
                        .discount(packageEntity.getDiscount().doubleValue())
                        .createdDate(packageEntity.getCreatedDate())
                        .updatedDate(packageEntity.getUpdatedDate())
                        .image(packageEntity.getImage())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserPackagesDTO registerPackages(RegisterPackageDTO data) throws Exception{
        Optional<Packages> packagesOpt = packagesRepository.findById(data.getPackageId());
        if(!packagesOpt.isPresent()){
            throw new Exception("Package not found for id: " + data.getPackageId());
        }        
        Packages packages = packagesOpt.get();
        Double chargeFee = packages.getPrice().doubleValue() * data.getMonths() * (1 -  packages.getDiscount().doubleValue() * (data.getMonths() - 1));
        if(chargeFee.doubleValue() > data.getPaymentAmount().doubleValue()){
            throw new Exception("Payment amount must be at least " + chargeFee);
        }
        UserBalances userBalances = userBalancesRepo.findByUserIdAndBalanceType(data.getUserId(), EnumUtils.fromString(BalanceType.class, "MAIN"));
        if(userBalances == null || userBalances.getBalance() < data.getPaymentAmount()){
            throw new Exception("Main balance is not enough");
        }
        User user = userRepository.getReferenceById(data.getUserId());
        UserPackages userPackages =  UserPackages.builder()
                                    .user(user)
                                    .packages(packages)
                                    .remainingDiamondPosts(packages.getMaxDiamondPosts())
                                    .remainingGoldPosts(packages.getMaxGoldPosts())
                                    .remainingSilverPosts(packages.getMaxSilverPosts())
                                    .remainingNormalPosts(packages.getMaxNormalPosts())
                                    .expiredDate(LocalDateTime.now().plusMonths(data.getMonths()))                                    
                                    .build();
        UserPackages saveUserPackage = userPackagesRepository.save(userPackages);

        userBalances.setBalance(userBalances.getBalance() - chargeFee.doubleValue());
        userBalancesRepo.save(userBalances);

        UserPackagesDTO userPackagesDTO = UserPackagesDTO.builder()
        .userPackageId(saveUserPackage.getUserPackageId())
        .userId(saveUserPackage.getUser().getUserId())
        .packageId(saveUserPackage.getPackages().getPackageId())
        .packageName(saveUserPackage.getPackages().getPackageName())
        .remainingDiamondPosts(saveUserPackage.getRemainingDiamondPosts())
        .remainingGoldPosts(saveUserPackage.getRemainingGoldPosts())
        .remainingSilverPosts(saveUserPackage.getRemainingSilverPosts())
        .remainingNormalPosts(saveUserPackage.getRemainingNormalPosts())
        .activeDate(saveUserPackage.getActiveDate())
        .expiredDate(saveUserPackage.getExpiredDate())
        .status(saveUserPackage.getStatus().toString())
        .build();

        return userPackagesDTO;
    }
}
