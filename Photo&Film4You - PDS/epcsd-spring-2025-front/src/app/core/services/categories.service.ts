import {Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Category} from '../models/categories/categorie.model';
import {environment} from '@app/environments/environment';
import {AuthService} from '@app/core/services/auth.service';


@Injectable({
  providedIn: 'root',
})
export class CategoriesService {
  private readonly baseUrl = environment.apiUrl;
  categories = signal<Category[]>([]);

  constructor(
    private readonly http: HttpClient,
    private readonly auth: AuthService,
  ) {
  }

  getCategories(): Observable<Category[]> {
    const url = `${this.baseUrl}/categories`;
    return this.http.get<Category[]>(url).pipe(
      tap((cats) => {
        this.categories.set(cats);
      })
    );
  }

  createCategory(category: Category): Observable<number> {
    const url = `${this.baseUrl}/categories`;
    const token = this.auth.getToken();

    return this.http.post<number>(url, category, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }
}
