import copy
from riskAI.domain.Moves.Attack import Attack
from riskAI.domain.Moves.Fortification import Fortification
from riskAI.domain.Moves.Reinforcement import Reinforcement


# This class is responsible for the logic of a game
class Board(object):
    def __init__(self):
        pass

    def current_player(self, state):
        return state.playersInGame[state.currentPlayer].playerInGameId

    def next_reinforced_state(self, state, reinforcement):
        copyState = copy.deepcopy(state)
        # Set the troops in the correct territory
        if reinforcement is None:
            copyState.nextPhase()
            return copyState
        ter = copyState.get_territory_by_name(reinforcement.territory_name)
        ter.troops += reinforcement.troops
        # Next phase
        copyState.nextPhase()
        return copyState

    def next_fortified_state(self, state, fortification):
        copyState = copy.deepcopy(state)

        if fortification is None:
            copyState.nextPhase()
            return copyState

        # Remove troops from moving country
        moving_ter = copyState.get_territory_by_name(fortification.territoryFrom)
        moving_ter.troops -= fortification.troops
        # Add troops to target country
        target_ter = copyState.get_territory_by_name(fortification.territoryTo)
        target_ter.troops += fortification.troops
        # Next turn
        copyState.nextTurn()
        return copyState

    def next_attacked_state(self, state, attack):
        # Conquering a territory
        copyState = copy.deepcopy(state)
        if attack is None:
            copyState.nextTurn()
            return copyState
        else:
            attackedTer = copyState.get_territory_by_name(attack.defenderTerritoryName)
            attackedTer.ownerId = self.current_player(copyState)
            attackedTer.troops = attack.amountOfAttackers - attack.amountOfDefenders
            attackingTer = copyState.get_territory_by_name(attack.attackerTerritoryName)
            attackingTer.troops = attackingTer.troops - attack.amountOfAttackers
            copyState.nextTurn()
        return copyState

    def legal_attacks(self, state_history):
        ters = state_history[-1].get_users_territories()
        possible_actions = []
        for ter in ters:
            for neighbour in ter.neighbors:
                # Check if it's another country
                ter2 = state_history[-1].get_territory_by_name(neighbour.name)
                if ter2.ownerId != self.current_player(state_history[-1]):
                    # Check if neighbour has less troops
                    # TODO Here is room to make a more offensive or defensive AI
                    if ter2.troops + 1 < ter.troops:
                        attack = Attack(state_history[-1].gameId, ter.name, ter2.name, ter.troops - 1, ter2.troops)
                        possible_actions.append(attack)
        # Add empty attack
        possible_actions.append(None)
        return possible_actions

    def legal_reinforcements(self, state_history):
        reinforcements = []

        # Get all countries owned by the current player
        ters = state_history[-1].get_users_territories()

        # Get the remainingTroopsToReinforce from playerInGame
        remainingTroopsToReinforce = state_history[-1].playersInGame[state_history[-1].currentPlayer].remainingTroopsToReinforce

        # Loop through all ters
        for ter in ters:
            # Create reinforcement for every country with the remainingTroopsToReinforce
            reinforcement = Reinforcement(ter.name, remainingTroopsToReinforce)
            reinforcements.append(reinforcement)

        reinforcements.append(None)
        return reinforcements

    def legal_fortifications(self, state_history):
        copy_history = copy.deepcopy(state_history)
        # Get all countries owned by the current player and return the ones with more than 1 troop
        fortifications = []
        multiple_troops_territories = []
        for ter in copy_history[-1].get_users_territories():
            if ter.troops > 1:
                multiple_troops_territories.append(ter)

        # Get all the recursive neighbours of the territories with more than 1 troop and that have the same user as the territory
        for ter in multiple_troops_territories:
            neighbours = copy_history[-1].get_recursive_neighbors(ter.name, [])
            for neighbour in neighbours:
                fortifications.append(Fortification(state_history[-1].gameId ,ter.name, neighbour, ter.troops - 1))

        fortifications.append(None)
        return fortifications

    def winner(self, state_history):
        copyHistory = copy.deepcopy(state_history)
        # Check if a player has moves left and otherwise return 0
        if self.legal_attacks(copyHistory) is None:
            return 0

        for i in range(len(copyHistory[-1].playersInGame)):
            copyHistory[-1].nextTurn()
            if self.legal_attacks(copyHistory) is None:
                return 0

        # Create map with playerid as key and amount of territories as value
        # Also give a huge bonus if the player owns an entire continent
        player_score = {}
        for continent in copyHistory[-1].continentList:
            owner_id_of_first_country = continent.territories[0].ownerId
            countries_owned_by_one_player = 0
            for country in continent.territories:
                if country.ownerId in player_score:
                    player_score[country.ownerId] += 1
                else:
                    player_score[country.ownerId] = 1

                if country.ownerId == owner_id_of_first_country:
                    countries_owned_by_one_player += 1
            if countries_owned_by_one_player == len(continent.territories):
                player_score[owner_id_of_first_country] += 5 * continent.bonusTroops

        # Return playerid of player with the highest score
        return max(player_score, key=player_score.get)
