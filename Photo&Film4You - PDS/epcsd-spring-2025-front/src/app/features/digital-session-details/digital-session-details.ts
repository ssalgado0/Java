import {Component, OnInit, ViewChild} from '@angular/core';
import {DigitalItem} from '@app/core/models/digital';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {MatButton, MatFabButton, MatIconButton} from '@angular/material/button';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {DigitalService} from '@app/core/services/digital.service';
import {LoadingService} from '@app/core/services/loading-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';
import {StatusLabelPipe} from '@app/core/pipes/status-label.pipe';
import {MatSort, MatSortHeader} from '@angular/material/sort';

@Component({
  selector: 'app-digital-session-details',
  imports: [
    RouterLink,
    MatCard,
    MatCardContent,
    MatFabButton,
    MatIcon,
    MatTooltip,
    MatTable,
    MatColumnDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatIconButton,
    MatButton,
    StatusLabelPipe,
    MatSortHeader,
    MatSort
  ],
  templateUrl: './digital-session-details.html',
  styleUrl: './digital-session-details.css',
})
export class DigitalSessionDetails implements OnInit {

  sessionId: number | null = null;
  dataSource = new MatTableDataSource<DigitalItem>()
  items: DigitalItem[] = [];
  columnsToDisplay: string[] = ['id', 'description', 'lat', 'lon', 'link', 'status', 'actions'];

  @ViewChild(MatSort)
  set matSort(sort: MatSort) {
    this.dataSource.sort = sort;
  }

  constructor(
    private readonly digitalService: DigitalService,
    private readonly loadingService: LoadingService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.loadingService.show();

    const id = this.route.snapshot.paramMap.get('id')!;
    this.sessionId = Number.parseInt(id);

    this.digitalService.getDigitalItemsBySessionId(this.sessionId)
      .subscribe({
        next: i => {
          this.dataSource.data = i;
          this.items = i;
          this.loadingService.hide();
        },
        error: () => {
          this.loadingService.hide();
          this.snackBar.open("Ha habido un error cargando los elementos de la sesión", "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
          this.router.navigate(['/sessions']);
        }
      });
  }

  getStatusClass(item: DigitalItem): string {
    return {
      [DigitalStatus.AVAILABLE]: 'bg-success',
      [DigitalStatus.NOT_AVAILABLE]: 'bg-danger',
      [DigitalStatus.REVIEW_PENDING]: 'bg-secondary'
    }[item.status];
  }
}
