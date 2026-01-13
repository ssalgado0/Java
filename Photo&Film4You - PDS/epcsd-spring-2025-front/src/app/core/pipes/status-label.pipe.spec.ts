import {StatusLabelPipe} from './status-label.pipe';
import {DigitalStatus, STATUS_LABEL} from '@app/core/enums/digital-status.enum';

describe('StatusLabelPipe', () => {
  let pipe: StatusLabelPipe;

  beforeEach(() => {
    pipe = new StatusLabelPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return empty string for null', () => {
    expect(pipe.transform(null)).toBe('');
  });

  it('should return empty string for undefined', () => {
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should return label for AVAILABLE status', () => {
    const status = DigitalStatus.AVAILABLE;
    expect(pipe.transform(status)).toBe(STATUS_LABEL[status]);
  });

  it('should return label for NOT_AVAILABLE status', () => {
    const status = DigitalStatus.NOT_AVAILABLE;
    expect(pipe.transform(status)).toBe(STATUS_LABEL[status]);
  });

  it('should return label for REVIEW_PENDING status', () => {
    const status = DigitalStatus.REVIEW_PENDING;
    expect(pipe.transform(status)).toBe(STATUS_LABEL[status]);
  });

  it('should return value itself if no label found (fallback)', () => {
    const unknownStatus = 'UNKNOWN_STATUS' as DigitalStatus;
    expect(pipe.transform(unknownStatus)).toBe(unknownStatus);
  });
});
