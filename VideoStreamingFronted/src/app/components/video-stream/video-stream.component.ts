
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-video',
  templateUrl: './video-stream.component.html',
  styleUrls: ['./video-stream.component.css']
})
export class VideoStreamComponent implements OnInit {
  @ViewChild('videoPlayer') videoPlayer!: ElementRef<HTMLVideoElement>;
  videoUrl: SafeUrl | null = null;
  videoId: string = '2542c662-a123-4092-86b7-5a7b40ee973c';  

  constructor(private http: HttpClient, private sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    this.loadVideo(this.videoId);  
  }

  loadVideo(videoId: string) {
    const videoStreamUrl = `http://localhost:8080/api/video/stream/range/${videoId}`;
    console.log("Fetching video from URL:", videoStreamUrl);  
    this.videoUrl = this.sanitizer.bypassSecurityTrustUrl(videoStreamUrl);
  }
}

