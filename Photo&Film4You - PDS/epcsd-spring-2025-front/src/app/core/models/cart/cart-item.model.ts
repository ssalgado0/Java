import {Product} from '@app/core/models/product/product.model';

export interface CartItem {
  product: Product;
  quantity: number;
}
