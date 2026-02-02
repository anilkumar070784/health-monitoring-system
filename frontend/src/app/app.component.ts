import { Component, OnInit } from '@angular/core';
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

interface FarmSummary {
  id: number;
  code: string;
  name: string;
  region: string;
  turbineCount: number;
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

type Role =
  | 'OPS'
  | 'SUPERVISOR'
  | 'ANALYST'
  | 'MAINT'
  | 'RELIABILITY'
  | 'ADMIN';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <!-- LOGIN PAGE -->
    <div *ngIf="!isLoggedIn" class="login-shell">
      <div class="login-card">
        <h2>Wind Turbine Monitoring</h2>
        <p class="login-subtitle">Sign in to access your dashboard</p>

        <div class="login-field">
          <label>Username</label>
          <input [(ngModel)]="loginUsername" autocomplete="off" />
        </div>

        <div class="login-field">
          <label>Password</label>
          <input [(ngModel)]="loginPassword" type="password" />
        </div>

        <button class="login-btn" (click)="login()">Login</button>

        <p *ngIf="loginError" class="login-error">{{ loginError }}</p>
      </div>
    </div>

    <!-- DASHBOARD -->
    <div *ngIf="isLoggedIn" class="page">
      <!-- Header -->
      <header class="app-header">
        <div class="app-title">Wind Turbine Health Monitoring</div>
        <div class="badge badge-primary">
          {{ currentRole }} · {{ loginUsername }}
        </div>
        <button class="logout-btn" (click)="logout()">Logout</button>
      </header>

      <!-- Main content, centered -->
      <main class="app-main">
        <div class="app-main-inner">

          <!-- Filters row -->
          <div class="filters-bar">
            <label>
              Region:
              <select [(ngModel)]="selectedRegion" (change)="onRegionChange()">
                <option value="">All</option>
                <option *ngFor="let r of regions" [value]="r">{{ r }}</option>
              </select>
            </label>

            <label>
              Farm:
              <select [(ngModel)]="selectedFarm" (change)="loadTurbines()">
                <option value="">All</option>
                <option *ngFor="let f of filteredFarms" [value]="f.code">
                  {{ f.code }} ({{ f.region }})
                </option>
              </select>
            </label>

            <label class="page-size-label">
              Page size:
              <select [(ngModel)]="pageSize" (change)="onPageSizeChange()">
                <option *ngFor="let s of pageSizes" [value]="s">{{ s }}</option>
              </select>
            </label>
          </div>

          <div class="grid">
            <!-- Turbines card -->
            <div class="card">
              <div class="card-header">
                <div class="card-title">Turbines</div>
                <span class="badge badge-primary">
                  {{ turbines.length }} assets
                </span>
              </div>
              <table class="turbine-table" *ngIf="pagedTurbines.length; else noTurbines">
                <thead>
                <tr>
                  <th>Code</th>
                  <th>Farm</th>
                  <th>Region</th>
                  <th>Status</th>
                  <th>Power (kW)</th>
                  <th>Wind (m/s)</th>
                  <th>Anomalous</th>
                </tr>
                </thead>
                <tbody>
                <tr *ngFor="let t of pagedTurbines" class="turbine-row">
                  <td>{{ t.code }}</td>
                  <td>{{ t.farmCode }}</td>
                  <td>{{ t.region }}</td>
                  <td>
                    <span class="status-chip" [ngClass]="statusClass(t.status)">
                      {{ t.status || 'N/A' }}
                    </span>
                  </td>
                  <td>{{ t.latestPowerKw ?? '—' }}</td>
                  <td>{{ t.latestWindSpeedMs ?? '—' }}</td>
                  <td>{{ t.anomalous ? 'YES' : 'NO' }}</td>
                </tr>
                </tbody>
              </table>
              <ng-template #noTurbines>
                <p>No turbines found.</p>
              </ng-template>

              <!-- Pagination controls -->
              <div class="pagination" *ngIf="totalPages > 1">
                <button (click)="prevPage()" [disabled]="currentPage === 1">
                  ‹ Prev
                </button>
                <span>
                  Page {{ currentPage }} / {{ totalPages }}
                </span>
                <button (click)="nextPage()" [disabled]="currentPage === totalPages">
                  Next ›
                </button>
              </div>
            </div>

            <!-- Alerts card (OPS / RELIABILITY / ADMIN) -->
            <div class="card alerts-card" *ngIf="hasAnyRole('OPS','RELIABILITY','ADMIN')">
              <div class="card-header">
                <div class="card-title">Recent alerts</div>
              </div>

