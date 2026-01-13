import {Component, computed, effect, signal} from '@angular/core';
import {AuthService} from '@app/core/services/auth.service';
import {RouterLink, RouterOutlet} from '@angular/router';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule} from '@angular/forms';
import {ProductService} from '@app/core/services/product.service'
import {Product} from '@app/core/models/product/product.model'
import {CommonModule} from '@angular/common';
import {LoadingService} from '@app/core/services/loading-service';
import {DatePicker} from '@app/shared/date-picker/date-picker';
import {DateRange} from '@app/core/models/date/date-range';
import {BookingService} from '@app/core/services/booking.service';
import {AvailabilityResponse} from '@app/core/models/booking';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TableFilter} from '@app/shared/table-filter/table-filter';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {PageResponse} from '@app/core/models/pagination/page-response.model';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {filter, Observable, switchMap} from 'rxjs';
import {MatTooltip} from '@angular/material/tooltip';
import {CartService} from '@app/core/services/cart.service';
import {CreateAlertRequest} from '@app/core/models/product/create-alert-request.model';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialog} from '@app/shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    RouterLink,
    RouterOutlet,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    FormsModule,
    CommonModule,
    DatePicker,
    TableFilter,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatTooltip
  ],
  templateUrl: './products.html',
  styleUrl: './products.css',
})
export class Products {

  productsPage = signal<PageResponse<Product> | null>(null);
  filterTerm = signal('');
  pageIndex = signal(0);
  pageSize = signal(12);
  isProductsLoading = signal(false);

  filteredProducts = computed(() => this.productsPage()?.content ?? []);

  totalItems = computed(() => this.productsPage()?.totalElements ?? 0);
  currentPage = computed(() => this.productsPage()?.number ?? 0);
  currentPageSize = computed(() => this.productsPage()?.size ?? this.pageSize());

  currentUser = computed(() => this.auth.currentUser());
  dateRange$: Observable<DateRange | null>;
  availability: AvailabilityResponse | null = null;
  dateRange: DateRange | null = null;

  constructor(
    private readonly auth: AuthService,
    private readonly productService: ProductService,
    private readonly loadingService: LoadingService,
    private readonly bookingService: BookingService,
    private readonly snackBar: MatSnackBar,
    private readonly cartService: CartService,
    private readonly dialog: MatDialog
  ) {
    effect(() => {
      const term = this.filterTerm();
      const index = this.pageIndex();
      const size = this.pageSize();

      this.fetchProducts(term, index, size);
    });
    this.dateRange$ = this.bookingService.dateRange$;
    this.dateRange$.subscribe(dateRange => {
      this.dateRange = dateRange;
    });
  }

  fetchProducts(term: string, index: number, size: number): void {

    this.isProductsLoading.set(true);
    this.loadingService.hide();
    this.productService.getProducts(term, index, size).subscribe({
      next: (pageResponse: PageResponse<Product>) => {
        this.productsPage.set(pageResponse);
        this.isProductsLoading.set(false);

        if (this.dateRange) {
          this.checkAvailability(this.dateRange);
        }
      },
      error: error => {
        console.error('Error fetching products:', error);
        this.snackBar.open("Error al cargar o filtrar productos", "Cerrar", { duration: 5000 });
        this.isProductsLoading.set(false);
      }
    });
  }

  onPaginatorChange(event: PageEvent): void {
    if (this.pageIndex() !== event.pageIndex) {
      this.pageIndex.set(event.pageIndex);
    }
    if (this.pageSize() !== event.pageSize) {
      this.pageSize.set(event.pageSize);
    }
  }

  onFilterTermChange(term: string): void {
    this.filterTerm.set(term);
    this.pageIndex.set(0);
  }

  onRangeSelected(range: DateRange): void {
    this.bookingService.setDateRange(range);
    this.checkAvailability(range);
  }

  checkAvailability(range: DateRange): void {
    this.loadingService.show();

    this.bookingService.checkAvailability({
      startDate: range.start,
      endDate: range.end,
      productIds: this.filteredProducts().map(p => p.id) as number[]
    }).subscribe({
      next: result => {
        this.availability = result;
        this.loadingService.hide();
      },
      error: error => {
        console.error('Error checking availability:', error);
        this.snackBar.open("Ha ocurrido un error al comprobar la disponibilidad", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        })
        this.loadingService.hide();
      }
    });
  }

  onRangeReseted(): void {
    this.bookingService.resetDateRange();
    this.cartService.clear();
    this.availability = null;
  }

  addProduct(product: Product): void {
    this.cartService.addProduct(product);
  }

  getAvailability(product: Product): string | number {
    if (!this.availability) {
      return 'Desconocido';
    }

    return this.availability.availableUnitsByProduct[product.id as number];
  }

  isPossibleToAddItem(product: Product): boolean {
    const availability = this.getAvailability(product);

    if (availability === 'Desconocido') {
      return false;
    }

    const itemQuantityInCart = this.cartService.getItemQuantity(product);
    return (availability as number) > itemQuantityInCart;
  }

  createAlert(product: Product): void {
    if (!this.dateRange || !this.currentUser()) {
      return;
    }

    const request: CreateAlertRequest = {
      productId: product.id as number,
      userId: Number(this.currentUser()!.id),
      from: this.formatDate(this.dateRange.start),
      to: this.formatDate(this.dateRange.end)
    };

    this.productService.createAvailabilityAlert(request).subscribe({
      next: () => {
        this.snackBar.open("Se ha creado la alerta", "Cerrar", { duration: 3000 });
      },
      error: (err) => {
        console.error('Error creating alert:', err);
        if (err.status === 409) {
          this.snackBar.open("Ya tienes una alerta creada para este producto y fechas", "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
        } else {
          this.snackBar.open("Error al crear la alerta", "Cerrar", { duration: 3000 });
        }
      }
    });
  }

  private formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${year}-${month}-${day}`;
  }

  isAdmin(): boolean {
    return this.auth.currentUser()?.role === 'ADMIN';
  }

  deleteProduct(product: Product): void {
    this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Eliminar producto',
        message: '¿Seguro que quieres eliminar este producto? Esta acción no se puede deshacer.',
        confirmText: 'Eliminar',
        cancelText: 'Cancelar'
      }
    })
    .afterClosed()
    .pipe(
      filter(Boolean),
      switchMap(() => this.productService.deleteProduct(product.id as number))
    )
    .subscribe({
      next: () => {
        this.snackBar.open('Producto eliminado correctamente', 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.fetchProducts(this.filterTerm(), this.pageIndex(), this.pageSize());
      },
      error: (error) => {
        if (error.status === 409) {
          this.snackBar.open('No se puede eliminar un producto con reservas asociadas', 'Cerrar', {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
        } else {
          this.snackBar.open('Error al eliminar el producto', 'Cerrar', {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
        }
      }
    });
  }
}
