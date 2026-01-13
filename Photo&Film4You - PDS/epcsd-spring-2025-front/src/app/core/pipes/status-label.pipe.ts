import {Pipe, PipeTransform} from '@angular/core';
import {DigitalStatus, STATUS_LABEL} from '@app/core/enums/digital-status.enum';

@Pipe({
  name: 'statusLabel',
  standalone: true
})
export class StatusLabelPipe implements PipeTransform {

  transform(value: DigitalStatus | null | undefined): string {
    if (!value) return '';
    return STATUS_LABEL[value] ?? value;
  }

}
