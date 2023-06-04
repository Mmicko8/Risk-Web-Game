import json


class AttackResponse:
    def __init__(self, amountOfSurvivingTroopsAttacker, amountOfSurvivingTroopsDefender, gameId, attackerDices, defenderDices):
        self.amountOfSurvivingTroopsAttacker = amountOfSurvivingTroopsAttacker
        self.amountOfSurvivingTroopsDefender = amountOfSurvivingTroopsDefender
        self.gameId = gameId
        self.attackerDices = attackerDices
        self.defenderDices = defenderDices

    def __str__(self):
        return "AttackResponse: " + str(self.amountOfSurvivingTroopsAttacker) + " " + str(self.amountOfSurvivingTroopsDefender)

    @classmethod
    def from_json(cls, json_string):
        json_dict = json.loads(json_string)
        return cls(**json_dict)