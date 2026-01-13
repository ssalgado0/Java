import {CartItem} from '@app/core/models/cart/cart-item.model';
import {BookingCart} from '@app/core/models/cart/cart.model';

describe('Cart', () => {
  it('should create an instance', () => {
    expect(new BookingCart()).toBeTruthy();
  });

  it('should count items correctly', () => {
    const cart = new BookingCart();
    const item1: CartItem = {
      product: {
        id: 1,
        name: 'Product 1',
        dailyPrice: 100,
        description: "product 1",
        brand: "brand1",
        model: "model1",
        categoryId: 0
      }, quantity: 2
    };
    const item2: CartItem = {
      product: {
        id: 2,
        name: 'Product 2',
        dailyPrice: 200,
        description: "product 2",
        brand: "brand2",
        model: "model2",
        categoryId: 0
      }, quantity: 3
    };
    cart.items.push(item1, item2);

    expect(cart.countItems()).toBe(5);
  });
})
