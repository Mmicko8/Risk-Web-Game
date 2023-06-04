import json

from riskAI.domain.Player import Player


class PlayerInGame:
    def __init__(self, playerInGameId, remainingTroopsToReinforce, player, color, playerCards, winner):
        self.playerInGameId = playerInGameId
        self.remainingTroopsToReinforce = remainingTroopsToReinforce
        self.player = Player.from_json(player)
        self.color = color
        self.playerCards = playerCards
        self.winner = winner


    @classmethod
    def list_from_json(cls, dict):
        return [cls(**item) for item in dict]

