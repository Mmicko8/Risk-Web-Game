import json
import os

import requests

from riskAI.AI.MonteCarloTreeSearch import MonteCarloTreeSearch
from riskAI.domain.Game import Game

backendUrl = os.environ.get("backend_url")
ai_api_path = os.environ.get("ai_api_path")

def Fortify(auth, gameId):
    while True:
        game = Game.from_json(json.dumps(get_game_state(gameId, auth)))
        MCTS = MonteCarloTreeSearch(game)
        move = MCTS.get_fortification()
        if move is None:
            print("Forticications klaar")
            return
        # Convert move to attack
        fortify(move.__dict__, auth)


def get_game_state(gameid, auth):
    response = requests.get(f"{backendUrl+ai_api_path}/game/" + str(gameid),
                            headers={"Authorization": "Bearer " + auth})
    return response.json()


def fortify(dto, auth):
    # Api call to backend
    response = requests.put(f"{backendUrl+ai_api_path}/game/fortify",
                            headers={"Authorization": "Bearer " + auth}, json=dto)
