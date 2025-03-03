package com.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.model.Video;
import com.web.msg.CustomMessage;
import com.web.service.VideoService;

@RestController
@RequestMapping("/api/video")
//@CrossOrigin("*")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("title") String title,
			@RequestParam("description") String description) {
		try {
			Video video = new Video();
			video.setTitle(title);
			video.setDescription(description);
			video.setVideoId(UUID.randomUUID().toString());

			Video savedVideo = videoService.save(video, file);

			if (savedVideo != null) {
				return ResponseEntity.ok(savedVideo);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new CustomMessage("Video is not uploaded", false));
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new CustomMessage("Something went wrong: " + e.getMessage(), false));
		}
	}

	@GetMapping("/stream/{videoId}")
	public ResponseEntity<Resource> stream(@PathVariable String videoId) {

		Video video = videoService.get(videoId);
		String contentType = video.getContentType();
		String filePath = video.getFilePath();

		Resource resource = new FileSystemResource(filePath);

		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);

	}

	@GetMapping("/getAll")
	public List<Video> getAll() {
		return videoService.getAll();

	}

	
	// Stream Videos in Chunks
	public static final int CHUNK_SIZE = 1024 * 1024;
	@SuppressWarnings("resource")
	@GetMapping("/stream/range/{videoId}")
	public ResponseEntity<Resource> streamVideoRange(@PathVariable("videoId") String videoId,
			@RequestHeader(value = "Range", required = false) String range

	) {
		System.out.println(range);
		Video video = videoService.get(videoId);
		Path path = Paths.get(video.getFilePath());
		Resource resource = new FileSystemResource(path);   //new FileSystemResource(path): Loads the file as a resource.
		String contentType = video.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";

		}

		// File length
		long fileLength = path.toFile().length();

		if (range == null) {
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);

		}

		long rangeStart;
		long rangeEnd;
		String[] ranges = range.replace("bytes=", "").split("-"); //Removes "bytes=" from the header and splits it into an array.

		rangeStart = Long.parseLong(ranges[0]);  // range  start  from  0

		rangeEnd = rangeStart + CHUNK_SIZE;   // rangeend =  0 + 1mb;
		if (rangeEnd >= fileLength) {    // 
			rangeEnd = fileLength - 1;
		}

		System.out.println("Start Range" + rangeStart);
		System.out.println("Start End" + rangeEnd);
		InputStream inputStream;
		try {

			inputStream = Files.newInputStream(path);
			long skip = inputStream.skip(rangeStart);
			System.out.println("skip:-" + skip);  // Skips to rangeStart position.

			long contentLength = rangeEnd - rangeStart + 1;

			byte[] data = new byte[(int) contentLength];  //Reads only the required bytes into the data array.
														 //  only  chunks  data read 
			int read = inputStream.read(data, 0, data.length);
			System.out.println("read no of bytes " + read);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
			//Content-Range: Tells the browser which bytes are sent (206 Partial Content).
			headers.add("Cache-Control", "no-cache , no-store, must-revalidate");
			headers.add("Pragma", "no-cache"); //Cache-Control, Pragma, Expires: Prevents caching.
			headers.add("Expires", "0");
			headers.add("X-Content-Type-Options", "nosniff");
			headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");

			headers.setContentLength(contentLength);
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers)
					.contentType(MediaType.parseMediaType(contentType)).body(new ByteArrayResource(data));
//Sends the chunk as a byte array (ByteArrayResource(data)).
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}
	

}
