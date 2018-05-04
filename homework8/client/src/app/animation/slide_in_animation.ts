import { trigger, state, animate, transition, style } from '@angular/animations';
 
export const slideInOutAnimation = trigger('slideInOutAnimation', [
	state('show', style({ left: '0' })),
	state('visible', style({ left: '0' })),
    state('hidden', style({ left: '100%' })),
    state('void', style({ left: '-100%' })),
    //transition('visible => hidden', animate('1s ease-in-out')),
    transition('hidden => show', animate('1s ease-in-out')),
    transition('void => visible', animate('1s ease-in-out'))
]);