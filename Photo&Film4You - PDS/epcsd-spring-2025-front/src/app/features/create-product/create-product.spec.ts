import {ComponentFixture, fakeAsync, flushMicrotasks, TestBed} from '@angular/core/testing';
import {CreateProduct} from './create-product';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {CategoriesService} from '@app/core/services/categories.service';
import {ProductService} from '@app/core/services/product.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, convertToParamMap, Router, UrlTree} from '@angular/router';
import {BehaviorSubject, of, throwError} from 'rxjs';
import {Category} from '@app/core/models/categories/categorie.model';
import {AuthService} from '@app/core/services/auth.service';
import {HttpClient} from '@angular/common/http';
import {MatDialog} from '@angular/material/dialog';

describe('CreateProduct', () => {
  let component: CreateProduct;
  let fixture: ComponentFixture<CreateProduct>;
  let categoriesServiceMock: jasmine.SpyObj<CategoriesService>;
  let productServiceMock: jasmine.SpyObj<ProductService>;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;
  let routerMock: jasmine.SpyObj<Router>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let httpClientMock: jasmine.SpyObj<HttpClient>;
  let dialogMock: jasmine.SpyObj<MatDialog>;
  let paramMap$: BehaviorSubject<any>;


  beforeEach(async () => {
    categoriesServiceMock = jasmine.createSpyObj('CategoriesService', ['getCategories']);
    productServiceMock = jasmine.createSpyObj('ProductService', ['createProduct', 'updateProduct', 'getProductById']);
    snackBarMock = jasmine.createSpyObj('MatSnackBar', ['open']);
    authServiceMock = jasmine.createSpyObj('AuthService', ['currentUser']);
    httpClientMock = jasmine.createSpyObj('HttpClient', ['get', 'post', 'put', 'delete']);
    dialogMock = jasmine.createSpyObj('MatDialog', ['open']);

    routerMock = Object.assign(
      jasmine.createSpyObj<Router>('Router', [
        'navigate',
        'navigateByUrl',
        'createUrlTree',
        'serializeUrl'
      ]),
      { events: of() }
    );

    routerMock.navigate.and.returnValue(Promise.resolve(true));
    routerMock.createUrlTree.and.returnValue({} as unknown as UrlTree);
    routerMock.serializeUrl.and.returnValue('/');
    categoriesServiceMock.getCategories.and.returnValue(of([]));
    authServiceMock.currentUser.and.returnValue(null);

    paramMap$ = new BehaviorSubject(convertToParamMap({}));

    TestBed.overrideComponent(CreateProduct, {
      add: {
        providers: [
          { provide: CategoriesService, useValue: categoriesServiceMock },
          {provide: ProductService, useValue: productServiceMock},
          {provide: MatSnackBar, useValue: snackBarMock},
          {provide: Router, useValue: routerMock},
          {provide: AuthService, useValue: authServiceMock},
          {provide: MatDialog, useValue: dialogMock},
          {
            provide: ActivatedRoute,
            useValue: {paramMap: paramMap$.asObservable()} as Partial<ActivatedRoute>
          }
        ]
      }
    });

    await TestBed.configureTestingModule({
      imports: [CreateProduct, ReactiveFormsModule],
      providers: [
        FormBuilder,
        {provide: HttpClient, useValue: httpClientMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateProduct);
    component = fixture.componentInstance;
    categoriesServiceMock.getCategories.and.returnValue(of([]));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debería crearse y cargar categorías en ngOnInit', () => {
    const mockCategories: Category[] = [{ id: 1, name: 'Cat 1' } as Category];
    categoriesServiceMock.getCategories.and.returnValue(of(mockCategories));

    component.ngOnInit();

    expect(categoriesServiceMock.getCategories).toHaveBeenCalled();
    expect(component.categories).toEqual(mockCategories);
    expect(component.productForm).toBeTruthy();
  });

  it('debería crear producto correctamente si el formulario es válido', async () => {
    component.productForm.setValue({
      nombre: 'Prod 1',
      descripcion: 'Desc',
      precioDiario: 10,
      marca: 'Marca',
      modelo: 'Modelo',
      categoria: 1
    });
    expect(component.productForm.valid).toBeTrue();

    productServiceMock.createProduct.and.returnValue(of(1));

    component.onSubmit();

    expect(productServiceMock.createProduct).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/products']);
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Producto creado correctamente',
      'Cerrar',
      jasmine.objectContaining({ panelClass: ['snackbar-success'] })
    );
  });

  it('debería mostrar error si createProduct devuelve error', () => {
    component.productForm.setValue({
      nombre: 'Prod 1',
      descripcion: 'Desc',
      precioDiario: 10,
      marca: 'Marca',
      modelo: 'Modelo',
      categoria: 1
    });

    productServiceMock.createProduct.and.returnValue(throwError(() => new Error('Error')));

    component.onSubmit();

    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Error al crear el producto.',
      'Cerrar',
      jasmine.objectContaining({ panelClass: ['snackbar-error'] })
    );
  });

  it('debería mostrar error y marcar campos si el formulario es inválido', fakeAsync(() => {
    component.productForm.setValue({
      nombre: '',
      descripcion: '',
      precioDiario: '',
      marca: '',
      modelo: '',
      categoria: ''
    });

    spyOn(component.productForm, 'markAllAsTouched');

    component.onSubmit();

    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Error al crear el producto.',
      'Cerrar',
      jasmine.objectContaining({panelClass: ['snackbar-error']})
    );

    flushMicrotasks();
    expect(component.productForm.markAllAsTouched).toHaveBeenCalled();
  }));

  it('en modo Editar, debería cargar el producto por id', fakeAsync(() => {
    const productFromApi = {
      id: 8,
      name: 'Prod API',
      description: 'Desc API',
      dailyPrice: 99,
      brand: 'Brand API',
      model: 'Model API',
      categoryId: 2
    };

    productServiceMock.getProductById.and.returnValue(of(productFromApi as any));

    paramMap$.next(convertToParamMap({id: '8'}));
    flushMicrotasks();

    expect(component.isEditMode).toBeTrue();
    expect(component.productId).toBe(8);
    expect(productServiceMock.getProductById).toHaveBeenCalledWith(8);

    expect(component.productForm.value).toEqual(jasmine.objectContaining({
      nombre: 'Prod API',
      descripcion: 'Desc API',
      precioDiario: 99,
      marca: 'Brand API',
      modelo: 'Model API',
      categoria: 2
    }));
  }));

  it('en modo Editar, debería actualizar producto correctamente si el formulario es válido', fakeAsync(() => {
    productServiceMock.getProductById.and.returnValue(of({
      id: 8,
      name: 'Old',
      description: 'Old',
      dailyPrice: 1,
      brand: 'Old',
      model: 'Old',
      categoryId: 1
    } as any));

    paramMap$.next(convertToParamMap({id: '8'}));
    flushMicrotasks();

    component.productForm.setValue({
      nombre: 'Nuevo',
      descripcion: 'Nueva desc',
      precioDiario: 10,
      marca: 'Nueva marca',
      modelo: 'Nuevo modelo',
      categoria: 3
    });

    productServiceMock.updateProduct.and.returnValue(of({} as any));

    component.onSubmit();
    flushMicrotasks();

    expect(productServiceMock.updateProduct).toHaveBeenCalledWith(8, jasmine.objectContaining({
      name: 'Nuevo',
      description: 'Nueva desc',
      dailyPrice: 10,
      brand: 'Nueva marca',
      model: 'Nuevo modelo',
      categoryId: 3
    }));

    expect(routerMock.navigate).toHaveBeenCalledWith(['/products']);
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Producto actualizado correctamente',
      'Cerrar',
      jasmine.objectContaining({panelClass: ['snackbar-success']})
    );
  }));

  it('en modo Editar, debería mostrar error si updateProduct devuelve error', fakeAsync(() => {
    productServiceMock.getProductById.and.returnValue(of({
      id: 8,
      name: 'Old',
      description: 'Old',
      dailyPrice: 1,
      brand: 'Old',
      model: 'Old',
      categoryId: 1
    } as any));

    paramMap$.next(convertToParamMap({id: '8'}));
    flushMicrotasks();

    component.productForm.setValue({
      nombre: 'Nuevo',
      descripcion: 'Nueva desc',
      precioDiario: 10,
      marca: 'Nueva marca',
      modelo: 'Nuevo modelo',
      categoria: 3
    });

    productServiceMock.updateProduct.and.returnValue(throwError(() => new Error('Error update')));

    component.onSubmit();

    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Error al actualizar el producto.',
      'Cerrar',
      jasmine.objectContaining({panelClass: ['snackbar-error']})
    );
  }));

});
