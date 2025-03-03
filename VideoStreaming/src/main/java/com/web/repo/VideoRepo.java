package com.web.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Video;

@Repository
public interface VideoRepo extends JpaRepository<Video, String> {

    Optional<Video> findByVideoId(String videoId);

}
