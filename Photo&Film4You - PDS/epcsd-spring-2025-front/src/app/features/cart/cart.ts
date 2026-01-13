import {Component, OnInit} from '@angular/core';
import {CartService} from '@app/core/services/cart.service';
import {Observable} from 'rxjs';
import {BookingCart, CartItem} from '@app/core/models/cart';
import { Booking } from '@app/core/models/booking/booking.model';


import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatFooterCell,
  MatFooterCellDef,
  MatFooterRow,
  MatFooterRowDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {AsyncPipe, CurrencyPipe} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {Router, RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {MatTooltip} from '@angular/material/tooltip';
import {BookingService} from '@app/core/services/booking.service';
import {AvailabilityResponse} from '@app/core/models/booking';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DateRange} from '@app/core/models/date/date-range';
import {BookingRequest} from '@app/core/models/booking/booking-request.model';

@Component({
  selector: 'app-cart',
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderCell,
    MatCell,
    CurrencyPipe,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    MatFooterCell,
    MatFooterCellDef,
    MatFooterRow,
    MatFooterRowDef,
    MatButton,
    RouterLink,
    MatIcon,
    MatCard,
    MatCardContent,
    MatCardActions,
    MatIconButton,
    MatTooltip,
    AsyncPipe
  ],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class Cart implements OnInit {

  readonly columnsToDisplay = ['name', 'dailyPrice', 'totalPrice', 'quantity', 'availability', 'actions'];
  dataSource = new MatTableDataSource<CartItem>()
  items: CartItem[] = [];
  cart: BookingCart | null = null;
  availabilityStatus: AvailabilityResponse | null = null;

  cart$: Observable<BookingCart> = new Observable<BookingCart>();
  dateRange$ = new Observable<DateRange | null>();

  constructor(
    private readonly cartService: CartService,
    private readonly bookingService: BookingService,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router
  ) {
    this.cart$ = this.cartService.cart$;
    this.dateRange$ = this.bookingService.dateRange$;
  }

  ngOnInit() {
    this.dateRange$.subscribe(range => {
      if (range) {
        this.cart$.subscribe(c => {
          c.startDate = range.start;
          c.endDate = range.end;

          this.dataSource.data = c.items;
          this.cart = c;
          this.items = c.items;
          this.checkAvailability(c);
        })
      }
    });
  }

  getTotalCost(): number {
    return this.items.reduce((acc, item) => acc + (item.product.dailyPrice * item.quantity), 0);
  }

  increase(item: CartItem): void {
    this.cartService.addProduct(item.product, 1);
  }

  decrease(item: CartItem): void {
    this.cartService.removeProduct(item.product, 1);
  }

  remove(item: CartItem): void {
    this.cartService.removeProduct(item.product);
  }

  clearCart(): void {
    this.cartService.clear();
  }

  checkAvailability(cart: BookingCart): void {
    if (!cart?.startDate || !cart?.endDate || cart?.items?.length === 0) {
      return;
    }

    this.bookingService.checkAvailability({
      startDate: cart.startDate,
      endDate: cart.endDate,
      productIds: cart.items.map(i => i.product.id) as number[]
    }).subscribe({
      next: (result) => {
        this.availabilityStatus = result;
      },
      error: () => {
        this.availabilityStatus = null;
        this.snackBar.open("Ha ocurrido un error al comprobar la disponibilidad", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  getAvailabilityStatus(item: CartItem): string {
    const availability = this.getAvailability(item);

    if (availability === 'Desconocido') {
      return 'bg-secondary';
    }

    if (availability as number <= 0) {
      return 'bg-danger';
    }

    if (availability as number <= 3) {
      return 'bg-warning text-dark';
    }

    return 'bg-success';
  }

  getAvailability(item: CartItem): string | number {
    if (!this.availabilityStatus) {
      return 'Desconocido';
    }

    return this.availabilityStatus.availableUnitsByProduct[item.product.id as number];
  }

  isPossibleToAddItem(item: CartItem): boolean {
    const availability = this.getAvailability(item);

    if (availability === 'Desconocido') {
      return false;
    }

    return (availability as number) > item.quantity;
  }

      checkout(): void {
        if (!this.cart) {
          return;
        }

        const { startDate, endDate, items } = this.cart;

        if (!startDate || !endDate || items.length === 0) {
          return;
        }

        const bookingRequest: BookingRequest = {
          startDate: this.formatDate(startDate),
          endDate: this.formatDate(endDate),
          lines: items.map(item => ({
            productId: item.product.id!,
            quantity: item.quantity
          }))
        };

        this.bookingService.createBooking(bookingRequest).subscribe({
          next: (booking: Booking) => {
            this.cartService.clear();
            this.snackBar.open("¡Reserva realizada con éxito!", "Cerrar", {
              duration: 4000,
              panelClass: ['bg-success', 'text-white']
            });

            const id = booking.id;
            this.router.navigate(['/bookings', id]);
          },
          error: (err) => {
            console.error(err);
            this.snackBar.open("Error al procesar la reserva", "Cerrar", {
              duration: 4000,
              panelClass: ['bg-danger', 'text-white']
            });
          }
        });
      }



  private formatDate(date: Date): string {
    const d = new Date(date);
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
  }
}
