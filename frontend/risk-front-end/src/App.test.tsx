import React from 'react';
import {render} from '@testing-library/react';
import App from './App';

test('App Smoke-test', () => {
    // gives error warning about wrapping in act() -> however this is a smoke test so we ignore the warning
    jest.spyOn(console, 'error').mockImplementation(() => {});
    render(<App/>);
});
