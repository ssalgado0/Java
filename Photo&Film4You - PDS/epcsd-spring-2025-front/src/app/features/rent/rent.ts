import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MatTableModule} from '@angular/material/table';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSnackBar} from '@angular/material/snack-bar';

import {BookingService} from '@app/core/services/booking.service';
import {Booking, BookingLine} from '@app/core/models/booking/booking.model';
import {LoadingService} from '@app/core/services/loading-service';

export interface BookingViewModel extends Booking {
  totalPrice: number;
}

@Component({
  selector: 'app-rent',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './rent.html',
  styleUrl: './rent.css',
})
export class Rent implements OnInit {

  bookings: BookingViewModel[] = [];

  columnsToDisplay = ['startDate', 'endDate', 'totalPrice', 'status', 'expand'];

  expandedElement: BookingViewModel | null = null;

  isLoading = false;

  constructor(
    private readonly bookingService: BookingService,
    private readonly snackBar: MatSnackBar,
    private readonly loadingService: LoadingService
  ) {}

  ngOnInit(): void {
    this.getUserBookings();
  }

  protected readonly BookingService = BookingService;

  getUserBookings(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.bookingService.getUserBookings().subscribe({
      next: (data) => {
        const safeData = data || [];

        this.bookings = safeData.map(booking => ({
          ...booking,
          totalPrice: this.calculateBookingTotal(booking.lines)
        }));

        this.isLoading = false;
        this.loadingService.hide();
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        this.loadingService.hide();
        this.snackBar.open("Error al cargar reservas", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  private calculateBookingTotal(lines: BookingLine[]): number {
    return lines.reduce((acc, line) => acc + (Number.parseFloat(line.totalPrice) || 0), 0);
  }
}
