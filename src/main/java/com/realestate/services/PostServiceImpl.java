package com.realestate.services;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.realestate.dto.ImagesDto;
import com.realestate.dto.PostDto;
import com.realestate.dto.RankingDto;
import com.realestate.models.Images;
import com.realestate.models.Posts;
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
                            .provinceCode(postDto.getProvinceCode()).districtCode(postDto.getDistrictCode())
                            .wardCode(postDto.getWardCode()).address(postDto.getAddress())                            
                            .status(EnumUtils.fromString(PostStatus.class, postDto.getStatus()))
                            .floors(postDto.getFloors())
                            .direction(EnumUtils.fromString(Direction.class, postDto.getDirection()))
                            .type(EnumUtils.fromString(Type.class, postDto.getType()))
                        .build();

        Posts savedPost =  postRepository.save(post);                        
        
        List<ImagesDto> imagesDtos = postDto.getImages();
        List<Images> imageJPAsList = new ArrayList<Images>();
        for (ImagesDto imagesDto : imagesDtos) {
            Images image = Images.builder()
                            .filePath(imagesDto.getFilePath()).fileName(imagesDto.getFileName())
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

            images.forEach(img -> {
                ImagesDto imgDTO = ImagesDto.builder()
                                    .imageId(img.getImageId())
                                    .postId(postId)
                                    .filePath(img.getFilePath())
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
                                .furniture(model.getFurniture().getValue())
                                .legal(model.getLegal().getValue())                                
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
                                .direction(model.getDirection().name())
                                .images(imagesDtos)
                                .build();
            return postDTO;
        }else{
            return null;
        }
        
    }
}
