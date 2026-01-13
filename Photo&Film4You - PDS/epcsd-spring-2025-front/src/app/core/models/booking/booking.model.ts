import {Product} from '../product/product.model';

export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  REJECTED = 'REJECTED'
}

export interface BookingLine {
  id: number;
  quantity: number;
  pricePerUnit: string;
  totalPrice: string;
  product: Product;
}
export interface ItemAllocation {
  id: number;
  itemSerialNumber: string;
}

export interface Booking {
  id: number;
  userId: number;

  startDate: string;
  endDate: string;

  status: BookingStatus;

  lines: BookingLine[];
  allocations: ItemAllocation[];
}
