import {DigitalStatus} from '@app/core/enums/digital-status.enum';

export interface DigitalSession {
  id?: number;
  email: string;
  description: string;
  status: DigitalStatus;
}