              <div class="alerts-list" *ngIf="filteredAlerts.length; else noAlerts">
                <div class="alert-item" *ngFor="let a of filteredAlerts">
                  <div class="alert-severity" [ngClass]="alertSeverityClass(a.severity)">
                    {{ a.severity }} · {{ a.type }}
                  </div>
                  <div class="alert-message">
                    {{ a.message }}
                  </div>
                  <div class="alert-meta">
                    {{ a.createdAt | date:'short' }} · {{ a.turbineCode }}
                    · {{ a.acknowledged ? 'ACK' : 'OPEN' }}
                  </div>
                </div>
              </div>

              <ng-template #noAlerts>
                <p>No recent alerts for current filters.</p>
              </ng-template>
            </div>
          </div>

          <!-- Anomalies table (Reliability / Admin) -->
          <div class="card anomalies-card" *ngIf="hasAnyRole('RELIABILITY','ADMIN')">
            <div class="card-header">
              <div class="card-title">Anomalies (Region / Farm scoped)</div>
              <div>
                <label style="font-size: 0.75rem; color: #6b7280;">
                  Severity:
                  <select [(ngModel)]="selectedSeverity">
                    <option *ngFor="let s of severities" [value]="s">{{ s }}</option>
                  </select>
                </label>
              </div>
            </div>

            <table class="turbine-table" *ngIf="anomalyRows.length; else noAnoms">
              <thead>
              <tr>
                <th>Time</th>
                <th>Turbine</th>
                <th>Severity</th>
                <th>Type</th>
                <th>Message</th>
                <th>Status</th>
              </tr>
              </thead>
              <tbody>
              <tr *ngFor="let a of anomalyRows">
                <td>{{ a.createdAt | date:'MM-dd HH:mm' }}</td>
                <td>{{ a.turbineCode }}</td>
                <td>{{ a.severity }}</td>
                <td>{{ a.type }}</td>
                <td>{{ a.message }}</td>
                <td>{{ a.acknowledged ? 'ACK' : 'OPEN' }}</td>
              </tr>
              </tbody>
            </table>

            <ng-template #noAnoms>
              <p>No anomalies for current filters.</p>
            </ng-template>
          </div>

        </div>
      </main>

