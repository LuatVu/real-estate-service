package com.realestate.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.realestate.dto.ImagesDto;
import com.realestate.dto.PostDto;
import com.realestate.dto.RankingDto;
import com.realestate.dto.UserDto;
import com.realestate.models.Images;
import com.realestate.models.Posts;
import com.realestate.models.Posts.TransactionType;
import com.realestate.models.Ranking;
import com.realestate.models.User;
import com.realestate.models.UserBalances;
import com.realestate.models.Posts.Direction;
import com.realestate.models.Posts.FurnitureType;
import com.realestate.models.Posts.LegalType;
import com.realestate.models.Posts.PostStatus;
import com.realestate.models.Posts.Type;
import com.realestate.models.Ranking.PriorityLevel;
import com.realestate.repositories.ImageRepository;
import com.realestate.repositories.PostRepository;
import com.realestate.repositories.RankingRepository;
import com.realestate.repositories.PostChargeFeesRepository;
import com.realestate.repositories.UserBalancesRepository;
import com.realestate.repositories.UserRepository;
import com.realestate.utilities.EnumUtils;

import jakarta.transaction.Transactional;
import com.realestate.models.PostChargeFees;

@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private PostChargeFeesRepository postChargeFeesRepo;

    @Autowired
    private UserBalancesRepository userBalancesRepository;

    @Transactional
    public Posts createPost(PostDto postDto) throws Exception{
        User user = userRepository.getReferenceById(postDto.getUserId());
        Posts post =  Posts.builder()
                            .user(user).title(postDto.getTitle()).description(postDto.getDescription())
                            .acreage(postDto.getAcreage()).bedrooms(postDto.getBedrooms())
                            .bathrooms(postDto.getBathrooms())
                            .furniture(EnumUtils.fromString(FurnitureType.class, postDto.getFurniture()) )
                            .legal(EnumUtils.fromString(LegalType.class, postDto.getLegal())).price(postDto.getPrice())
                            .provinceCode(postDto.getProvinceCode())
                            // .districtCode(postDto.getDistrictCode())
                            .wardCode(postDto.getWardCode()).address(postDto.getAddress())                            
                            // .status(EnumUtils.fromString(PostStatus.class, postDto.getStatus()))
                            .status(PostStatus.DRAFT) // By default a new post will be Draft
                            .floors(postDto.getFloors())
                            .frontage(postDto.getFrontage())
                            .direction(EnumUtils.fromString(Direction.class, postDto.getDirection()))
                            .type(EnumUtils.fromString(Type.class, postDto.getType()))
                        .build();

        Posts savedPost =  postRepository.save(post);                        
        
        List<ImagesDto> imagesDtos = postDto.getImages();
        List<Images> imageJPAsList = new ArrayList<Images>();
        for (ImagesDto imagesDto : imagesDtos) {
            Images image = Images.builder()
                            .fileUrl(imagesDto.getFileUrl())
                            .fileName(imagesDto.getFileName())
                            .post(savedPost)
                            .isPrimary(imagesDto.getIsPrimary())                
                            .build();
            imageJPAsList.add(image);
        }
        imageRepository.saveAll(imageJPAsList);
        
        RankingDto rankDto = postDto.getRankingDto();
        Ranking rankJPA = Ranking.builder().post(savedPost)
                            .priorityLevel(EnumUtils.fromString(PriorityLevel.class,rankDto.getPriorityLevel()))
                            .build();
        rankingRepository.save(rankJPA);
        return savedPost;        
    }

    public PostDto getPost(String postId) throws Exception{

        Optional<Posts> postJPA = postRepository.findById(postId);
        if(postJPA.isPresent()){
            Posts model = postJPA.get();
            List<Images> images = model.getImages();
            List<ImagesDto> imagesDtos = new ArrayList<>();

            UserDto userDto = UserDto.builder()
                                    .userId(model.getUser().getUserId())
                                    .username(model.getUser().getUsername())
                                    .email(model.getUser().getEmail())
                                    .phoneNumber(model.getUser().getPhoneNumber())
                                    .contactPhoneNumber(model.getUser().getContactPhoneNumber())
                                    .profilePicture(model.getUser().getProfilePicture())
                                    .build();

            images.forEach(img -> {
                ImagesDto imgDTO = ImagesDto.builder()
                                    .imageId(img.getImageId())
                                    .postId(postId)
                                    .fileUrl(img.getFileUrl())
                                    .fileName(img.getFileName())
                                    .isPrimary(img.getIsPrimary())
                                    .build();
                imagesDtos.add(imgDTO);
            });

            PostDto postDTO = PostDto.builder()
                                .postId(postId)
                                .userId(model.getUser().getUserId())                                
                                .title(model.getTitle())
                                .description(model.getDescription())                                
                                .acreage(model.getAcreage())
                                .bedrooms(model.getBedrooms())
                                .bathrooms(model.getBathrooms())
                                .furniture(model.getFurniture() != null ? model.getFurniture().getValue() : null)
                                .legal(model.getLegal() != null ? model.getLegal().getValue() : null)                                
                                .price(model.getPrice())
                                .provinceCode(model.getProvinceCode())
                                .districtCode(model.getDistrictCode())
                                .wardCode(model.getWardCode())
                                .address(model.getAddress())
                                .createdDate(model.getCreatedDate())
                                .updatedDate(model.getUpdatedDate())
                                .expiredAt(model.getExpiredAt())
                                .status(model.getStatus().toString())
                                .type(model.getType() != null? model.getType().toString(): null)
                                .floors(model.getFloors())
                                .direction(model.getDirection() != null ? model.getDirection().name() : null)
                                .frontage(model.getFrontage())
                                .images(imagesDtos)
                                .user(userDto)
                                .build();
            return postDTO;
        }else{
            return null;
        }
        
    }

    public List<PostDto> getPost(com.realestate.dto.PostRequest postRequest, String userId) throws Exception{
        
        // Calculate date filter if lastDate is provided
        LocalDateTime dateFilter = null;
        if (postRequest.getLastDate() != null && postRequest.getLastDate() > 0) {
            dateFilter = LocalDateTime.now().minusDays(postRequest.getLastDate());
        }        
        
        // Get posts from repository with filters
        List<Posts> posts = postRepository.findPostsWithFilters(
            postRequest.getTitle(),
            EnumUtils.fromString(TransactionType.class, postRequest.getTransactionType()),
            dateFilter,
            userId
        );
        
        List<PostDto> postDtos = new ArrayList<>();
        
        for (Posts post : posts) {
            // Get images for each post
            List<Images> images = post.getImages();
            List<ImagesDto> imagesDtos = new ArrayList<>();
            
            if (images != null) {
                images.forEach(img -> {
                    ImagesDto imgDTO = ImagesDto.builder()
                                        .imageId(img.getImageId())
                                        .postId(post.getPostId())
                                        .fileUrl(img.getFileUrl())
                                        .fileName(img.getFileName())
                                        .isPrimary(img.getIsPrimary())
                                        .build();
                    imagesDtos.add(imgDTO);
                });
            }
            
            PostDto postDTO = PostDto.builder()
                                .postId(post.getPostId())
                                .userId(post.getUser().getUserId())                                
                                .title(post.getTitle())
                                .description(post.getDescription())                                
                                .acreage(post.getAcreage())
                                .bedrooms(post.getBedrooms())
                                .bathrooms(post.getBathrooms())
                                .furniture(post.getFurniture() != null ? post.getFurniture().getValue() : null)
                                .legal(post.getLegal() != null ? post.getLegal().getValue() : null)                                
                                .price(post.getPrice())
                                .provinceCode(post.getProvinceCode())
                                .districtCode(post.getDistrictCode())
                                .wardCode(post.getWardCode())
                                .address(post.getAddress())
                                .createdDate(post.getCreatedDate())
                                .updatedDate(post.getUpdatedDate())
                                .expiredAt(post.getExpiredAt())
                                .status(post.getStatus().toString())
                                .type(post.getType().name())
                                .floors(post.getFloors())
                                .direction(post.getDirection() != null ? post.getDirection().name() : null)
                                .transactionType(post.getTransactionType() != null ? post.getTransactionType().name() : null)
                                .images(imagesDtos)
                                .build();
            postDtos.add(postDTO);
        }
        return postDtos;
    }

    @Transactional
    public void updatePostStatus(String postId, String status) throws Exception{
        Optional<Posts> postJPA = postRepository.findById(postId);
        if(postJPA.isPresent()){
            Posts post = postJPA.get();
            post.setStatus(EnumUtils.fromString(PostStatus.class, status));
            postRepository.save(post);
        }else{
            throw new Exception("Post not found with ID: " + postId);
        }
    }

    public Page<PostDto> searchPosts(com.realestate.dto.PostSearchRequest request, int page, int size) throws Exception{
        
        // Convert type codes from strings to enum values if needed
        List<String> typeCodes = new ArrayList<>();
        Boolean isTypeCodesEmpty = true;
        if (request.getTypeCodes() != null && !request.getTypeCodes().isEmpty()) {
            typeCodes = request.getTypeCodes();
            isTypeCodesEmpty = false;
        }
        
        // Convert ward codes list
        List<String> wardCodes = new ArrayList<>();
        Boolean isWardCodesEmpty = true;
        if (request.getWardCodes() != null && !request.getWardCodes().isEmpty()) {
            wardCodes = request.getWardCodes();
            isWardCodesEmpty = false;
        }
        
        // Create Pageable with sorting by priority level and bump time
        Pageable pageable = PageRequest.of(page, size);
        
        // Convert BigDecimal to Double for repository call
        Double minPrice = request.getMinPrice() != null ? request.getMinPrice().doubleValue() : null;
        Double maxPrice = request.getMaxPrice() != null ? request.getMaxPrice().doubleValue() : null;
        Double minAcreage = request.getMinAcreage() != null ? request.getMinAcreage().doubleValue() : null;
        Double maxAcreage = request.getMaxAcreage() != null ? request.getMaxAcreage().doubleValue() : null;
        
        // Get posts from repository with search filters
        Page<Posts> postsPage = postRepository.searchPosts(
            request.getQuery(),
            minPrice,
            maxPrice,
            minAcreage,
            maxAcreage,
            typeCodes,
            isTypeCodesEmpty,
            request.getCityCode(),            
            wardCodes,
            isWardCodesEmpty,
            EnumUtils.fromString(TransactionType.class, request.getTransactionType() != null ? request.getTransactionType() : "SELL"),
            pageable
        );
        
        List<Posts> posts = postsPage.getContent();
        List<PostDto> postDtos = new ArrayList<>();
        
        for (Posts post : posts) {
            // Get images for each post
            List<Images> images = post.getImages();
            List<ImagesDto> imagesDtos = new ArrayList<>();
            
            if (images != null) {
                images.forEach(img -> {
                    ImagesDto imgDTO = ImagesDto.builder()
                                        .imageId(img.getImageId())
                                        .postId(post.getPostId())
                                        .fileUrl(img.getFileUrl())
                                        .fileName(img.getFileName())
                                        .isPrimary(img.getIsPrimary())
                                        .build();
                    imagesDtos.add(imgDTO);
                });
            }
            
            // Get user information
            UserDto userDto = UserDto.builder()
                                    .userId(post.getUser().getUserId())
                                    .username(post.getUser().getUsername())
                                    .email(post.getUser().getEmail())
                                    .phoneNumber(post.getUser().getPhoneNumber())
                                    .contactPhoneNumber(post.getUser().getContactPhoneNumber())
                                    .profilePicture(post.getUser().getProfilePicture())
                                    .build();
            
            PostDto postDTO = PostDto.builder()
                                .postId(post.getPostId())
                                .userId(post.getUser().getUserId())                                
                                .title(post.getTitle())
                                .description(post.getDescription())                                
                                .acreage(post.getAcreage())
                                .bedrooms(post.getBedrooms())
                                .bathrooms(post.getBathrooms())
                                .furniture(post.getFurniture() != null ? post.getFurniture().getValue() : null)
                                .legal(post.getLegal() != null ? post.getLegal().getValue() : null)                                
                                .price(post.getPrice())
                                .provinceCode(post.getProvinceCode())
                                .districtCode(post.getDistrictCode())
                                .wardCode(post.getWardCode())
                                .address(post.getAddress())
                                .createdDate(post.getCreatedDate())
                                .updatedDate(post.getUpdatedDate())
                                .expiredAt(post.getExpiredAt())
                                .status(post.getStatus().toString())
                                .type(post.getType() != null ? post.getType().name() : null)
                                .floors(post.getFloors())
                                .frontage(post.getFrontage())
                                .direction(post.getDirection() != null ? post.getDirection().name() : null)
                                .transactionType(post.getTransactionType() != null ? post.getTransactionType().name() : null)
                                .images(imagesDtos)
                                .user(userDto)
                                .build();
            postDtos.add(postDTO);
        }
        
        return new PageImpl<>(postDtos, pageable, postsPage.getTotalElements());
    }

    @Transactional
    public void reupPost(String postId) throws Exception{
        Optional<Ranking> rankingOpt = rankingRepository.findByPostId(postId);
        if(rankingOpt.isPresent()){
            Ranking ranking = rankingOpt.get();
            Posts post = ranking.getPost();
            List<UserBalances> userBalances = post.getUser().getUserBalances();
            
            // Check if userBalances is null or empty
            if (userBalances == null || userBalances.isEmpty()) {
                throw new Exception("User has no balance records.");
            }
            
            // Filter out null balances and sort: PROMO first, MAIN last
            userBalances = userBalances.stream()
                .filter(balance -> balance != null && balance.getBalanceType() != null && balance.getBalance() != null)
                .sorted(Comparator.comparing(UserBalances::getBalanceType, 
                    (type1, type2) -> {
                        if (type1 == UserBalances.BalanceType.PROMO && type2 == UserBalances.BalanceType.MAIN) return -1;
                        if (type1 == UserBalances.BalanceType.MAIN && type2 == UserBalances.BalanceType.PROMO) return 1;
                        return 0;
                    }))
                .collect(java.util.stream.Collectors.toList());
                
            Double totalBalance = userBalances.stream()
                                        .mapToDouble(UserBalances::getBalance)
                                        .sum();
            Optional<PostChargeFees> reupFee = postChargeFeesRepo.findByPriorityLevel(ranking.getPriorityLevel());
            if(reupFee.isPresent()){
                Double fee = reupFee.get().getReupFee().doubleValue();
                if(totalBalance >= fee){
                    // Deduct balance from user's balances
                    Double remainingFee = fee;
                    for(UserBalances balance : userBalances){
                        if(remainingFee <= 0) break;
                        if(balance.getBalance() >= remainingFee){
                            balance.setBalance(balance.getBalance() - remainingFee);
                            remainingFee = 0.0;
                        }else{
                            remainingFee -= balance.getBalance();
                            balance.setBalance(0.0);
                        }
                        // Save updated balance
                        userBalancesRepository.save(balance);
                    }
                    // Update bump time
                    ranking.setBumpTime(LocalDateTime.now());
                    rankingRepository.save(ranking);
                }else{
                    throw new Exception("Insufficient balance to reup the post.");
                }
            }else{
                throw new Exception("Reup fee not found for priority level: " + ranking.getPriorityLevel());
            }
        }else{
            throw new Exception("Post not found with ID: " + postId);
        }
    }

    @Transactional
    public void renewPost(String postId) throws Exception{
        Optional<Ranking> rankingOpt = rankingRepository.findByPostId(postId);
        if(rankingOpt.isPresent()){
            Ranking ranking = rankingOpt.get();
            Posts post = ranking.getPost();
            if(post.getStatus() != PostStatus.EXPIRED){
                throw new Exception("Only expired posts can be renewed.");

            }

            List<UserBalances> userBalances = post.getUser().getUserBalances();
            
            // Check if userBalances is null or empty
            if (userBalances == null || userBalances.isEmpty()) {
                throw new Exception("User has no balance records.");
            }
            
            // Filter out null balances and sort: PROMO first, MAIN last
            userBalances = userBalances.stream()
                .filter(balance -> balance != null && balance.getBalanceType() != null && balance.getBalance() != null)
                .sorted(Comparator.comparing(UserBalances::getBalanceType, 
                    (type1, type2) -> {
                        if (type1 == UserBalances.BalanceType.PROMO && type2 == UserBalances.BalanceType.MAIN) return -1;
                        if (type1 == UserBalances.BalanceType.MAIN && type2 == UserBalances.BalanceType.PROMO) return 1;
                        return 0;
                    }))
                .collect(java.util.stream.Collectors.toList());
                
            Double totalBalance = userBalances.stream()
                                        .mapToDouble(UserBalances::getBalance)
                                        .sum();
            Optional<PostChargeFees> renewFee = postChargeFeesRepo.findByPriorityLevel(ranking.getPriorityLevel());
            if(renewFee.isPresent()){
                Double fee = renewFee.get().getRenewFee().doubleValue();
                if(totalBalance >= fee){
                    // Deduct balance from user's balances
                    Double remainingFee = fee;
                    for(UserBalances balance : userBalances){
                        if(remainingFee <= 0) break;
                        if(balance.getBalance() >= remainingFee){
                            balance.setBalance(balance.getBalance() - remainingFee);
                            remainingFee = 0.0;
                        }else{
                            remainingFee -= balance.getBalance();
                            balance.setBalance(0.0);
                        }
                        // Save updated balance
                        userBalancesRepository.save(balance);
                    }
                    // Update bump time
                    ranking.setBumpTime(LocalDateTime.now());
                    rankingRepository.save(ranking);

                    // Update expired date
                    post.setUpdatedDate(LocalDateTime.now().plusDays(30));
                    post.setExpiredAt(LocalDateTime.now().plusDays(30)); // Set expiration to 30 days from now
                    post.setStatus(PostStatus.PUBLISHED); // Set status to ACTIVE
                    postRepository.save(post);
                }else{
                    throw new Exception("Insufficient balance to renew the post.");
                }
            }else{
                throw new Exception("Renew fee not found for priority level: " + ranking.getPriorityLevel());
            }
        }else{
            throw new Exception("Post not found with ID: " + postId);
        }
    }

    public com.realestate.dto.PostChargeFeeDto getPostChargeFee(String postId) throws Exception{
        Optional<Ranking> rankingOpt = rankingRepository.findByPostId(postId);
        if(rankingOpt.isPresent()){
            Ranking ranking = rankingOpt.get();
            Optional<PostChargeFees> chargeFeeOpt = postChargeFeesRepo.findByPriorityLevel(ranking.getPriorityLevel());
            if(chargeFeeOpt.isPresent()){
                PostChargeFees chargeFee = chargeFeeOpt.get();
                com.realestate.dto.PostChargeFeeDto chargeFeeDto = com.realestate.dto.PostChargeFeeDto.builder()
                                                        .postId(postId)
                                                        .priorityLevel(ranking.getPriorityLevel().name())
                                                        .reupFee(chargeFee.getReupFee().doubleValue())
                                                        .renewFee(chargeFee.getRenewFee().doubleValue())
                                                        .status(chargeFee.getStatus())
                                                        .build();
                return chargeFeeDto;
            }else{
                throw new Exception("Charge fee not found for priority level: " + ranking.getPriorityLevel());
            }
        }else{
            throw new Exception("Post not found with ID: " + postId);
        }
    }

    @Transactional
    @Override
    public PostDto editPost(PostDto postDto) throws Exception{
        // to be implemented
        if(postDto.getPostId() != null){
            // Fetch existing post
            Optional<Posts> postOpt = postRepository.findById(postDto.getPostId());
            if(postOpt.isPresent()){
                Posts post = postOpt.get();
                // Update fields
                post.setTitle(postDto.getTitle());
                post.setDescription(postDto.getDescription());
                post.setAcreage(postDto.getAcreage());
                post.setBedrooms(postDto.getBedrooms());
                post.setBathrooms(postDto.getBathrooms());
                post.setFurniture(EnumUtils.fromString(FurnitureType.class, postDto.getFurniture()));
                post.setLegal(EnumUtils.fromString(LegalType.class, postDto.getLegal()));
                post.setPrice(postDto.getPrice());
                post.setProvinceCode(postDto.getProvinceCode());                
                post.setWardCode(postDto.getWardCode());
                post.setAddress(postDto.getAddress());
                post.setFloors(postDto.getFloors());
                post.setFrontage(postDto.getFrontage());
                post.setDirection(EnumUtils.fromString(Direction.class, postDto.getDirection()));
                post.setType(EnumUtils.fromString(Type.class, postDto.getType()));
                // Save updated post
                Posts updatedPost = postRepository.save(post);

                List<ImagesDto> imagesDtos = postDto.getImages();
                for (ImagesDto imagesDto : imagesDtos) {
                    try{
                        if(imagesDto.getUpdatedType() == ImagesDto.UpdatedType.ADD){
                            // Add new image
                            Images newImage = Images.builder()
                                            .fileUrl(imagesDto.getFileUrl())
                                            .fileName(imagesDto.getFileName())
                                            .post(updatedPost)
                                            .isPrimary(imagesDto.getIsPrimary())                
                                            .build();
                            imageRepository.save(newImage);
                        }else if(imagesDto.getUpdatedType() == ImagesDto.UpdatedType.DELETE){
                            // Delete image
                            if(imagesDto.getImageId() != null){
                                imageRepository.updateImageStatus(imagesDto.getImageId(), false);
                            }
                        }else if(imagesDto.getUpdatedType() == ImagesDto.UpdatedType.UPDATE){
                            // Update existing image
                            Optional <Images> imageOpt = imageRepository.findPrimaryImage(updatedPost.getPostId());
                            if(imageOpt.isPresent()){
                                Images imageToUpdate = imageOpt.get();                                
                                imageToUpdate.setIsPrimary(false);
                                imageRepository.save(imageToUpdate);

                                Images imageNewPrimary = imageRepository.getReferenceById(imagesDto.getImageId());
                                imageNewPrimary.setIsPrimary(true);
                                imageRepository.save(imageNewPrimary);
                            }
                        }
                    }catch(Exception e){
                        // Log error and continue
                        System.out.println("Error processing image update: " + e.getMessage());
                    }                    
                }                
                // Convert to PostDto to return
                List<ImagesDto> imageJPA = updatedPost.getImages().stream().map(img -> ImagesDto.builder()
                                                        .imageId(img.getImageId())
                                                        .postId(updatedPost.getPostId())
                                                        .fileUrl(img.getFileUrl())
                                                        .fileName(img.getFileName())
                                                        .isPrimary(img.getIsPrimary())
                                                        .build()).toList();
                                                                      
                PostDto updatedPostDto = PostDto.builder()
                                        .postId(updatedPost.getPostId())
                                        .userId(updatedPost.getUser().getUserId())                                
                                        .title(updatedPost.getTitle())
                                        .description(updatedPost.getDescription())                                
                                        .acreage(updatedPost.getAcreage())
                                        .bedrooms(updatedPost.getBedrooms())
                                        .bathrooms(updatedPost.getBathrooms())
                                        .furniture(updatedPost.getFurniture() != null ? updatedPost.getFurniture().getValue() : null)
                                        .legal(updatedPost.getLegal() != null ? updatedPost.getLegal().getValue() : null)                                
                                        .price(updatedPost.getPrice())
                                        .provinceCode(updatedPost.getProvinceCode())
                                        .districtCode(updatedPost.getDistrictCode())
                                        .wardCode(updatedPost.getWardCode())
                                        .address(updatedPost.getAddress())
                                        .createdDate(updatedPost.getCreatedDate())
                                        .updatedDate(updatedPost.getUpdatedDate())
                                        .expiredAt(updatedPost.getExpiredAt())
                                        .status(updatedPost.getStatus().toString())
                                        .type(updatedPost.getType() != null ? updatedPost.getType().name() : null)
                                        .floors(updatedPost.getFloors())
                                        .frontage(updatedPost.getFrontage())
                                        .direction(updatedPost.getDirection() != null ? updatedPost.getDirection().name() : null)
                                        .images(imageJPA)
                                        .build();
                return updatedPostDto;
            }else{
                throw new Exception("Post not found with ID: " + postDto.getPostId());
            }
        }else{
            throw new Exception("Post ID is required for editing.");
        }
    }
}
