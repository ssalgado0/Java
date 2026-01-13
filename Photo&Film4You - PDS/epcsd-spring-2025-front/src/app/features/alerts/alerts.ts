import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MatTableModule} from '@angular/material/table';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSnackBar} from '@angular/material/snack-bar';

import {ProductService} from '@app/core/services/product.service';
import {Alert} from '@app/core/models/product/alert.model';
import {LoadingService} from '@app/core/services/loading-service';
import {forkJoin} from 'rxjs';
import {RouterLink} from '@angular/router';

interface AlertViewModel extends Alert {
  productName: string;
}

@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './alerts.html',
  styleUrl: './alerts.css',
})
export class Alerts implements OnInit {

  alerts: AlertViewModel[] = [];

  columnsToDisplay = ['productName', 'from', 'to', 'actions'];

  isLoading = false;

  constructor(
    private readonly productService: ProductService,
    private readonly snackBar: MatSnackBar,
    private readonly loadingService: LoadingService
  ) {}

  ngOnInit(): void {
    this.getUserAlerts();
  }

  getUserAlerts(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.productService.getAlertsByCurrentUser().subscribe({
      next: (alerts) => {
        const safeAlerts = alerts || [];

        if (safeAlerts.length === 0) {
            this.alerts = [];
            this.isLoading = false;
            this.loadingService.hide();
            return;
        }

        // We need to fetch product details to show names
        // For simplicity, let's fetch all products first or fetch individually.
        // Since we don't have a "getProductsByIds" endpoint, and getAllProducts might be heavy if many products...
        // But let's assume we can fetch all products for now or just fetch individually.
        // Let's try fetching all products first as it might be cached or easier.
        // Actually, let's fetch each product individually for the alerts we have.

        const productIds = [...new Set(safeAlerts.map(a => a.productId))];
        const productRequests = productIds.map(id => this.productService.getProductById(id));

        forkJoin(productRequests).subscribe({
          next: (products) => {
              const productMap = new Map(products.map(p => [p.id, p.name]));

              this.alerts = safeAlerts.map(alert => ({
                  ...alert,
                  productName: productMap.get(alert.productId) || 'Producto desconocido'
              }));

              this.isLoading = false;
              this.loadingService.hide();
          },
          error: (err) => {
              console.error('Error fetching products for alerts', err);
              this.isLoading = false;
              this.loadingService.hide();
              this.snackBar.open("Error al cargar detalles de productos", "Cerrar", { duration: 3000 });
          }
        });
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        this.loadingService.hide();
        this.snackBar.open("Error al cargar alertas", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  deleteAlert(alert: AlertViewModel): void {
    if (!confirm('¿Estás seguro de que quieres eliminar esta alerta?')) {
      return;
    }

    this.loadingService.show();
    this.productService.deleteAlert(alert.id).subscribe({
      next: () => {
        this.alerts = this.alerts.filter(a => a.id !== alert.id);
        this.snackBar.open("Alerta eliminada correctamente", "Cerrar", { duration: 3000 });
        this.loadingService.hide();
      },
      error: (err) => {
        console.error('Error deleting alert', err);
        this.snackBar.open("Error al eliminar la alerta", "Cerrar", { duration: 3000 });
        this.loadingService.hide();
      }
    });
  }
}
