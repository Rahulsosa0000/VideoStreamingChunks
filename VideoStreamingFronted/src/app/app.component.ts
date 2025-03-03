import { Component } from '@angular/core';
import { VideoStreamComponent } from "./components/video-stream/video-stream.component";

@Component({
  selector: 'app-root',
  imports: [ VideoStreamComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'videoStreaming';
}
