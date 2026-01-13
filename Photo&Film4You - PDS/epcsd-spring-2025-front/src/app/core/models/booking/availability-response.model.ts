export interface AvailabilityResponse {
  startDate: Date;
  endDate: Date;
  availableUnitsByProduct: Record<number, number>
}
