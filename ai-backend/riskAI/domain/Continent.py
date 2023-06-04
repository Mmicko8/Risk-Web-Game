import json

from riskAI.domain.Territory import Territory


class Continent:
    def __init__(self, continentId, territories, bonusTroops, name):
        self.continentId = continentId
        self.territories = Territory.list_from_json(territories)
        self.bonusTroops = bonusTroops
        self.name = name

    def __str__(self):
        my_cont = ""
        my_cont += "    Name: " + str(self.name)
        my_cont += "<br>    Territory list: <br>            "
        for territory in self.territoryList:
            my_cont += str(territory) + "<br>           "
        return my_cont

    @classmethod
    def list_from_json(cls, dict_list):
        return [cls(**dict) for dict in dict_list]
