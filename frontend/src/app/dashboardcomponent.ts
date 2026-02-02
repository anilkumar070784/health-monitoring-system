import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface TurbineHealth {
  id: number;
  code: string;
  farmCode: string;
  region: string;
  status: string | null;
  latestPowerKw: number | null;
  latestWindSpeedMs: number | null;
  latestRotorSpeedRpm: number | null;
  lastUpdatedAt: string | null;
  lastHourEnergyKwh: number | null;
  anomalous: boolean | null;
}

interface HealthAlert {
  id: number;
  turbineId: number;
  turbineCode: string;
  severity: string;
  type: string;
  message: string;
  createdAt: string;
  acknowledged: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section>
      <div class="filters-bar">
        <select [(ngModel)]="regionFilter" (ngModelChange)="loadTurbines()">
          <option value="">All regions</option>
          <option *ngFor="let r of regions" [value]="r">{{ r }}</option>
        </select>
        <select [(ngModel)]="farmFilter" (ngModelChange)="loadTurbines()">
          <option value="">All farms</option>
          <option *ngFor="let f of farms" [value]="f">{{ f }}</option>
        </select>
      </div>

      <div class="grid">
        <div class="card">
          <div class="card-header">
            <div class="card-title">Turbines</div>
            <span class="badge badge-primary">{{ turbines.length }} assets</span>
          </div>
          <table class="turbine-table" *ngIf="turbines.length; else empty">
            <thead>
            <tr>
              <th>Code</th>
              <th>Farm</th>
              <th>Region</th>
              <th>Status</th>
              <th>Power (kW)</th>
              <th>Wind (m/s)</th>
            </tr>
            </thead>
            <tbody>
            <tr *ngFor="let t of turbines"
                class="turbine-row"
                (click)="selectTurbine(t)"
                [class.selected]="t.id === selectedTurbine?.id">
              <td>{{ t.code }}</td>
              <td>{{ t.farmCode }}</td>
              <td>{{ t.region }}</td>
              <td>
                <span class="status-chip"
                      [ngClass]="statusClass(t.status)">
                  {{ t.status || 'N/A' }}
                </span>
              </td>
              <td>{{ t.latestPowerKw ?? '—' }}</td>
              <td>{{ t.latestWindSpeedMs ?? '—' }}</td>
            </tr>
            </tbody>
          </table>
          <ng-template #empty>
            <p>No turbines found. Seed some data or check filters.</p>
          </ng-template>
        </div>

        <div class="card" *ngIf="selectedTurbine">
          <div class="card-header">
            <div class="card-title">
              {{ selectedTurbine.code }} &middot; Real-time health
            </div>
            <span class="badge"
                  [ngClass]="selectedTurbine.anomalous ? 'badge-critical' : 'badge-primary'">
              {{ selectedTurbine.anomalous ? 'Anomaly detected' : 'Healthy' }}
            </span>
          </div>

          <div class="metrics-grid">
            <div class="metric-pill">
              <div class="metric-label">Power</div>
              <div class="metric-value">
                {{ selectedTurbine.latestPowerKw ?? '—' }} kW
              </div>
              <div class="metric-sub">Last updated {{ selectedTurbine.lastUpdatedAt || '—' }}</div>
            </div>

            <div class="metric-pill">
              <div class="metric-label">Wind speed</div>
              <div class="metric-value">
                {{ selectedTurbine.latestWindSpeedMs ?? '—' }} m/s
              </div>
              <div class="metric-sub">Rotor {{ selectedTurbine.latestRotorSpeedRpm ?? '—' }} rpm</div>
            </div>

            <div class="metric-pill">
              <div class="metric-label">Last hour energy</div>
              <div class="metric-value">
                {{ selectedTurbine.lastHourEnergyKwh ?? '—' }} kWh
              </div>
              <div class="metric-sub">Hourly aggregate</div>
            </div>

            <div class="metric-pill">
              <div class="metric-label">Region / Farm</div>
              <div class="metric-value">
                {{ selectedTurbine.region }} / {{ selectedTurbine.farmCode }}
              </div>
              <div class="metric-sub">Asset metadata</div>
            </div>
          </div>

          <div class="card-header" style="margin-top: 1rem;">
            <div class="card-title">Recent alerts</div>
          </div>

          <div class="alerts-list" *ngIf="alerts.length; else noAlerts">
            <div class="alert-item" *ngFor="let a of alerts">
              <div class="alert-severity"
                   [ngClass]="alertSeverityClass(a.severity)">
                {{ a.severity }} &middot; {{ a.type }}
              </div>
              <div class="alert-message">
                {{ a.message }}
              </div>
              <div class="alert-meta">
                {{ a.createdAt }} &middot;
                {{ a.acknowledged ? 'Acknowledged' : 'Unacknowledged' }}
              </div>
            </div>
          </div>
          <ng-template #noAlerts>
            <p>No recent alerts for this turbine.</p>
          </ng-template>
        </div>
      </div>
    </section>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  apiBase = 'http://localhost:8080/api';

  regionFilter = '';
  farmFilter = '';
  regions: string[] = ['NORTH', 'SOUTH', 'EAST', 'WEST'];
  farms: string[] = []; // can be populated via API later

  turbines: TurbineHealth[] = [];
  selectedTurbine: TurbineHealth | null = null;
  alerts: HealthAlert[] = [];

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    this.loadTurbines();
  }

  loadTurbines(): void {
    const params: string[] = [];
    if (this.regionFilter) {
      params.push(`region=${encodeURIComponent(this.regionFilter)}`);
    }
    if (this.farmFilter) {
      params.push(`farmCode=${encodeURIComponent(this.farmFilter)}`);
    }

    const query = params.length ? `?${params.join('&')}` : '';

    this.http.get<TurbineHealth[]>(`${this.apiBase}/monitoring/turbines${query}`)
      .subscribe({
        next: data => {
          this.turbines = data;
          if (!this.selectedTurbine && this.turbines.length) {
            this.selectTurbine(this.turbines[0]);
          }
        },
        error: err => console.error('Error loading turbines', err)
      });
  }

  selectTurbine(turbine: TurbineHealth): void {
    this.selectedTurbine = turbine;
    this.http.get<HealthAlert[]>(`${this.apiBase}/monitoring/alerts?turbineId=${turbine.id}`)
      .subscribe({
        next: data => this.alerts = data,
        error: err => console.error('Error loading alerts', err)
      });
  }

  statusClass(status: string | null | undefined): string {
    const s = (status || '').toUpperCase();
    if (s === 'OK') {
      return 'status-ok';
    }
    if (s === 'WARNING') {
      return 'status-warning';
    }
    if (s === 'FAULT' || s === 'CRITICAL') {
      return 'status-fault';
    }
    return '';
  }

  alertSeverityClass(severity: string): string {
    const s = (severity || '').toUpperCase();
    if (s === 'CRITICAL') {
      return 'alert-severity-critical';
    }
    if (s === 'WARNING') {
      return 'alert-severity-warning';
    }
    return '';
  }
}

