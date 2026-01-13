import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductDetailPresentation } from './product-detail-presentation';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { Product } from '@app/core/models/product/product.model';

describe('ProductDetailPresentation', () => {
  let component: ProductDetailPresentation;
  let fixture: ComponentFixture<ProductDetailPresentation>;

  const mockProduct: Product = {
    id: 1,
    brand: 'anyBrand',
    model: 'anyModel',
    name: 'Test Product',
    description: 'A detailed description',
    dailyPrice: 99.99,
    categoryId: 1
  } as Product;

  const mockDialogRef = {
    close: jasmine.createSpy('close')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductDetailPresentation, MatDialogModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: mockProduct },
        { provide: MatDialogRef, useValue: mockDialogRef }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductDetailPresentation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the product name from injected data', () => {
    const titleElement: HTMLElement = fixture.nativeElement.querySelector('h2');
    expect(titleElement.textContent).toContain('Test Product');
  });

  it('should call dialogRef.close() when the close button is clicked', () => {

    const closeButton: HTMLButtonElement = fixture.nativeElement.querySelector('button');
    closeButton.click();
    expect(mockDialogRef.close).toHaveBeenCalledTimes(1);
  });
});
