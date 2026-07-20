import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-greeting',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './greeting.component.html',
  styleUrl: './greeting.component.css'
})
export class GreetingComponent implements OnInit {
  @Input() userName = '';
  
  greetingMessage = 'Welcome';
  timeString = '';

  ngOnInit() {
    this.updateGreeting();
   
    const date = new Date();
    this.timeString = date.toLocaleDateString(undefined, { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  private updateGreeting() {
    const hours = new Date().getHours();
    if (hours < 12) {
      this.greetingMessage = 'Good Morning';
    } else if (hours < 17) {
      this.greetingMessage = 'Good Afternoon';
    } else {
      this.greetingMessage = 'Good Evening';
    }
  }
}
