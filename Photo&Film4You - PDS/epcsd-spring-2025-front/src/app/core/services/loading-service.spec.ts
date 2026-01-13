import {TestBed} from '@angular/core/testing';

import {LoadingService} from './loading-service';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have initial isLoading false', () => {
    expect(service.isLoading()).toBeFalse();
  });

  it('show() should set isLoading to true', () => {
    service.show();
    expect(service.isLoading()).toBeTrue();
  });

  it('hide() should set isLoading to false', () => {
    service.show();
    expect(service.isLoading()).toBeTrue();
    service.hide();
    expect(service.isLoading()).toBeFalse();
  });
});
