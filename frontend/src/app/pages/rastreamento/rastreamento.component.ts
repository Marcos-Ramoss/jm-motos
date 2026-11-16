import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrackingService } from '../../core/services/tracking.service';
import { TrackingStatus } from '../../core/models/tracking-status.model';

@Component({
  selector: 'app-rastreamento',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rastreamento.component.html',
  styleUrl: './rastreamento.component.css'
})
export class RastreamentoComponent implements OnInit {
  private trackingService = inject(TrackingService);

  trackingData: TrackingStatus | null = null;
  loading = false;

  ngOnInit(): void {
    this.loadStatus();
  }

  loadStatus(): void {
    this.loading = true;
    this.trackingService.getStatus().subscribe({
      next: (res) => {
        this.trackingData = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
