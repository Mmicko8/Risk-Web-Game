import json

from riskAI.Service import NeighbourService
from riskAI.domain.Neighbor import Neighbor


class Territory:
    def __init__(self, territoryId, name, ownerId, troops):
        self.territoryId = territoryId
        self.neighbors = Neighbor.list_from_json(NeighbourService.get_neighbour_list(name))
        self.name = name
        self.ownerId = ownerId
        self.troops = troops

    def __str__(self):
        my_terr = "Territory: " + self.name + "<br> Neighbors: ("
        for neighbor in self.neighbors:
            my_terr += neighbor.name + " "
        my_terr += ")"
        return my_terr

    @classmethod
    def list_from_json(cls, dict_list):
        return [cls(**dict) for dict in dict_list]
