package com.realestate.services;

import com.realestate.dto.UserPackagesDTO;
import java.util.List;

public interface PackagesService {
    List<UserPackagesDTO> getUserPackages(String userId);
}
