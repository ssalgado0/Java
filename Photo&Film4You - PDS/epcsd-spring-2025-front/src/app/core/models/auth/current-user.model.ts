import {UserRole} from '@app/core/enums/user-role.enum';

export interface CurrentUser {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
}
