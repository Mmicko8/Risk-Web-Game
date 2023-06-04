import json


class Player:
    def __init__(self, id, username, email, gamesWon, gamesLost, gamesWonAgainstAi, gamesPlayed, loyaltyPoints, profilePicture, title, aiDifficulty):
        self.id = id
        self.username = username
        self.email = email

    def __str__(self):
        return self.username

    @classmethod
    def from_json(cls, json_string):
        dump = json.dumps(json_string)
        json_dict = json.loads(dump)
        return cls(**json_dict)
