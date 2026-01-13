import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule, CurrencyPipe, DatePipe} from '@angular/common';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
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
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

import {Booking, BookingLine} from '@app/core/models/booking/booking.model';
import {BookingService} from '@app/core/services/booking.service';

@Component({
  selector: 'app-booking-summary',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    MatCard,
    MatCardContent,
    MatCardActions,
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    MatButton,
    MatIcon
  ],
  templateUrl: './booking-summary.html'
})
export class BookingSummary implements OnInit {

  booking: Booking | null = null;
  totalCost = 0;
  isLoading = false;

  readonly columnsToDisplay = ['name', 'dailyPrice', 'quantity', 'totalItem'];
  dataSource = new MatTableDataSource<BookingLine>();

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly bookingService: BookingService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;

    if (!id || Number.isNaN(id)) {
      this.router.navigate(['/cart']);
      return;
    }

    this.isLoading = true;

    this.bookingService.getBookingById(id).subscribe({
      next: (booking) => {
        this.booking = booking;
        this.dataSource.data = booking.lines || [];

        this.totalCost = booking.lines?.reduce((acc, line) =>
            acc + (Number.parseFloat(line.totalPrice) || 0)
        , 0) ?? 0;

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error cargando la reserva', err);
        this.isLoading = false;
        this.router.navigate(['/cart']);
      }
    });
  }

  goBackToProducts(): void {
    this.router.navigate(['/products']);
  }

}
