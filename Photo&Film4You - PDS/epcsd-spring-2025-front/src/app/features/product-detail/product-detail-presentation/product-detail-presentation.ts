import {Component, Inject} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {CommonModule} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {Product} from '@app/core/models/product/product.model';

@Component({
  selector: 'app-product-detail-presentation',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './product-detail-presentation.html',
})

export class ProductDetailPresentation {

  constructor(
    public dialogRef: MatDialogRef<ProductDetailPresentation>,
    @Inject(MAT_DIALOG_DATA) public data: Product
  ) {
  }

  onClose() : void {
    this.dialogRef.close();
  }

}
