class Card:
    def __init__(self, cardId, stars, name):
        self.cardId = cardId
        self.stars = stars
        self.name = name

    def __str__(self):
        return "Card: " + str(self.cardId) + " " + str(self.stars) + " " + str(self.name)

    @classmethod
    def list_from_json(cls, dict):
        return [cls(**item) for item in dict]