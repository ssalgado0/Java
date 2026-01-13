import { TestBed } from '@angular/core/testing';
import { CartService } from './cart.service';
import { Product } from '@app/core/models/product/product.model';
import { take } from 'rxjs/operators';


describe('CartService', () => {
  let service: CartService;
  let store: Record<string, string> = {};
  const KEY = 'booking_cart';

  const productA: Product = {
    id: 1,
    name: 'Product A',
    dailyPrice: 10
  } as Product;

  const mockSessionStorage = {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value;
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    }
  };

  beforeEach(() => {
    store = {};
    Object.defineProperty(window, 'sessionStorage', { value: mockSessionStorage });
    TestBed.configureTestingModule({
      providers: [CartService]
    });
  });

  describe('initialization', () => {
    it('should load cart from storage on init', (done) => {
      const storedCart = {
        startDate: new Date(),
        endDate: new Date(),
        items: [{ product: productA, quantity: 2 }]
      };

      store[KEY] = JSON.stringify(storedCart);

      const newService = TestBed.inject(CartService);

      newService.cart$.pipe(take(1)).subscribe(cart => {
        if (cart.items.length > 0) {
          expect(cart.items.length).toBe(1);
          expect(cart.items[0].quantity).toBe(2);
          done();
        }
      });
    });
  });

  describe('runtime methods', () => {
    beforeEach(() => {
      service = TestBed.inject(CartService);
      service.clear(); 
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should set date range correctly', (done) => {
      const range = {
        start: new Date('2024-01-01'),
        end: new Date('2024-01-10')
      };

      service.setDates(range);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.startDate).toEqual(range.start);
        expect(cart.endDate).toEqual(range.end);
        done();
      });
    });

    it('should add a new product to the cart', (done) => {
      service.addProduct(productA);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items.length).toBe(1);
        expect(cart.items[0].product.id).toBe(1);
        expect(cart.items[0].quantity).toBe(1);
        done();
      });
    });

    it('should increase quantity when adding the same product', (done) => {
      service.addProduct(productA);
      service.addProduct(productA, 2);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items[0].quantity).toBe(3);
        done();
      });
    });

    it('should persist cart into storage', (done) => {
      service.addProduct(productA);

      service.cart$.pipe(take(1)).subscribe(() => {
        const stored = JSON.parse(store[KEY]);
        expect(stored.items.length).toBe(1);
        expect(stored.items[0].product.id).toBe(1);
        expect(stored.items[0].quantity).toBe(1);
        done();
      });
    });

    it('should remove product entirely if no quantity is provided', (done) => {
      service.addProduct(productA, 3);
      service.removeProduct(productA);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items.length).toBe(0);
        done();
      });
    });

    it('should decrease product quantity when a positive quantity is provided', (done) => {
      service.addProduct(productA, 3);
      service.removeProduct(productA, 1);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items[0].quantity).toBe(2);
        done();
      });
    });

    it('should remove product if resulting quantity is zero or less', (done) => {
      service.addProduct(productA, 3);
      service.removeProduct(productA, 5);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items.length).toBe(0);
        done();
      });
    });

    it('should clear all items and dates', (done) => {
      service.addProduct(productA);
      service.setDates({ start: new Date(), end: new Date() });

      service.clear();

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items.length).toBe(0);
        expect(cart.startDate).toBeUndefined();
        expect(cart.endDate).toBeUndefined();
        done();
      });
    });


    it('getItemQuantity should return the correct quantity', () => {
      service.addProduct(productA, 5);
      expect(service.getItemQuantity(productA)).toBe(5);
    });

    it('getItemQuantity should return 0 if product is not in cart', () => {
      expect(service.getItemQuantity(productA)).toBe(0);
    });

    it('removeProduct should handle trying to remove a product that is not in cart', (done) => {
      service.removeProduct(productA);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items.length).toBe(0);
        done();
      });
    });

    it('removeProduct with quantity should do nothing if product not found', (done) => {
      service.removeProduct(productA, 5);

      service.cart$.pipe(take(1)).subscribe(cart => {
        expect(cart.items.length).toBe(0);
        done();
      });
    });

    it('should not break if trying to remove quantity of non-existent item', (done) => {
  service.removeProduct(productA, 5); 
  
  service.cart$.subscribe(cart => {
    expect(cart.items.length).toBe(0);
    done();
  });
});

it('should remove item specifically when quantity hits exactly 0', (done) => {
  service.addProduct(productA, 2);
  service.removeProduct(productA, 2);

  service.cart$.subscribe(cart => {
    expect(cart.items.length).toBe(0);
    done();
  });
});
  }); 
});