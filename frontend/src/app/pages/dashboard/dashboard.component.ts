import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardSummary } from '../../core/models/dashboard-summary.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);

  summary: DashboardSummary | null = null;
  loading = false;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.dashboardService.getSummary().subscribe({
      next: (res) => {
        this.summary = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
