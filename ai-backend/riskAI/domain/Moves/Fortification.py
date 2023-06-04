import json


class Fortification:
    def __init__(self, gameid, territoryFrom, territoryTo, troops):
        self.gameId = gameid
        self.territoryFrom = territoryFrom
        self.territoryTo = territoryTo
        self.troops = troops

    def __str__(self):
        return "Fortification: %s -> %s (%d)" % (self.territoryFrom, self.territoryTo, self.troops)

    def __eq__(self, other):
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash((self.gameId, self.territoryFrom, self.territoryTo, self.troops))

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)