import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VideoserviceService {

  private baseUrl = 'http://localhost:8080/api/video/stream/range'; 

  constructor(private http: HttpClient) {}

  getVideoChunk(videoId: string, start: number, end: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      'Range': `bytes=${start}-${end}`
    });

    return this.http.get(`${this.baseUrl}/${videoId}`, { 
      headers, 
      responseType: 'blob',
      observe: 'response' 
    });
  }
}
