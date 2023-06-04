import {QueryClient, QueryClientProvider} from "react-query";
import {render} from "@testing-library/react";
import Home from "./Home";
import {BrowserRouter} from "react-router-dom";


test('Smoke-test Home', () => {
    // gives warning about wrapping in act() -> however this is a smoke test so we ignore the warning
    jest.spyOn(console, 'error').mockImplementation(() => {});
    const queryClient = new QueryClient();
    render(
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <Home/>
            </BrowserRouter>
        </QueryClientProvider>
    )
})