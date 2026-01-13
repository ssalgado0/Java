export enum DigitalStatus {
  AVAILABLE = 'AVAILABLE',
  NOT_AVAILABLE = 'NOT_AVAILABLE',
  REVIEW_PENDING = 'REVIEW_PENDING'
};

export const STATUS_LABEL: Record<DigitalStatus, string> = {
  [DigitalStatus.AVAILABLE]: 'Disponible',
  [DigitalStatus.NOT_AVAILABLE]: 'No disponible',
  [DigitalStatus.REVIEW_PENDING]: 'Pendiente de revisión'
};
