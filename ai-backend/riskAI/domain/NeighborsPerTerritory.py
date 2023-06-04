from riskAI.domain.Neighbor import Neighbor


class NeighborsPerTerritory:
    def __init__(self, territoryId, name, neighbors):
        self.territoryId = territoryId
        self.name = name
        self.neighbors = Neighbor.list_from_json(neighbors)

    @classmethod
    def list_from_json(cls, dict_list):
        return [cls(**dict) for dict in dict_list]

