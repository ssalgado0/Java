export interface Notification {
  id: number;
  title: string;
  message: string;
  createdAt: Date;
  read: boolean;
  type?: 'info' | 'warning' | 'success' | 'error';
  entity: 'DIGITAL_ITEM' | 'GENERAL';
}
