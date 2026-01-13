export interface BookingRequest {
    startDate: string; 
    endDate: string;  
    lines: BookingLineRequest[];
}

export interface BookingLineRequest {
    productId: number;
    quantity: number;
}

