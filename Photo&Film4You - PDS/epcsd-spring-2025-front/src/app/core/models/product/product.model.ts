export interface Product {
  id?: number;
  name: string;
  description: string;
  dailyPrice: number;
  brand: string;
  model: string;
  categoryId: number;
  unitsAvailable?: number;
}
