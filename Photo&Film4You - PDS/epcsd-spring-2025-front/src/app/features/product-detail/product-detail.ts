import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {
  ProductDetailPresentation
} from '@app/features/product-detail/product-detail-presentation/product-detail-presentation';
import {ActivatedRoute, Router} from '@angular/router';
import {ProductService} from '@app/core/services/product.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {catchError, of, switchMap} from 'rxjs';
import {Product} from '@app/core/models/product/product.model';

@Component({
  selector: 'app-product-detail',
  imports: [ CommonModule,
  MatDialogModule,
  MatProgressSpinnerModule
  ],
  standalone: true,
  template: '',
  styles: [],
})
export class ProductDetail implements OnInit{

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly productService = inject(ProductService);
  private readonly snackBar = inject(MatSnackBar);


  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        if (id) {
          return this.productService.getProductById(Number(id)).pipe(
            catchError( error => {
              this.snackBar.open("Error al cargar el detalle del producto.", "Cerrar", {duration: 5000});
              console.error(error);
              return of(null as Product | null)
            } )
          );
        }
        return of(null);
      })
    ).subscribe( product => {
      if (product) {
        const dialogRef = this.dialog.open(ProductDetailPresentation, {
          data: product,
          width: '600px',
          disableClose: true
        });

        dialogRef.afterClosed().subscribe(() => {
          this.router.navigate(['../'], { relativeTo: this.route });
        });
      } else {
        this.router.navigate(['../'], { relativeTo: this.route });
      }
    })
  }

}
