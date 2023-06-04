import json


class Reinforcement:
    def __init__(self, territory_name, troops):
        self.troops = troops
        self.territory_name = territory_name

    def __str__(self):
        return "Reinforcement: " + str(self.troops) + " troops to " + self.territory_name

    def __eq__(self, other):
        return self.troops == other.troops and self.territory_name == other.territory_name

    def __hash__(self):
        return hash((self.troops, self.territory_name))

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)