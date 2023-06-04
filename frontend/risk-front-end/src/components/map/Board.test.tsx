import {render} from "@testing-library/react";
import React from "react";
import Board from "./Board";

test('Smoke-test Board', () => {
    render(
        <Board selectTerritory={jest.fn} attackableTerritoryNames={[]} fortifiableTerritoryNames={[]} territories={[]}/>
    )
})