import {CartItem} from '@app/core/models/cart/cart-item.model';

export interface BookingCartInterface {
  startDate?: Date;
  endDate?: Date;
  items: CartItem[];
}

export class BookingCart implements BookingCartInterface {
  startDate?: Date;
  endDate?: Date;
  items: CartItem[];

  constructor(cart: BookingCartInterface = {items: []}) {
    this.items = [];
    this.startDate = cart.startDate;
    this.endDate = cart.endDate;
    if (cart.items && cart.items.length > 0) {
      this.items = cart.items;
    }
  }

  countItems(): number {
    return this.items.reduce((total, item) => total + item.quantity, 0);
  }
}
