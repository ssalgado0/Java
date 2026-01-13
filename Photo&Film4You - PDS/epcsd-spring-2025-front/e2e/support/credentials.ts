import {UserRole} from '@app/core/enums/user-role.enum';
import {LoginRequest} from '@app/core/models/auth/login-request.model'

export const credentials: Record<UserRole, LoginRequest> = {
  [UserRole.ADMIN]: {
    email: 'neil@gmail.com',
    password: 'admin',
  },
  [UserRole.USER]: {
    email: 'juanpa@gmail.com',
    password: '12345',
  },
  [UserRole.EXTERNAL]: {
    email: 'externo@externo.com',
    password: '12345',
  },
};
