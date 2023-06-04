
export interface AttackResult {
    amountOfSurvivingTroopsAttacker: number;
    amountOfSurvivingTroopsDefender: number;
    gameId: number;
    attackerDices: number[];
    defenderDices: number[];
}