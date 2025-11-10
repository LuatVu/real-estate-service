package com.realestate.services;

import com.realestate.dto.UserPackagesDTO;
import com.realestate.dto.PackagesDTO;
import java.util.List;

public interface PackagesService {
    List<UserPackagesDTO> getUserPackages(String userId);
    List<PackagesDTO> getAllPackages();
}
