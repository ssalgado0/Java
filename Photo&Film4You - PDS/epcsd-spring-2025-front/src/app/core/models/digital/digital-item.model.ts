import {DigitalStatus} from '@app/core/enums/digital-status.enum';

export interface DigitalItem {
  id?: number;
  digitalSessionId: number;
  description: string;
  lat: number;
  lon: number;
  link: string;
  status: DigitalStatus;
}
