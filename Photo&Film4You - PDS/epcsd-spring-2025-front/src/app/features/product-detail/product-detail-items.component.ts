import {Component, computed, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatChipsModule} from '@angular/material/chips';
import {MatTableModule} from '@angular/material/table';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {ProductService} from '@app/core/services/product.service';
import {LoadingService} from '@app/core/services/loading-service';
import {AuthService} from '@app/core/services/auth.service';
import {Product} from '@app/core/models/product/product.model';
import {Item} from '@app/core/models/item/item.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatTableModule,
    MatSnackBarModule
  ],
  templateUrl: './product-detail-items.component.html',
})
export class ProductDetailItems implements OnInit {
  product: Product | null = null;
  items: Item[] = [];

  addFormVisible = false;
  newSerial: string = '';

  productId: number | null = null;
  displayedColumns: string[] = ['serialNumber', 'status', 'actions'];

  currentUser = computed(() => this.auth.currentUser());

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly productService: ProductService,
    private readonly loadingService: LoadingService,
    private readonly auth: AuthService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productId = Number(id);
      this.loadProduct();
      this.loadItems();
    } else {
      this.router.navigate(['/products']);
    }
  }

  toggleAddForm(productId?: number) {
    this.addFormVisible = !this.addFormVisible;
  }

  createItem() {
    if (!this.productId) return;
    const serial = this.newSerial;

    if (!serial || serial.trim().length === 0) {
      this.snackBar.open("El número de serie es obligatorio", "Cerrar", {
        duration: 4000,
        panelClass: ['snackbar-error']
      })
      return;
    }

    this.loadingService.show();
    this.productService.createItem(this.productId, serial.trim()).subscribe({
      next: (createdItem) => {

        this.snackBar.open("Unidad añadida correctamente", "Cerrar", {
          duration: 3000
        })

        this.loadItems();


        this.newSerial = '';
        this.addFormVisible = false;

        this.loadingService.hide();
      },
      error: (err) => {
        this.snackBar.open("Error al añadir la unidad", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        })

        this.loadingService.hide();
      }
    });
  }


  loadProduct(): void {
    if (!this.productId) return;

    this.loadingService.show();
    this.productService.getProductById(this.productId).subscribe({
      next: (product) => {
        this.product = product;
        this.loadingService.hide();
      },
      error: (error) => {
        this.snackBar.open('Error al cargar el producto', 'Cerrar', {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
        this.loadingService.hide();
        this.router.navigate(['/products']);
      }
    });
  }

  loadItems(): void {
    if (!this.productId) return;

    this.productService.getItemsByProductId(this.productId).subscribe({
      next: (items) => {
        this.items = items;
        this.loadingService.hide();
      },
      error: (error) => {
        this.snackBar.open('Error al cargar las unidades', 'Cerrar', {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
        this.loadingService.hide();
      }
    });
  }

  toggleItemStatus(item: Item): void {

    const operational = item.status !== 'OPERATIONAL';
    const statusText = operational ? 'Operativa' : 'No Operativa';


    this.loadingService.show();
    this.productService.updateItemStatus(item.serialNumber, operational).subscribe({
      next: (updatedItem) => {
        const index = this.items.findIndex(i => i.serialNumber === item.serialNumber);
        if (index !== -1) {
          this.items[index] = updatedItem;
          this.items = [...this.items];
        }


        this.snackBar.open(`Estado actualizado a ${statusText}`, 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.loadingService.hide();
      },
      error: (error) => {
        let errorMessage = 'Error al actualizar el estado';

        if (error.status === 409) {
          errorMessage = 'No se puede cambiar el estado. La unidad tiene alquileres activos o pendientes.';
        } else if (error.status === 404) {
          errorMessage = 'Unidad no encontrada';
        }

        this.snackBar.open(errorMessage, 'Cerrar', {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
        this.loadingService.hide();
      }
    });
  }

  getStatusLabel(status: string): string {
    return status === 'OPERATIONAL' ? 'Operativa' : 'No Operativa';
  }

  getStatusClass(status: string): string {
    return status === 'OPERATIONAL' ? 'status-operational' : 'status-non-operational';
  }

  resetAddForm(): void {
    this.newSerial = '';
    this.addFormVisible = false;
  }

  isAdmin(): boolean {
    return this.auth.currentUser()?.role === 'ADMIN';
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }
}
