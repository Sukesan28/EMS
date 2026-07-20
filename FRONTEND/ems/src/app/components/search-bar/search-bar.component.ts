import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {
  @Input() placeholder = 'Search...';
  @Input() query = '';
  
  @Output() search = new EventEmitter<string>();
  @Output() clear = new EventEmitter<void>();

  onSearch() {
    this.search.emit(this.query);
  }

  onClear() {
    this.query = '';
    this.clear.emit();
    this.search.emit('');
  }
}
