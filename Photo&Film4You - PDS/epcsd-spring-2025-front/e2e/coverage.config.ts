import {test as base} from '@playwright/test';
import {addCoverageReport} from 'monocart-reporter';

type CoverageConfig = {
  autoCoverage: void;
};

export const test = base.extend<CoverageConfig>({
  autoCoverage: [async ({page}, use) => {
    const isChromium = test.info().project.name.toLowerCase().includes('chromium');

    if (isChromium) {
      await Promise.all([
        page.coverage.startJSCoverage({resetOnNavigation: false}),
        page.coverage.startCSSCoverage({resetOnNavigation: false}),
      ]);
    }

    await use();

    if (isChromium) {
      const [js, css] = await Promise.all([
        page.coverage.stopJSCoverage(),
        page.coverage.stopCSSCoverage(),
      ]);
      await addCoverageReport([...js, ...css], test.info());
    }
  }, {auto: true, scope: 'test'}],
});

export {expect} from '@playwright/test';
