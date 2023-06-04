import json
import os

import requests

from riskAI.AI.MonteCarloTreeSearch import MonteCarloTreeSearch
from riskAI.Service.FortifyService import Fortify
from riskAI.domain.Game import Game
from riskAI.domain.Moves.AttackResponse import AttackResponse
from riskAI.domain.Moves.Fortification import Fortification

backendUrl = os.environ.get("backend_url")
ai_api_path = os.environ.get("ai_api_path")

def Attack(auth, gameId):
    # Infinite loop
    while True:
        print("Roep game op voor volgende aanval")
        game = Game.from_json(json.dumps(get_game_state(gameId, auth)))
        MCTS = MonteCarloTreeSearch(game)
        move = MCTS.get_attack()
        if move is None:
            print("Aanvallen klaar")
            return
        # Convert move to attack
        print(move)
        # Convert move to json
        response = attack(move.__dict__, auth)
        attackResponse = AttackResponse.from_json(json.dumps(response))
        print(attackResponse)
        print("Aanval analyseren met antwoord")
        if attackResponse.amountOfSurvivingTroopsDefender == 0:
            print("Aanval won, dus troepen naar daar verplaatsen...")
            #Create fortification to fortify the conquered territory
            fort = Fortification(gameId, move.attackerTerritoryName, move.defenderTerritoryName, attackResponse.amountOfSurvivingTroopsAttacker)
            print(fort)
            print(attackResponse)
            fortify(fort.__dict__, auth)



def get_game_state(gameid, auth):
    response = requests.get(f"{backendUrl+ai_api_path}/game/" + str(gameid),
                            headers={"Authorization": "Bearer " + auth})
    return response.json()


def attack(dto, auth):
    # Api call to backend
    response = requests.put(f"{backendUrl+ai_api_path}/game/attack",
                            headers={"Authorization": "Bearer " + auth}, json=dto)
    return response.json()

def fortify(dto, auth):
    # Api call to backend
    response = requests.put(f"{backendUrl+ai_api_path}/game/fortify",
                            headers={"Authorization": "Bearer " + auth}, json=dto)
