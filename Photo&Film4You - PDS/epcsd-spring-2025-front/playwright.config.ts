import {defineConfig, devices} from '@playwright/test';

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */
// require('dotenv').config();

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  testDir: './e2e',
  /* Run tests in files in parallel */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env['CI'],
  /* Retry on CI only */
  retries: process.env['CI'] ? 2 : 0,
  /* Opt out of parallel tests on CI. */
  workers: 1,
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Base URL to use in actions like `await page.goto('/')`. */
    baseURL: process.env['E2E_BASE_URL'] ?? 'http://localhost:4200',
    headless: true,

    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    testIdAttribute: 'data-testid',
  },

  // Only start the server if not running in a CI environment.
  webServer: process.env['CI'] ? undefined : {
    command: 'npm start',
    url: 'http://localhost:4200',
    reuseExistingServer: true,
  },

  reporter: [
    ['list'],
    ['monocart-reporter', {
      name: "PhotoAndFilm4You E2E Tests",
      outputFile: './reports-e2e/index.html',
      // global coverage report options
      coverage: {
        outputDir: './coverage-e2e',
        lcov: true,
        reports: ['lcov', 'html', 'cobertura', 'text'],
        entryFilter: () => true,
        sourceFilter: (sourcePath: string) => sourcePath.search(/src\/.+/) !== -1,
      }
    }],
    ['junit', {
      outputFile: 'reports-e2e/junit-test-results.xml',
      embedAnnotationsAsProperties: true
    }]
  ],

  /* Configure projects for major browsers */
  projects: [
    {
      name: 'chromium',
      use: {...devices['Desktop Chrome']},
    },

    // {
    //   name: 'firefox',
    //   use: {...devices['Desktop Firefox']},
    // },
    //
    // {
    //   name: 'webkit',
    //   use: {...devices['Desktop Safari']},
    // },
  ],
});
