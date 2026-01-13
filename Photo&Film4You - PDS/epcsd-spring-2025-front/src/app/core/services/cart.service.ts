import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {BookingCart, BookingCartInterface} from '@app/core/models/cart';
import {Product} from '@app/core/models/product/product.model';
import {DateRange} from '@app/core/models/date/date-range';

@Injectable({
  providedIn: 'root',
})
export class CartService {

  private readonly cartStorageKey = 'booking_cart';

  private readonly cartSubject = new BehaviorSubject<BookingCart>(new BookingCart())
  cart$ = this.cartSubject.asObservable();

  constructor() {
    this.loadFromStorage();
  }

  setDates(range: DateRange): void {
    const cart = this.cartSubject.value;
    cart.startDate = range.start;
    cart.endDate = range.end;
    this.updateCart(cart);
  }

  addProduct(product: Product, quantity: number = 1): void {
    const cart = this.cartSubject.value;
    const item = cart.items.find(i => i.product.id === product.id);

    if (item) {
      item.quantity += quantity;
    } else {
      cart.items.push({product, quantity: quantity});
    }

    this.updateCart(cart);
  }

  getItemQuantity(product: Product): number {
    const cart = this.cartSubject.value;
    const item = cart.items.find(i => i.product.id === product.id);
    return item ? item.quantity : 0;
  }

removeProduct(product: Product, quantity?: number): void {
    const cart = this.cartSubject.value;
    const item = cart.items.find(i => i.product.id === product.id);

    if (quantity && item) {
      item.quantity -= quantity;
    }

    if (!quantity || (item && item.quantity <= 0)) {
      cart.items = cart.items.filter(i => i.product.id !== product.id);
    }

    this.updateCart(cart);
  }

  clear(): void {
    this.updateCart(new BookingCart());
  }

  private updateCart(cart: BookingCart): void {
    this.cartSubject.next(cart);
    sessionStorage.setItem(this.cartStorageKey, JSON.stringify(cart));
  }

  private loadFromStorage(): void {
    const data = sessionStorage.getItem(this.cartStorageKey);
    if (data) {
      const cart: BookingCartInterface = JSON.parse(data);
      this.cartSubject.next(new BookingCart(cart));
    }
  }

}
