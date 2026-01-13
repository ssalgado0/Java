import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProductDetail} from './product-detail';
import {Product} from '@app/core/models/product/product.model';
import {Observable, of, throwError} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {ProductService} from '@app/core/services/product.service';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';

describe('ProductDetail', () => {
  let component: ProductDetail;
  let fixture: ComponentFixture<ProductDetail>;
  let getProductByIdSpy: jasmine.Spy;
  let openDialogSpy: jasmine.Spy;
  let navigateSpy: jasmine.Spy;
  let openSnackBarSpy: jasmine.Spy;

  const mockProduct: Product = { id: 10, name: 'Smart Test',
    brand: 'anyBrand',
    model: 'anyModel',
    description: 'A detailed description',
    dailyPrice: 99.99,
    categoryId: 1} as Product;

  const mockActivatedRoute = {
    paramMap: of(new Map([['id', '10']]))
  };

  const mockProductService = {
    getProductById: (id: number): Observable<Product> => of(mockProduct)
  }

  const mockDialogRef = {
    afterClosed: () => of(null)
  };
  const mockMatDialog = {
    open: (component: any, config?: any) => mockDialogRef
  };
  const mockRouter = {
    navigate: (commands: any[], extras?: any) => {}
  };
  const mockSnackBar = {
    open: (message: string, action?: string, config?: any) => {}
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductDetail],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: ProductService, useValue: mockProductService },
        { provide: MatDialog, useValue: mockMatDialog },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    })
    .compileComponents();

    getProductByIdSpy = spyOn(mockProductService, 'getProductById').and.returnValue(of(mockProduct));
    openDialogSpy = spyOn(mockMatDialog, 'open').and.returnValue(mockDialogRef as any);
    navigateSpy = spyOn(mockRouter, 'navigate');
    openSnackBarSpy = spyOn(mockSnackBar, 'open');

    fixture = TestBed.createComponent(ProductDetail);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle API error and show a snackBar', () => {
    spyOn(console, 'error');
    getProductByIdSpy.and.returnValue(throwError(() => new Error('404 Not Found')));
    fixture.detectChanges();
    expect(openDialogSpy).not.toHaveBeenCalled();
    expect(openSnackBarSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../'] as any, jasmine.anything() as any);
  });
});
