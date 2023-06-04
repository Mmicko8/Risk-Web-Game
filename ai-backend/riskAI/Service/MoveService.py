import json
import os

import requests

from riskAI.Service.AttackService import Attack
from riskAI.Service.FortifyService import Fortify
# from riskAI.Service.FortifyService import Fortify
from riskAI.Service.LoginService import login
from riskAI.Service.ReinforceService import Reinforce
from riskAI.domain.Game import Game

backendUrl = os.environ.get("backend_url")
ai_api_path = os.environ.get("ai_api_path")

class MoveService:
    def __init__(self):
        self.authcode = login()
        # self.neighbours = get_neighbors(self.authcode)
        return

    def Move(self, jsonbody):
        game = Game.from_json(json.dumps(jsonbody))
        print("Beginnen met beurt voor speler " + game.playersInGame[game.currentPlayer].player.username)
        Reinforce(game, self.authcode)
        next_phase(game.gameId, self.authcode)
        Attack(self.authcode, game.gameId)
        next_phase(game.gameId, self.authcode)
        Fortify(self.authcode, game.gameId)
        next_turn(game.gameId, self.authcode)
        return


def next_phase(gameid, auth):
    # Api call to backend
    response = requests.put(f"{backendUrl+ai_api_path}/game/" + str(gameid) + "/nextPhase",
                            headers={"Authorization": "Bearer " + auth})


def next_turn(gameid, auth):
    # Api call to backend
    response = requests.put(f"{backendUrl+ai_api_path}/game/" + str(gameid) + "/nextTurn",
                            headers={"Authorization": "Bearer " + auth})


def get_game_state(gameid, auth):
    print("Getting game state")
    response = requests.get(f"{backendUrl+ai_api_path}/game/" + str(gameid),
                            headers={"Authorization": "Bearer " + auth})
    return response.json()


def get_neighbors(auth):
    response = requests.get(f"{backendUrl+ai_api_path}/territory/game/1/neighbors",
                            headers={"Authorization": "Bearer " + auth})
    return print(response.json())


def finish(game):
    # TODO hier komt api call naar server voor te finishen
    print("Beurt " + str(game.turn) + " gedaan voor " + game.playersInGame[game.currentPlayer].player.username)
