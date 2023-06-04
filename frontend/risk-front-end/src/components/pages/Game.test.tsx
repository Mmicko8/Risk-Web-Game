import {render} from "@testing-library/react";
import Game from "./Game";
import {QueryClient, QueryClientProvider} from "react-query";


test('Smoke-test Game', () => {
    const queryClient = new QueryClient();
    render(
        <QueryClientProvider client={queryClient}>
            <Game/>
        </QueryClientProvider>
    )
})