      <!-- Footer -->
      <footer class="app-footer">
        Wind Turbine Monitoring · © 2026
      </footer>
    </div>
  `
})
export class AppComponent implements OnInit {
  apiBase = 'http://localhost:8080/api';

  // Login state
  isLoggedIn = false;
  loginUsername = '';
  loginPassword = '';
  currentRole: Role = 'OPS';
  loginError = '';

  // Data
  turbines: TurbineHealth[] = [];
  farms: FarmSummary[] = [];
  alerts: HealthAlert[] = [];

  // Filters
  regions: string[] = ['NORTH', 'SOUTH', 'EAST', 'WEST'];
  selectedRegion = '';
  selectedFarm = '';

  // Pagination
  pageSizes: number[] = [10,25, 50, 100];
  pageSize = 10;
  currentPage = 1;

  // Anomaly filter
  severities: string[] = ['ALL', 'CRITICAL', 'WARNING', 'INFO'];
  selectedSeverity = 'ALL';

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {}

  /* Login / logout */

  login(): void {
    const u = (this.loginUsername || '').trim();
    const p = this.loginPassword || '';

    if (!u || !p) {
      this.loginError = 'Please enter username and password.';
      return;
    }

    sessionStorage.setItem('authUser', u);
    sessionStorage.setItem('authPass', p);

    this.http.get<FarmSummary[]>(`${this.apiBase}/monitoring/farms`).subscribe({
      next: data => {
        this.farms = data;
        this.isLoggedIn = true;
        this.loginError = '';

        const roleMap: Record<string, Role> = {
          'ops': 'OPS',
          'supervisor': 'SUPERVISOR',
          'analyst': 'ANALYST',
          'planner': 'MAINT',
          'reliability': 'RELIABILITY',
          'admin': 'ADMIN'
        };
        this.currentRole = roleMap[u.toLowerCase()] || 'OPS';

        this.loadTurbines();
        this.loadAlerts();
      },
      error: err => {
        console.error('Login failed', err);
        this.loginError = 'Invalid credentials or access denied.';
        sessionStorage.removeItem('authUser');
        sessionStorage.removeItem('authPass');
      }
    });
  }

  logout(): void {
    this.isLoggedIn = false;
    this.loginUsername = '';
    this.loginPassword = '';
    this.turbines = [];
    this.farms = [];
    this.alerts = [];
    this.selectedRegion = '';
    this.selectedFarm = '';
    this.currentPage = 1;

    sessionStorage.removeItem('authUser');
    sessionStorage.removeItem('authPass');
  }

  hasAnyRole(...roles: Role[]): boolean {
    return roles.includes(this.currentRole);
  }

  /* Data loading */

  loadFarms(): void {
    this.http.get<FarmSummary[]>(`${this.apiBase}/monitoring/farms`)
      .subscribe({
        next: data => this.farms = data,
        error: err => console.error('Error loading farms', err)
      });
  }

  loadTurbines(): void {
    const params: string[] = [];
    if (this.selectedRegion) {
      params.push(`region=${encodeURIComponent(this.selectedRegion)}`);
    }
    if (this.selectedFarm) {
      params.push(`farmCode=${encodeURIComponent(this.selectedFarm)}`);
    }
    const query = params.length ? `?${params.join('&')}` : '';

    this.http.get<TurbineHealth[]>(`${this.apiBase}/monitoring/turbines${query}`)
      .subscribe({
        next: data => {
          this.turbines = data;
          this.currentPage = 1; // reset page on filter change
        },
        error: err => console.error('Error loading turbines', err)
      });
  }

  loadAlerts(): void {
    this.http.get<HealthAlert[]>(`${this.apiBase}/monitoring/alerts`)
      .subscribe({
        next: data => this.alerts = data,
        error: err => console.error('Error loading alerts', err)
      });
  }

  /* Status / severity helpers */

  statusClass(status: string | null | undefined): string {
    const s = (status || '').toUpperCase();
    if (s === 'OK') return 'status-ok';
    if (s === 'WARNING') return 'status-warning';
    if (s === 'FAULT' || s === 'CRITICAL') return 'status-fault';
    return '';
  }

  alertSeverityClass(severity: string): string {
    const s = (severity || '').toUpperCase();
    if (s === 'CRITICAL') return 'alert-severity-critical';
    if (s === 'WARNING') return 'alert-severity-warning';
    return '';
  }

  /* Region / farm filters */

  get filteredFarms(): FarmSummary[] {
    if (!this.selectedRegion) {
      return this.farms;
    }
    return this.farms.filter(f => f.region === this.selectedRegion);
  }

  onRegionChange(): void {
    const stillValid =
      this.selectedFarm &&
      this.farms.some(
        f => f.code === this.selectedFarm && f.region === this.selectedRegion
      );

    if (!stillValid) {
      this.selectedFarm = '';
    }
    this.loadTurbines();
  }

  /* Pagination */

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.turbines.length / this.pageSize));
  }

  get pagedTurbines(): TurbineHealth[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.turbines.slice(start, start + this.pageSize);
  }

  onPageSizeChange(): void {
    this.currentPage = 1;
  }

  setPage(page: number): void {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
  }

  nextPage(): void {
    this.setPage(this.currentPage + 1);
  }

  prevPage(): void {
    this.setPage(this.currentPage - 1);
  }

  /* Alerts and anomalies */

  // Alerts card: alerts for any visible turbine in current grid (region/farm)
  get filteredAlerts(): HealthAlert[] {
    if (!this.turbines.length) {
      return [];
    }
    const visibleCodes = new Set(this.turbines.map(t => t.code));
    return this.alerts.filter(a => visibleCodes.has(a.turbineCode));
  }

  // Anomaly table for reliability/admin: extra severity + region/farm filter
  get anomalyRows(): HealthAlert[] {
    if (!this.alerts.length || !this.turbines.length) {
      return [];
    }

    const visibleCodes = new Set(this.turbines.map(t => t.code));

    return this.alerts.filter(a => {
      if (!visibleCodes.has(a.turbineCode)) {
        return false;
      }

      const sev = (a.severity || '').toUpperCase();
      if (this.selectedSeverity !== 'ALL' && sev !== this.selectedSeverity) {
        return false;
      }

      const t = this.turbines.find(tu => tu.code === a.turbineCode);
      if (!t) {
        return false;
      }

      if (this.selectedRegion && t.region !== this.selectedRegion) {
        return false;
      }

      if (this.selectedFarm && t.farmCode !== this.selectedFarm) {
        return false;
      }

      return true;
    });
  }
}
