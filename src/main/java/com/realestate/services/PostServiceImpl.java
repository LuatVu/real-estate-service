package com.realestate.services;


import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.realestate.models.Posts.Direction;
import com.realestate.models.Posts.FurnitureType;
import com.realestate.models.Posts.LegalType;
import com.realestate.models.Posts.PostStatus;
import com.realestate.models.Posts.Type;
import com.realestate.models.Ranking.PriorityLevel;
import com.realestate.repositories.ImageRepository;
import com.realestate.repositories.PostRepository;
import com.realestate.repositories.RankingRepository;
import com.realestate.repositories.UserRepository;
import com.realestate.utilities.EnumUtils;

import jakarta.transaction.Transactional;

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
}
