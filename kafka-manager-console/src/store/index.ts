import { configure } from 'mobx';
configure({ enforceActions: 'observed' });

export { wrapper } from './wrapper';
export { region } from './region';
