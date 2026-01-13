import {UserRole} from '@app/core/enums/user-role.enum';

export interface UserProfile {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  role: UserRole;
}
