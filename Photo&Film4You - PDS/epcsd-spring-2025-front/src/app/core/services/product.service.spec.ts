import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {ProductService} from '@app/core/services/product.service';
import {Product} from '../models/product/product.model';
import {AuthService} from './auth.service';
import {environment} from '@app/environments/environment';
import {PageResponse} from '@app/core/models/pagination/page-response.model';
import {CreateAlertRequest} from '../models/product/create-alert-request.model';
import {Alert} from '../models/product/alert.model';
import {Item} from '../models/item/item.model';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;
  const fakeToken = 'fake-jwt-token';
  const apiUrl = environment.apiUrl;

  const mockPageResponse: PageResponse<Product> = {
    content: [
      {id: 1, name: 'A', dailyPrice: 5} as any,
      {id: 2, name: 'B', dailyPrice: 7} as any
    ],
    totalElements: 20,
    totalPages: 2,
    number: 0,
    size: 10,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {provide: AuthService, useValue: {getToken: () => fakeToken}}
      ]
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('createProduct should POST to /products with Authorization header and return id', () => {
    const newProduct: Product = {id: 0, name: 'Test', dailyPrice: 10} as any;
    const mockId = 123;

    let respId: number | undefined;
    service.createProduct(newProduct).subscribe(id => {
      respId = id;
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/products`);
    expect(req.request.method).toBe('POST');
    const authHeader = req.request.headers.get('Authorization');
    expect(authHeader).toBe(`Bearer ${fakeToken}`);
    expect(req.request.body).toEqual(newProduct);

    req.flush(mockId);
    expect(respId).toBe(mockId);
  });

  it('getProducts should GET products using /search with correct query parameters', () => {

    const filterTerm = 'canon';
    const pageIndex = 1;
    const pageSize = 15;
    const sort = 'price,desc';

    let resp: PageResponse<Product> | undefined;
    service.getProducts(filterTerm, pageIndex, pageSize, sort).subscribe(res => {
      resp = res;
    });

    const expectedUrl =
      `${apiUrl}/products/search?filterTerm=${filterTerm}&page=${pageIndex}&size=${pageSize}&sort=${sort}`;

    const req = httpMock.expectOne(expectedUrl);
    expect(req.request.method).toBe('GET');

    req.flush(mockPageResponse);
    expect(resp).toEqual(mockPageResponse);
  });

  it('getProductById should GET product by id', () => {
    const mockProduct: Product = {id: 1, name: 'Test Product', dailyPrice: 10} as any;
    const productId = 1;

    service.getProductById(productId).subscribe(product => {
      expect(product).toEqual(mockProduct);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/${productId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProduct);
  });

  it('createAvailabilityAlert should POST to /alerts', () => {
    const request: CreateAlertRequest = {userId: 1, productId: 1, from: '2023-01-01', to: '2023-01-05'};

    service.createAvailabilityAlert(request).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/alerts`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    expect(req.request.body).toEqual(request);
    req.flush(null);
  });

  it('getAlerts should GET all alerts', () => {
    const mockAlerts: Alert[] = [{id: 1, productId: 1, userId: 1, from: '2023-01-01', to: '2023-01-05'}];

    service.getAlerts().subscribe(alerts => {
      expect(alerts).toEqual(mockAlerts);
    });

    const req = httpMock.expectOne(`${apiUrl}/alerts`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockAlerts);
  });

  it('deleteAlert should DELETE alert by id', () => {
    const alertId = 1;

    service.deleteAlert(alertId).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/alerts/${alertId}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(null);
  });

  it('getAlertsByCurrentUser should GET alerts for current user', () => {
    const mockAlerts: Alert[] = [{id: 1, productId: 1, userId: 1, from: '2023-01-01', to: '2023-01-05'}];

    service.getAlertsByCurrentUser().subscribe(alerts => {
      expect(alerts).toEqual(mockAlerts);
    });

    const req = httpMock.expectOne(`${apiUrl}/alerts/byCurrentUser`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockAlerts);
  });

  it('getAllProducts should GET all products and update signal', () => {
    const mockProducts: Product[] = [{id: 1, name: 'P1', dailyPrice: 10} as any];

    service.getAllProducts().subscribe(products => {
      expect(products).toEqual(mockProducts);
      expect(service.product()).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/products`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('getItemsByProductId should GET items for a product', () => {
    const productId = 1;
    const mockItems: Item[] = [{serialNumber: 'SN1', operational: true} as any];

    service.getItemsByProductId(productId).subscribe(items => {
      expect(items).toEqual(mockItems);
    });

    const req = httpMock.expectOne(`${apiUrl}/items/product/${productId}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockItems);
  });

  it('updateItemStatus should PATCH item status', () => {
    const serialNumber = 'SN1';
    const operational = false;
    const mockItem: Item = {serialNumber, operational} as any;

    service.updateItemStatus(serialNumber, operational).subscribe(item => {
      expect(item).toEqual(mockItem);
    });

    const req = httpMock.expectOne(`${apiUrl}/items/${serialNumber}`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    expect(req.request.body).toBe(operational);
    req.flush(mockItem);
  });

  it('createItem should POST to /items', () => {
    const productId = 1;
    const serialNumber = 'SN1';
    const responseText = 'Item created';

    service.createItem(productId, serialNumber).subscribe(res => {
      expect(res).toBe(responseText);
    });

    const req = httpMock.expectOne(`${apiUrl}/items`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    expect(req.request.body).toEqual({productId, serialNumber});
    req.flush(responseText);
  });

  it('getProductsWithAvailableUnits should GET available products', () => {
    const mockProducts: Product[] = [{id: 1, name: 'P1', dailyPrice: 10} as any];

    service.getProductsWithAvailableUnits().subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/available`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });
});
