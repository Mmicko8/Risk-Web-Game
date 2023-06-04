import {QueryClient, QueryClientProvider} from "react-query";
import {render} from "@testing-library/react";
import Profile from "./Profile";

test('Smoke-test Profile', () => {
    const queryClient = new QueryClient();
    render(
        <QueryClientProvider client={queryClient}>
            <Profile/>
        </QueryClientProvider>
    )
})