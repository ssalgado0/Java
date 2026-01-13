import {ComponentFixture, fakeAsync, flush, TestBed, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Products} from './products';
import {provideNativeDateAdapter} from '@angular/material/core';
import {ActivatedRoute} from '@angular/router';
import {ProductService} from '@app/core/services/product.service';
import {LoadingService} from '@app/core/services/loading-service';
import {BookingService} from '@app/core/services/booking.service';
import {CartService} from '@app/core/services/cart.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {BehaviorSubject, of, throwError} from 'rxjs';
import {Product} from '@app/core/models/product/product.model';
import {AuthService} from '@app/core/services/auth.service';
import {DateRange} from '@app/core/models/date/date-range';
import {PageResponse} from '@app/core/models/pagination/page-response.model';
import {PageEvent} from '@angular/material/paginator';
import {AvailabilityResponse} from '@app/core/models/booking';
import {signal, WritableSignal} from '@angular/core';
import {CurrentUser} from '@app/core/models/auth';
import {UserRole} from '@app/core/enums/user-role.enum';
import {MatDialog} from '@angular/material/dialog';

const mockPageResponse: PageResponse<Product> = {
  content: [{ id: 1, name: 'Product A' } as Product, { id: 2, name: 'Product B' } as Product],
  totalElements: 50,
  totalPages: 5,
  number: 0,
  size: 10,
};

const mockAvailability: AvailabilityResponse = {
  startDate: new Date(),
  endDate: new Date(),
  availableUnitsByProduct: {
    1: 5,
    2: 0
  }
};

