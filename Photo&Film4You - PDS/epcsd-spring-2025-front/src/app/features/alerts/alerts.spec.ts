import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Alerts} from './alerts';
import {ProductService} from '@app/core/services/product.service';
import {LoadingService} from '@app/core/services/loading-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {of, throwError} from 'rxjs';
import {Alert} from '@app/core/models/product/alert.model';
import {Product} from '@app/core/models/product/product.model';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('Alerts', () => {
  let component: Alerts;
  let fixture: ComponentFixture<Alerts>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockAlerts: Alert[] = [
    {id: 1, from: '2023-01-01', to: '2023-01-05', productId: 101, userId: 1},
    {id: 2, from: '2023-02-01', to: '2023-02-05', productId: 102, userId: 1}
  ];

  const mockProducts: Product[] = [
    {
      id: 101,
      name: 'Product A',
      description: 'Desc A',
      dailyPrice: 10,
      brand: 'Brand A',
      model: 'Model A',
      categoryId: 1
    },
    {
      id: 102,
      name: 'Product B',
      description: 'Desc B',
      dailyPrice: 20,
      brand: 'Brand B',
      model: 'Model B',
      categoryId: 2
    }
  ];

  beforeEach(async () => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getAlertsByCurrentUser', 'getProductById', 'deleteAlert']);
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [Alerts],
      providers: [
        {provide: ProductService, useValue: productServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy},
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Alerts);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load alerts and products on init', () => {
    productServiceSpy.getAlertsByCurrentUser.and.returnValue(of(mockAlerts));
    productServiceSpy.getProductById.withArgs(101).and.returnValue(of(mockProducts[0]));
    productServiceSpy.getProductById.withArgs(102).and.returnValue(of(mockProducts[1]));

    // triggers ngOnInit
    fixture.detectChanges();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(productServiceSpy.getAlertsByCurrentUser).toHaveBeenCalled();
    expect(productServiceSpy.getProductById).toHaveBeenCalledTimes(2);
    expect(component.alerts.length).toBe(2);
    expect(component.alerts[0].productName).toBe('Product A');
    expect(component.alerts[1].productName).toBe('Product B');
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
  });

  it('should handle empty alerts list', () => {
    productServiceSpy.getAlertsByCurrentUser.and.returnValue(of([]));

    fixture.detectChanges();

    expect(component.alerts).toEqual([]);
    expect(productServiceSpy.getProductById).not.toHaveBeenCalled();
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
  });

  it('should handle error when loading alerts', () => {
    const error = new Error('Network error');
    productServiceSpy.getAlertsByCurrentUser.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith(error);
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
    expect(snackBarSpy.open).toHaveBeenCalledWith('Error al cargar alertas', 'Cerrar', jasmine.any(Object));
  });

  it('should handle error when loading products details', () => {
    productServiceSpy.getAlertsByCurrentUser.and.returnValue(of(mockAlerts));
    productServiceSpy.getProductById.and.returnValue(throwError(() => new Error('Product error')));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Error fetching products for alerts', jasmine.any(Error));
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
    expect(snackBarSpy.open).toHaveBeenCalledWith('Error al cargar detalles de productos', 'Cerrar', jasmine.any(Object));
  });

  it('should delete alert successfully', () => {
    // Setup initial state
    component.alerts = [
      {...mockAlerts[0], productName: 'Product A'},
      {...mockAlerts[1], productName: 'Product B'}
    ];

    spyOn(window, 'confirm').and.returnValue(true);
    productServiceSpy.deleteAlert.and.returnValue(of(void 0));

    component.deleteAlert(component.alerts[0]);

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(productServiceSpy.deleteAlert).toHaveBeenCalledWith(mockAlerts[0].id);
    expect(component.alerts.length).toBe(1);
    expect(component.alerts[0].id).toBe(mockAlerts[1].id);
    expect(snackBarSpy.open).toHaveBeenCalledWith('Alerta eliminada correctamente', 'Cerrar', jasmine.any(Object));
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
  });

  it('should not delete alert if user cancels confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(false);

    component.deleteAlert({...mockAlerts[0], productName: 'Product A'});

    expect(productServiceSpy.deleteAlert).not.toHaveBeenCalled();
  });

  it('should handle error when deleting alert', () => {
    component.alerts = [{...mockAlerts[0], productName: 'Product A'}];
    spyOn(window, 'confirm').and.returnValue(true);
    productServiceSpy.deleteAlert.and.returnValue(throwError(() => new Error('Delete error')));
    spyOn(console, 'error');

    component.deleteAlert(component.alerts[0]);

    expect(console.error).toHaveBeenCalledWith('Error deleting alert', jasmine.any(Error));
    expect(snackBarSpy.open).toHaveBeenCalledWith('Error al eliminar la alerta', 'Cerrar', jasmine.any(Object));
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
    expect(component.alerts.length).toBe(1);
  });
});
