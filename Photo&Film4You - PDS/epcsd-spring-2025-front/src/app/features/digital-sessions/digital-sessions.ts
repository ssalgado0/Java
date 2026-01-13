import {Component, computed, OnInit} from '@angular/core';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {DigitalSession} from '@app/core/models/digital';
import {DigitalService} from '@app/core/services/digital.service';
import {LoadingService} from '@app/core/services/loading-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';
import {AuthService} from '@app/core/services/auth.service';
import {MatTooltip} from '@angular/material/tooltip';
import {StatusLabelPipe} from '@app/core/pipes/status-label.pipe';

@Component({
  selector: 'app-digital-sessions',
  standalone: true,
  imports: [
    MatButton,
    MatFabButton,
    MatIcon,
    RouterLink,
    MatTooltip,
    StatusLabelPipe,
  ],
  templateUrl: './digital-sessions.html',
  styleUrl: './digital-sessions.css',
})
export class DigitalSessions implements OnInit {

  sessions: DigitalSession[] = [];
  viewAll: boolean = false;
  currentUser = computed(() => this.auth.currentUser());

  constructor(
    private readonly digitalService: DigitalService,
    private readonly loadingService: LoadingService,
    private readonly auth: AuthService,
    private readonly snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.fetchSessions(this.viewAll);
  }

  fetchSessions(viewAll: boolean): void {
    this.loadingService.show();

    const request = viewAll
      ? this.digitalService.getAllDigitalSessions()
      : this.digitalService.getUserDigitalSessions();

    request.subscribe({
      next: s => {
        this.sessions = s;
        this.loadingService.hide();
      },
      error: () => {
        this.snackBar.open("Ha ocurrido un error al cargar las sesiones digitales", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
        this.loadingService.hide();
      }
    });
  }

  toggleViewAllSessions(): void {
    this.viewAll = !this.viewAll;
    this.fetchSessions(this.viewAll);
  }

  getStatusClass(session: DigitalSession): string {
    return {
      [DigitalStatus.AVAILABLE]: 'bg-success',
      [DigitalStatus.NOT_AVAILABLE]: 'bg-danger',
      [DigitalStatus.REVIEW_PENDING]: 'bg-secondary'
    }[session.status];
  }
}
