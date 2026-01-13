import {Injectable, signal} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Product} from '../models/product/product.model';
import {Item} from '../models/item/item.model';
import {CreateAlertRequest} from '../models/product/create-alert-request.model';
import {Alert} from '../models/product/alert.model';
import {AuthService} from './auth.service'
import {environment} from '@app/environments/environment';
import {PageResponse} from '@app/core/models/pagination/page-response.model';


@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private readonly baseUrl = environment.apiUrl;
  product = signal<Product[]>([]);

  constructor(private readonly http: HttpClient, private readonly authService: AuthService) {}

  createProduct(product: Product): Observable<number> {
    const url = `${this.baseUrl}/products`;
    const token = this.authService.getToken();

    return this.http.post<number>(url, product, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    })
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    const url = `${this.baseUrl}/products/${id}`;
    const token = this.authService.getToken();

    return this.http.put<Product>(url, product, {
      headers: {Authorization: `Bearer ${token || ''}`},
    });
  }

  getProducts(
    filterTerm: string,
    pageIndex: number,
    pageSize: number,
    sort: string = 'name,asc'
  ): Observable<PageResponse<Product>> {

    let params = new HttpParams()
      .set('filterTerm', filterTerm)
      .set('page', pageIndex.toString())
      .set('size', pageSize.toString())
      .set('sort', sort);

    return this.http.get<PageResponse<Product>>(`${this.baseUrl}/products/search`, { params });
  }

  getProductById(id: number): Observable<Product> {
    const url = `${this.baseUrl}/products/${id}`;
    return this.http.get<Product>(url);
  }

  createAvailabilityAlert(request: CreateAlertRequest): Observable<void> {
    const url = `${this.baseUrl}/alerts`;
    const token = this.authService.getToken();
    return this.http.post<void>(url, request, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  getAlerts(): Observable<Alert[]> {
    const url = `${this.baseUrl}/alerts`;
    const token = this.authService.getToken();
    return this.http.get<Alert[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  deleteAlert(id: number): Observable<void> {
    const url = `${this.baseUrl}/alerts/${id}`;
    const token = this.authService.getToken();
    return this.http.delete<void>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  getAlertsByCurrentUser(): Observable<Alert[]> {
    const url = `${this.baseUrl}/alerts/byCurrentUser`;
    const token = this.authService.getToken();

    return this.http.get<Alert[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  getAllProducts(): Observable<Product[]> {
      const url = `${this.baseUrl}/products`;
      return this.http.get<Product[]>(url).pipe(
        tap((products: Product[]) => {
          this.product.set(products);
        })
      );
    }

  getItemsByProductId(productId: number): Observable<Item[]> {
    const url = `${this.baseUrl}/items/product/${productId}`;
    const token = this.authService.getToken();

    return this.http.get<Item[]>(url, {headers: {Authorization: `Bearer ${token || ''}`}});
  }

  updateItemStatus(serialNumber: string, operational: boolean): Observable<Item> {
    const url = `${this.baseUrl}/items/${serialNumber}`;
    const token = this.authService.getToken();

    return this.http.patch<Item>(url, operational, {
      headers: {
        Authorization: `Bearer ${token || ''}`,
        'Content-Type': 'application/json'
      },
    });
  }

  createItem(productId: number, serialNumber: string): Observable<string> {
    const url = `${this.baseUrl}/items`;
    const token = this.authService.getToken();

    const request: { productId: number; serialNumber: string } = {productId, serialNumber};

    return this.http.post(
      url,
      request,
      {
        headers: {Authorization: `Bearer ${token || ''}`},
        responseType: 'text'
      }
    );
  }

  getProductsWithAvailableUnits(): Observable<Product[]> {
    const url = `${this.baseUrl}/products/available`;
    return this.http.get<Product[]>(url);
  }

  deleteProduct(id: number): Observable<void> {
    const url = `${this.baseUrl}/products/${id}`;
    const token = this.authService.getToken();
    return this.http.delete<void>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }
}
