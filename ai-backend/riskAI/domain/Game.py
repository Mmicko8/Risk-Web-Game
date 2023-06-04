import json

from riskAI.domain.Card import Card
from riskAI.domain.Continent import Continent
from riskAI.domain.Moves.Attack import Attack
from riskAI.domain.PlayerInGame import PlayerInGame


#  This class is meant to be a representation of the game state
class Game:
    def __init__(self, gameId, continents, gameCards, playersInGame, startTime, endTime, timer, turn, currentPlayerIndex,
                 phase, afkThreshold):
        self.gameId = gameId
        self.continentList = Continent.list_from_json(continents)
        # self.cardList = Card.list_from_json(cards)
        self.playersInGame = PlayerInGame.list_from_json(playersInGame)
        self.timer = timer
        self.turn = turn
        self.currentPlayer = currentPlayerIndex
        self.phase = phase

    def __str__(self):
        # Return a string with current playername and turn
        return "Current player: " + self.playersInGame[self.currentPlayer].player.username + " Turn: " + str(self.turn)

    @classmethod
    def from_json(cls, json_string):
        json_dict = json.loads(json_string)
        return cls(**json_dict)

    def get_users_territories(self):
        countries = []
        for continent in self.continentList:
            for country in continent.territories:
                if country.ownerId == self.playersInGame[self.currentPlayer].playerInGameId:
                    countries.append(country)
        return countries

    def get_territory_by_name(self, name):
        for continent in self.continentList:
            for country in continent.territories:
                if country.name == name:
                    return country
        return None

    def nextTurn(self):
        self.currentPlayer = self.currentPlayer + 1
        if self.currentPlayer == len(self.playersInGame):
            self.currentPlayer = 0
        self.turn = self.turn + 1

    def nextPhase(self):
        if self.phase == "attack":
            self.phase = "fortify"
        elif self.phase == "fortify":
            self.phase = "reinforce"
        elif self.phase == "reinforce":
            self.phase = "attack"

    def get_recursive_neighbors(self, territory_name, visited):
        territory = self.get_territory_by_name(territory_name)
        for neighbor in territory.neighbors:
            territory_neighbor = self.get_territory_by_name(neighbor.name)
            if neighbor.name not in visited and territory_neighbor.ownerId == territory.ownerId:
                visited.append(neighbor.name)
                self.get_recursive_neighbors(neighbor.name, visited)
        return visited

    def get_territory_by_name(self, name):
        for continent in self.continentList:
            for country in continent.territories:
                if country.name == name:
                    return country
        return None