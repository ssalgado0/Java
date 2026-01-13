import {test as base} from '@e2e/coverage.config';
import {UserRole} from '@app/core/enums/user-role.enum';
import {credentials} from '@e2e/support/credentials';
import {request} from '@playwright/test';

const SESSION_KEY = 'jwt_token';

function createAuthTest(role: UserRole) {
  return base.extend<{ token: string }>({
    // Login once per worker, its value is reused in each test
    token: [
      async ({}, use) => {
        const apiURL = process.env['E2E_API_URL'] ?? 'http://localhost:8080';

        const ctx = await request.newContext({baseURL: apiURL});
        const res = await ctx.post('/auth/login', {data: credentials[role]});

        if (!res.ok()) {
          const body = await res.text().catch(() => '');
          await ctx.dispose();
          throw new Error(`Login API failed (${res.status()}): ${body}`);
        }

        const {token} = await res.json();
        await ctx.dispose();

        if (!token) throw new Error('Login API returned no token');

        await use(token);
      },
      {scope: 'worker'},
    ],

    // Inject the token into the sessionStorage before each test
    page: async ({page, context, token}, use) => {
      await context.addInitScript(
        ([k, t]) => sessionStorage.setItem(k, t),
        [SESSION_KEY, token]
      );

      await use(page);
    },
  });
}

export const testAnonymous = base;
export const testUser = createAuthTest(UserRole.USER);
export const testAdmin = createAuthTest(UserRole.ADMIN);
export {expect} from '@e2e/coverage.config';
