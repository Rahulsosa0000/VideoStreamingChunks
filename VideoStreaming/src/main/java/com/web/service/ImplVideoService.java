//package com.web.service;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.web.model.Video;
//import com.web.repo.VideoRepo;
//
//import jakarta.annotation.PostConstruct;
//
//@Service
//public class ImplVideoService implements VideoService{
//	
//	@Value("${files.video}")
//	String DIR;
//	
//	@Autowired
//	private VideoRepo videoRepo;
//	
//	@PostConstruct
//	public void init() {
//		File file= new File(DIR);
//		if(!file.exists()) {
//			file.mkdir();
//			System.out.println("Folder created...");
//		}else {
//			System.out.println("Folder Alredy Exist..");
//		}
//	}
//
//	 @Override
//	    public Video save(Video video, MultipartFile file) {
//	        try {
//	            String filename = StringUtils.cleanPath(file.getOriginalFilename());
//	            String cleanFolder = StringUtils.cleanPath(DIR);
//
//	            // Ensure directory exists
//	            File directory = new File(cleanFolder);
//	            if (!directory.exists()) {
//	                directory.mkdirs();
//	            }
//
//	            Path path = Paths.get(cleanFolder, filename);
//	            System.out.println("Saving file to: " + path);
//
//	            // Copy file to target directory
//	            try (InputStream inputStream = file.getInputStream()) {
//	                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
//	            }
//
//	            // Save metadata in database
//	            video.setContentType(file.getContentType());
//		        String normalizedPath = path.toString().replace("\\", "/"); 
//
//	            video.setFilePath(normalizedPath);
//
//	            return videoRepo.save(video); 
//
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	            return null;
//	        }
//	    }
//
//	@Override
//	public Video get(String videoId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//}



package com.web.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.web.model.Video;
import com.web.repo.VideoRepo;

import jakarta.annotation.PostConstruct;

@Service
public class ImplVideoService implements VideoService {
    
    @Value("${files.video}")
    private String DIR;
    
    @Autowired
    private VideoRepo videoRepo;
    
    @PostConstruct
    public void init() {
        try {
            File file = new File(DIR);
            if (!file.exists()) {
                if (file.mkdirs()) {
                    System.out.println(" Folder created successfully: " + DIR);
                } else {
                    System.out.println(" Warning: Could not create folder.");
                }
            } else {
                System.out.println("Folder already exists: " + DIR);
            }
        } catch (Exception e) {
            System.out.println(" Error initializing folder: " + e.getMessage());
        }
    }

    @Override
    public Video save(Video video, MultipartFile file) {
        try {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
          //  String cleanFolder = Paths.get(DIR).toAbsolutePath().normalize().toString();
            String cleanFolder = StringUtils.cleanPath(DIR);

            
            // Debugging logs
            System.out.println(" Target Directory: " + cleanFolder);
            System.out.println(" File Name: " + filename);
            
            // Ensure directory exists
            File directory = new File(cleanFolder);
            if (!directory.exists() && !directory.mkdirs()) {
                System.out.println("Warning: Could not create directory.");
                return null;
            }

            Path path = Paths.get(cleanFolder, filename);
            System.out.println("Saving file to: " + path);

            // Copy file to target directory
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            // Save metadata in database
            video.setContentType(file.getContentType());
            video.setFilePath(path.toString().replace("\\", "/"));

            System.out.println("File uploaded successfully: " + filename);
            return videoRepo.save(video);

        } catch (IOException e) {
            System.out.println(" Error saving file: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println(" Unexpected error: " + e.getMessage());
            return null;
        }
    }

//    @Override
//    public Video get(String videoId) {
//    	
//    	Video video =videoRepo.findById(videoId)
//    			.orElseThrow(()-> new RuntimeException("video not found")); 
//    	return video;
//    }
    
    @Override
    public Video get(String videoId) {
//        Video video = videoRepo.findById(videoId)
//                .orElseThrow(() -> new RuntimeException("Video not found for ID: " + videoId));
//        
//        // Check if the file exists at the given path
//        File videoFile = new File(video.getFilePath());
//        if (!videoFile.exists()) {
//            throw new RuntimeException("Video file not found at path: " + video.getFilePath());
//        }
//
//        return video;
    	
    	Video video = videoRepo.findByVideoId(videoId).orElseThrow(() -> new RuntimeException("Video Not Found"));
        return video;
    }


	@Override
	public List<Video> getAll() {
		// TODO Auto-generated method stub
		return videoRepo.findAll();
	}
}

