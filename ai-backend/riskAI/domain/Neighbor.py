import json

class Neighbor:
    def __init__(self, name):
        self.name = name

    def __str__(self):
        return self.name

    @classmethod
    def list_from_json(cls, dict_list):
        return [cls(**dict) for dict in dict_list]