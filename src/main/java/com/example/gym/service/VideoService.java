package com.example.gym.service;
import com.example.gym.entity.Video;
import com.example.gym.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;


    public List<Video> getAllVideos(){
        return videoRepository.findAllByOrderByOrderIndexAsc();
    }

    public Video createVideo(Video video) {
        return videoRepository.save(video);
    }

    public Video updateVideo(Long id, Video videoDetails){
        Video video = videoRepository.findById(id).orElseThrow(()->new RuntimeException("Video not found"));


        video.setTitle(videoDetails.getTitle());
        video.setDescription(videoDetails.getDescription());
        video.setUrl(videoDetails.getUrl());
        video.setOrderIndex(videoDetails.getOrderIndex());
        video.setMinSubscription(videoDetails.getMinSubscription());

        return videoRepository.save(video);
    }
    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }
}
