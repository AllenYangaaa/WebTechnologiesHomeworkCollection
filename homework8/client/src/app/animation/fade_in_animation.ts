import { trigger, state, animate, transition, style } from '@angular/animations';
 
export const fadeInAnimation =trigger('fadeInAnimation', [
    state('true' , style({ opacity: 1 })), 
    state('false', style({ opacity: 0 })),
    transition('false => true', animate('0.3s ease-in')),
    //transition('1 => 0', animate('0.5s')) 
]);