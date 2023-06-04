import os

import requests

from riskAI.AI.MonteCarloTreeSearch import MonteCarloTreeSearch
# from riskAI.AI.MonteCarloTreeSearch import AttackMonteCarloTreeSearch
from riskAI.domain.Board import Board

backendUrl = os.environ.get("backend_url")
ai_api_path = os.environ.get("ai_api_path")

def Reinforce(game, auth):
    MCTS = MonteCarloTreeSearch(game)
    move = MCTS.get_reinforcement()
    # Convert move to attack
    print(move)

    if move is None:
        return

    reinforce(game.get_territory_by_name(move.territory_name).territoryId, move.troops, auth)

    return


def reinforce(territoryId, troops, auth):
    # Api call to backend
    response = requests.put(f"{backendUrl+ai_api_path}/territory/" + str(territoryId) + "/placeTroops/" + str(troops),
                            headers={"Authorization": "Bearer " + auth})
