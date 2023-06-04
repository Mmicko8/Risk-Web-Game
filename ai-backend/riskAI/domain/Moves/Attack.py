import json


class Attack:
    def __init__(self, gameId, attackerTerritoryName, defenderTerritoryName, amountOfAttackers, amountOfDefenders):
        self.gameId = gameId
        self.attackerTerritoryName = attackerTerritoryName
        self.defenderTerritoryName = defenderTerritoryName
        self.amountOfAttackers = amountOfAttackers
        self.amountOfDefenders = amountOfDefenders

    def __str__(self):
        return " Attack: " + self.attackerTerritoryName + " -> " + self.defenderTerritoryName + " (" + str(
            self.amountOfAttackers) + " -> " + str(self.amountOfDefenders) + ")"

    def __eq__(self, other):
        return self.attackerTerritoryName == other.attackerTerritoryName and self.defenderTerritoryName == other.defenderTerritoryName and self.amountOfAttackers == other.amountOfAttackers and self.amountOfDefenders == other.amountOfDefenders

    def __hash__(self):
        return hash((self.attackerTerritoryName, self.defenderTerritoryName, self.amountOfAttackers,
                     self.amountOfDefenders))

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
