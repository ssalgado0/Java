import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {Products} from '@app/features/products/products';
import {CreateProduct} from '@app/features/create-product/create-product';
import {ProductDetailItems} from '@app/features/product-detail/product-detail-items.component';
import {roleGuard} from '@app/security/guards/role-guard';
import {Cart} from '@app/features/cart/cart';
import {Rent} from '@app/features/rent/rent';
import {Alerts} from '@app/features/alerts/alerts';
import {UserRole} from '@app/core/enums/user-role.enum';
import {BookingSummary} from '@app/features/booking-summary/booking-summary';
import {DigitalSessions} from '@app/features/digital-sessions/digital-sessions';
import {DigitalSessionForm} from '@app/features/digital-session-form/digital-session-form';
import {DigitalSessionDetails} from '@app/features/digital-session-details/digital-session-details';
import {DigitalItemForm} from '@app/features/digital-item-form/digital-item-form';
import {Notifications} from '@app/features/notifications/notifications';
import {Profile} from '@app/features/profile/profile';
import {EditProfile} from '@app/features/profile/edit-profile';
import {ChangePassword} from '@app/features/profile/change-password';
import {DeleteAccount} from '@app/features/profile/delete-account';
import {CreateUser} from '@app/features/create-user/create-user';



export const routes: Routes = [
  { path: '', component: Home },
  {
    path: 'products/create',
    component: CreateProduct,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN]}
  },
  {
    path: 'products/:id/edit',
    component: CreateProduct,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN]}
  },

  { path: 'products',
    component: Products,
    children: [
      {
        path: ':id',
        loadComponent: () => import('@app/features/product-detail/product-detail').then(m => m.ProductDetail),
      }
    ]},
  {
    path: 'products/:id/items',
    component: ProductDetailItems,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN]}
  },
  {
    path: 'cart',
    component: Cart,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'bookings',
    component: Rent,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'alerts',
    component: Alerts,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'notifications',
    component: Notifications,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'bookings/:id',
    component: BookingSummary,
    canActivate: [roleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.USER] }
  },
  {
    path: 'sessions',
    component: DigitalSessions,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'sessions/create',
    component: DigitalSessionForm,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'sessions/:id/edit',
    component: DigitalSessionForm,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'sessions/:id',
    component: DigitalSessionDetails,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'sessions/:sessionId/items/create',
    component: DigitalItemForm,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'sessions/:sessionId/items/:itemId/edit',
    component: DigitalItemForm,
    canActivate: [roleGuard],
    data: {roles: [UserRole.ADMIN, UserRole.USER]}
  },
  {
    path: 'profile',
    component: Profile,
    canActivate: [roleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.USER] }
  },
  {
    path: 'profile/edit',
    component: EditProfile,
    canActivate: [roleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.USER] }
  },
  {
    path: 'profile/change-password',
    component: ChangePassword,
    canActivate: [roleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.USER] }
  },
  {
    path: 'profile/delete',
    component: DeleteAccount,
    canActivate: [roleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.USER] }
  },
  {
    path: 'users/create',
    component: CreateUser,
    canActivate: [roleGuard],
    data: { roles: [UserRole.ADMIN] }
  },

  { path: '**', redirectTo: '' }
];
