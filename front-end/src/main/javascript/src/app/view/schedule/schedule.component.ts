import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Track} from "../../model/track";
import {ScheduleService} from "./schedule.service";
import {Observable} from "rxjs/index";

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['schedule.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ScheduleComponent implements OnInit {

  private scheduleService: ScheduleService;

  currentTrack: Track;
  tracks: Track[] = [];

  constructor(scheduleService: ScheduleService) {
    this.scheduleService = scheduleService;
  }

  ngOnInit(): void {
    this.refreshSchedule();
  }

  refreshSchedule(): void {
    this.getCurrentTrack().subscribe(track => this.currentTrack = track);
    this.getTracks().subscribe(tracks => this.tracks = tracks);
  }

  getCurrentTrack(): Observable<Track> {
    return this.scheduleService.getCurrentTrack();
  }

  getTracks(): Observable<Track[]> {
    return this.scheduleService.getUpcommingTracks();
  }

  calculateDurationString(durationMs: number): string {
    let minutes: number = Math.floor(durationMs / (1000 * 60));
    let seconds: number = Math.floor(durationMs - (minutes * 1000 * 60)) / 1000;
    return minutes + ":" + this.pad(seconds, 2);
  }

  private pad(number: number, size: number): string {
    var s = number + "";
    while (s.length < size) s = "0" + s;
    return s;
  }
}
