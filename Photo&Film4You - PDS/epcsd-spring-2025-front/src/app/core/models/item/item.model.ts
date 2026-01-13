export interface Item {
  serialNumber: string;
  status: 'OPERATIONAL' | 'NON_OPERATIONAL';
  productId: number;
}
