import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-statistic-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistic-card.component.html',
  styleUrl: './statistic-card.component.css'
})
export class StatisticCardComponent {
  @Input() title = '';
  @Input() value: string | number = 0;
  @Input() themeColor = 'primary'; 
}