describe('Products', () => {
  let component: Products;
  let fixture: ComponentFixture<Products>;

  let productServiceMock: jasmine.SpyObj<ProductService>;
  let loadingServiceMock: jasmine.SpyObj<LoadingService>;
  let bookingServiceMock: any;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;
  let cartServiceMock: jasmine.SpyObj<CartService>;
  let dialogMock: jasmine.SpyObj<MatDialog>;

  let dateRangeSubject: BehaviorSubject<DateRange | null>;
  let authUserSignal: WritableSignal<CurrentUser | null>;

  beforeEach(async () => {
    productServiceMock = jasmine.createSpyObj<ProductService>('ProductService', ['getProducts', 'createAvailabilityAlert', 'deleteProduct']);
    loadingServiceMock = jasmine.createSpyObj<LoadingService>('LoadingService', ['show', 'hide']);

    bookingServiceMock = jasmine.createSpyObj<BookingService>('BookingService', ['getActualDateRange', 'resetDateRange', 'checkAvailability', 'setDateRange']);
    dateRangeSubject = new BehaviorSubject<DateRange | null>(null);
    bookingServiceMock.dateRange$ = dateRangeSubject.asObservable();
    bookingServiceMock.getActualDateRange.and.returnValue(null);

    snackBarMock = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);
    cartServiceMock = jasmine.createSpyObj<CartService>('CartService', ['addProduct', 'clear', 'getItemQuantity']);
    dialogMock = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);

    authUserSignal = signal(null);

    await TestBed.configureTestingModule({
      imports: [Products, HttpClientTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => null } } } },
        {provide: AuthService, useValue: {currentUser: authUserSignal}},
        {provide: ProductService, useValue: productServiceMock},
        {provide: LoadingService, useValue: loadingServiceMock},
        {provide: BookingService, useValue: bookingServiceMock},
        {provide: MatSnackBar, useValue: snackBarMock},
        {provide: CartService, useValue: cartServiceMock},
        {provide: MatDialog, useValue: dialogMock},
        provideNativeDateAdapter()
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(Products);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it("should load products on init", fakeAsync(() => {
    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));
    fixture.detectChanges();
    tick(0);

    expect(productServiceMock.getProducts).toHaveBeenCalledWith('', 0, 12);
    expect(component.isProductsLoading()).toBeFalse();
    expect(component.filteredProducts().length).toBe(2);
    flush();
  }));

  it("should update filter term and trigger fetch", fakeAsync(() => {
    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));
    fixture.detectChanges();
    tick(0);

    productServiceMock.getProducts.calls.reset();

    const newTerm = 'canon';
    component.onFilterTermChange(newTerm);
    fixture.detectChanges();
    tick(0);

    expect(component.filterTerm()).toBe(newTerm);
    expect(productServiceMock.getProducts).toHaveBeenCalledWith(newTerm, 0, 12);
    flush();
  }));

  it("should update page index and trigger fetch", fakeAsync(() => {
    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));
    fixture.detectChanges();
    tick(0);

    productServiceMock.getProducts.calls.reset();

    const pageEvent: PageEvent = { pageIndex: 1, pageSize: 12, length: 50 };
    component.onPaginatorChange(pageEvent);
    fixture.detectChanges();
    tick(0);

    expect(component.pageIndex()).toBe(1);
    expect(productServiceMock.getProducts).toHaveBeenCalledWith('', 1, 12);
    flush();
  }));

  it("should show snackbar on error loading products", fakeAsync(() => {
    productServiceMock.getProducts.and.returnValue(throwError(() => new Error("Search failed")));
    fixture.detectChanges();
    tick(0);

    expect(snackBarMock.open).toHaveBeenCalledWith(
      "Error al cargar o filtrar productos", "Cerrar", jasmine.any(Object)
    );
    expect(component.isProductsLoading()).toBeFalse();
    flush();
  }));

  it('should show snackbar on error checking availability', fakeAsync(() => {
    spyOn(console, 'error');

    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));
    fixture.detectChanges();
    tick(0);

    const range: DateRange = {start: new Date(), end: new Date()};
    bookingServiceMock.checkAvailability.and.returnValue(throwError(() => new Error('Error')));

    component.onRangeSelected(range);
    tick(0);

    expect(snackBarMock.open).toHaveBeenCalledWith(
      "Ha ocurrido un error al comprobar la disponibilidad", "Cerrar", jasmine.any(Object)
    );
    expect(loadingServiceMock.hide).toHaveBeenCalled();
    flush();
  }));

  it('should check availability when a range is selected', fakeAsync(() => {
    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));
    bookingServiceMock.checkAvailability.and.returnValue(of(mockAvailability));
    fixture.detectChanges();
    tick(0);

    const range: DateRange = {start: new Date(), end: new Date()};
    component.onRangeSelected(range);
    tick(0);

    expect(bookingServiceMock.setDateRange).toHaveBeenCalledWith(range);
    expect(loadingServiceMock.show).toHaveBeenCalled();
    expect(component.availability).toEqual(mockAvailability);
    expect(loadingServiceMock.hide).toHaveBeenCalled();
    flush();
  }));

  it('should reset range, cart and availability on range reset', () => {
    component.availability = mockAvailability;
    component.onRangeReseted();

    expect(bookingServiceMock.resetDateRange).toHaveBeenCalled();
    expect(cartServiceMock.clear).toHaveBeenCalled();
    expect(component.availability).toBeNull();
  });

  it('should add product to cart', () => {
    const product = mockPageResponse.content[0];
    component.addProduct(product);
    expect(cartServiceMock.addProduct).toHaveBeenCalledWith(product);
  });

  it('should get availability for a specific product', () => {
    component.availability = mockAvailability;

    expect(component.getAvailability(mockPageResponse.content[0])).toBe(5);
    expect(component.getAvailability(mockPageResponse.content[1])).toBe(0);
  });

  it('should return "Desconocido" if availability is null', () => {
    component.availability = null;
    expect(component.getAvailability(mockPageResponse.content[0])).toBe('Desconocido');
  });

  it('should return false for isPossibleToAddItem if availability is unknown', () => {
    component.availability = null;
    expect(component.isPossibleToAddItem(mockPageResponse.content[0])).toBeFalse();
  });

  it('should validate if it is possible to add item based on cart quantity', () => {
    component.availability = mockAvailability;
    const product = mockPageResponse.content[0];

    cartServiceMock.getItemQuantity.and.returnValue(2);
    expect(component.isPossibleToAddItem(product)).toBeTrue();

    cartServiceMock.getItemQuantity.and.returnValue(5);
    expect(component.isPossibleToAddItem(product)).toBeFalse();
  });

  it('should re-check availability after fetching products if dateRange is already set', fakeAsync(() => {
    const range: DateRange = {start: new Date(), end: new Date()};
    dateRangeSubject.next(range);

    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));
    bookingServiceMock.checkAvailability.and.returnValue(of(mockAvailability));

    fixture.detectChanges();
    tick(0);

    expect(bookingServiceMock.checkAvailability).toHaveBeenCalled();
    expect(component.availability).toEqual(mockAvailability);

    flush();
  }));

  it('should create alert successfully', fakeAsync(() => {
    const product = mockPageResponse.content[0];
    const range: DateRange = {start: new Date(), end: new Date()};
    component.dateRange = range;

    authUserSignal.set({id: '1', email: 'test@test.com', fullName: 'Test', role: UserRole.USER});

    productServiceMock.createAvailabilityAlert.and.returnValue(of(void 0));

    component.createAlert(product);
    tick();

    expect(productServiceMock.createAvailabilityAlert).toHaveBeenCalled();
    expect(snackBarMock.open).toHaveBeenCalledWith("Se ha creado la alerta", "Cerrar", jasmine.any(Object));
  }));

  it('should handle error when creating alert', fakeAsync(() => {
    const product = mockPageResponse.content[0];
    const range: DateRange = {start: new Date(), end: new Date()};
    component.dateRange = range;

    authUserSignal.set({id: '1', email: 'test@test.com', fullName: 'Test', role: UserRole.USER});

    productServiceMock.createAvailabilityAlert.and.returnValue(throwError(() => new Error('Error')));
    spyOn(console, 'error');

    component.createAlert(product);
    tick();

    expect(productServiceMock.createAvailabilityAlert).toHaveBeenCalled();
    expect(snackBarMock.open).toHaveBeenCalledWith("Error al crear la alerta", "Cerrar", jasmine.any(Object));
  }));

  it('should not create alert if no date range', () => {
    const product = mockPageResponse.content[0];
    component.dateRange = null;
    authUserSignal.set({id: '1', email: 'test@test.com', fullName: 'Test', role: UserRole.USER});

    component.createAlert(product);

    expect(productServiceMock.createAvailabilityAlert).not.toHaveBeenCalled();
  });

  it('should not create alert if no current user', () => {
    const product = mockPageResponse.content[0];
    const range: DateRange = {start: new Date(), end: new Date()};
    component.dateRange = range;
    authUserSignal.set(null);

    component.createAlert(product);

    expect(productServiceMock.createAvailabilityAlert).not.toHaveBeenCalled();
  });

  it('should not show edit button for USER', fakeAsync(() => {
    authUserSignal.set({id: '2', email: 'user@test.com', fullName: 'User', role: UserRole.USER});

    productServiceMock.getProducts.and.returnValue(of(mockPageResponse));

    fixture.detectChanges();
    tick(0);
    fixture.detectChanges();

    const buttons: NodeListOf<HTMLElement> = fixture.nativeElement.querySelectorAll('[data-testid="edit-product-button"]');
    expect(buttons.length).toBe(0);
  }));

  it('should delete product successfully', () => {
    const product = {id: 1, name: 'Product A'} as Product;

    const dialogRefSpy = jasmine.createSpyObj({afterClosed: of(true)});
    dialogMock.open.and.returnValue(dialogRefSpy as any);
    productServiceMock.deleteProduct.and.returnValue(of(void 0));
    spyOn<any>(component, 'fetchProducts');

    component.deleteProduct(product);

    expect(dialogMock.open).toHaveBeenCalled();
    expect(productServiceMock.deleteProduct).toHaveBeenCalledWith(1);
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Producto eliminado correctamente',
      'Cerrar',
      {
        duration: 3000,
        panelClass: ['snackbar-success']
      }
    );
  });

  it('should show specific message when deleteProduct receives 409', () => {
    const product = {id: 1, name: 'Product with bookings'} as Product;

    const dialogRefSpy = jasmine.createSpyObj({afterClosed: of(true)});
    dialogMock.open.and.returnValue(dialogRefSpy as any);
    productServiceMock.deleteProduct.and.returnValue(throwError(() => ({status: 409})));

    component.deleteProduct(product);

    expect(productServiceMock.deleteProduct).toHaveBeenCalledWith(1);
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'No se puede eliminar un producto con reservas asociadas',
      'Cerrar',
      {
        duration: 5000,
        panelClass: ['snackbar-error']
      }
    );
  });

  it('should show generic message when deleteProduct receives non-409 error', () => {
    const product = {id: 1, name: 'Other error product'} as Product;

    const dialogRefSpy = jasmine.createSpyObj({afterClosed: of(true)});
    dialogMock.open.and.returnValue(dialogRefSpy as any);
    productServiceMock.deleteProduct.and.returnValue(throwError(() => ({status: 500})));

    component.deleteProduct(product);

    expect(productServiceMock.deleteProduct).toHaveBeenCalledWith(1);
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Error al eliminar el producto',
      'Cerrar',
      {
        duration: 5000,
        panelClass: ['snackbar-error']
      }
    );
  });

});
