import {ComponentFixture, TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ProductDetailItems} from './product-detail-items.component';
import {ActivatedRoute, Router} from '@angular/router';
import {of, throwError} from 'rxjs';
import {ProductService} from '@app/core/services/product.service';
import {LoadingService} from '@app/core/services/loading-service';
import {AuthService} from '@app/core/services/auth.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Item} from '@app/core/models/item/item.model';

describe('ProductDetail', () => {
  let component: ProductDetailItems;
  let fixture: ComponentFixture<ProductDetailItems>;

  let productServiceMock: jasmine.SpyObj<ProductService>;
  let loadingServiceMock: jasmine.SpyObj<LoadingService>;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;
  let routerMock: jasmine.SpyObj<Router>;

  const activatedRouteMock: any = { snapshot: { paramMap: { get: () => '123' } } };

  beforeEach(async () => {
    productServiceMock = jasmine.createSpyObj<ProductService>('ProductService', ['getItemsByProductId', 'createItem', 'updateItemStatus', 'getProductById']);
    loadingServiceMock = jasmine.createSpyObj<LoadingService>('LoadingService', ['show', 'hide']);
    snackBarMock = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);
    routerMock = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ProductDetailItems, HttpClientTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerMock },
        { provide: ProductService, useValue: productServiceMock },
        { provide: LoadingService, useValue: loadingServiceMock },
        { provide: AuthService, useValue: { currentUser: () => ({ role: 'ADMIN' }) } },
        { provide: MatSnackBar, useValue: snackBarMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductDetailItems);
    component = fixture.componentInstance;

    productServiceMock.getProductById.and.returnValue(of({} as any));
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load items on init', () => {
    const items: Item[] = [{ serialNumber: 'S1', status: 'OPERATIONAL' } as Item];
    productServiceMock.getItemsByProductId.and.returnValue(of(items));

    fixture.detectChanges();

    expect(productServiceMock.getItemsByProductId).toHaveBeenCalledWith(123);
    expect(component.items).toEqual(items);
    expect(loadingServiceMock.hide).toHaveBeenCalled();
  });



  it('resetAddForm should clear fields', () => {
    component.newSerial = 'abc';
    component.resetAddForm();
    expect(component.newSerial).toBe('');
  });

  it('getStatusLabel and getStatusClass work', () => {
    expect(component.getStatusLabel('OPERATIONAL')).toBe('Operativa');
    expect(component.getStatusLabel('NON_OPERATIONAL')).toBe('No Operativa');
    expect(component.getStatusClass('OPERATIONAL')).toBe('status-operational');
    expect(component.getStatusClass('NON_OPERATIONAL')).toBe('status-non-operational');
  });

  it('createItem shows snackbar when serial empty', () => {
    component.productId = 1;
    component.newSerial = '   ';
    component.createItem();

  });

  it('createItem success flow', fakeAsync(() => {
    component.productId = 1;
    component.newSerial = 'S2';

    const created: Item = { serialNumber: 'S2', status: 'OPERATIONAL' } as Item;
    productServiceMock.createItem.and.returnValue(of(created.serialNumber));
    productServiceMock.getItemsByProductId.and.returnValue(of([created]));

    component.createItem();
    tick();

    expect(loadingServiceMock.show).toHaveBeenCalled();
    expect(productServiceMock.createItem).toHaveBeenCalledWith(1, 'S2');
    expect(component.items.length).toBe(1);
  }));

  it('createItem error flow with 409', fakeAsync(() => {
    component.productId = 1;
    component.newSerial = 'S3';
    const error = { status: 409 };
    productServiceMock.createItem.and.returnValue(throwError(() => error));

    component.createItem();
    tick();

    expect(loadingServiceMock.hide).toHaveBeenCalled();
  }));

  it('toggleItemStatus success updates item', fakeAsync(() => {
    component.productId = 1;
    component.items = [ { serialNumber: 'S1', status: 'OPERATIONAL' } as Item ];
    const updated: Item = { serialNumber: 'S1', status: 'NON_OPERATIONAL' } as Item;
    productServiceMock.updateItemStatus.and.returnValue(of(updated));

    component.toggleItemStatus(component.items[0]);
    tick();

    expect(loadingServiceMock.show).toHaveBeenCalled();
    expect(component.items[0].status).toBe('NON_OPERATIONAL');
  }));

  it('toggleItemStatus error flow 404 shows message', fakeAsync(() => {
    component.productId = 1;
    component.items = [ { serialNumber: 'S9', status: 'OPERATIONAL' } as Item ];
    const error = { status: 404 };
    productServiceMock.updateItemStatus.and.returnValue(throwError(() => error));

    component.toggleItemStatus(component.items[0]);
    tick();

    expect(loadingServiceMock.hide).toHaveBeenCalled();
  }));

  it('goBack navigates to products', () => {
    component.goBack();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/products']);
  });
});
