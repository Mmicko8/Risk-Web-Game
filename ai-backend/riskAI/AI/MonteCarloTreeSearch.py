import random
import datetime
from math import sqrt, log

from urllib3.connectionpool import xrange

from riskAI.domain.Board import Board


class MonteCarloTreeSearch:
    def __init__(self, game, **kwargs):
        self.board = Board()
        self.states = [game]
        seconds = kwargs.get('seconds', 3)
        self.calculation_time = datetime.timedelta(seconds=seconds)
        self.max_moves = kwargs.get('max_moves', 30)
        self.wins = {}
        self.plays = {}
        self.C = kwargs.get('C', 1.4)

    def update(self, state):
        self.states.append(state)

    def get_reinforcement(self):
        self.max_depth = 0
        state = self.states[-1]
        player = self.board.current_player(state)
        legal = self.board.legal_reinforcements(self.states[:])

        # Check if there are 0 to 1 legal moves and return the only one
        if not legal:
            return None
        if len(legal) == 1:
            return legal[0]

        games = 0
        begin = datetime.datetime.utcnow()
        while datetime.datetime.utcnow() - begin < self.calculation_time:
            self.run_reinforcement_simulation()
            games += 1

        moves_states = [(p, self.board.next_reinforced_state(state, p)) for p in legal]

        # Pick the attack with the highest percentage of wins
        final_reinforcement = max(self.plays, key=lambda x: self.wins[x] / self.plays[x])

        # Show the percentage of wins for each attack
        # for rein, state in moves_states:
        #     print(str(rein), " Wins: ", self.wins.get((player, rein), 0), " Plays: ",
        #           self.plays.get((player, rein), 1))

        return final_reinforcement[1]

    def get_attack(self):
        self.max_depth = 0
        state = self.states[-1]
        player = self.board.current_player(state)
        legal = self.board.legal_attacks(self.states[:])

        # Check if there are 0 to 1 legal moves and return the only one
        if not legal:
            return None
        if len(legal) == 1:
            return legal[0]

        games = 0
        begin = datetime.datetime.utcnow()
        while datetime.datetime.utcnow() - begin < self.calculation_time:
            self.run_attack_simulation()
            games += 1

        moves_states = [(p, self.board.next_attacked_state(state, p)) for p in legal]

        # Pick the attack with the highest percentage of wins
        final_attack = max(self.plays, key=lambda x: self.wins[x] / self.plays[x])

        # Show the percentage of wins for each attack
        # for attack, state in moves_states:
        #     print("Attack: ", str(attack), " Wins: ", self.wins.get((player, attack), 0), " Plays: ",
        #           self.plays.get((player, attack), 1))

        return final_attack[1]

    def get_fortification(self):
        self.max_depth = 0
        state = self.states[-1]
        player = self.board.current_player(state)
        legal = self.board.legal_fortifications(self.states[:])

        # Check if there are 0 to 1 legal moves and return the only one
        if not legal:
            return None
        if len(legal) == 1:
            return legal[0]

        games = 0
        begin = datetime.datetime.utcnow()
        while datetime.datetime.utcnow() - begin < self.calculation_time:
            self.run_fortification_simulation()
            games += 1

        moves_states = [(p, self.board.next_fortified_state(state, p)) for p in legal]

        # Pick the attack with the highest percentage of wins
        final_fort = max(self.plays, key=lambda x: self.wins[x] / self.plays[x])

        # Show the percentage of wins for each attack
        # for fort, state in moves_states:
        #     print(str(fort), " Wins: ", self.wins.get((player, fort), 0), " Plays: ",
        #           self.plays.get((player, fort), 1))

        return final_fort[1]

    def run_attack_simulation(self):
        plays, wins, C = self.plays, self.wins, self.C

        visited_states = set()
        states_copy = self.states[:]
        state = states_copy[-1]
        player = self.board.current_player(state)

        winner = 0
        expand = True
        for t in xrange(1, self.max_moves + 1):
            legal = self.board.legal_attacks(states_copy)
            moves_states = [(attack, self.board.next_attacked_state(state, attack)) for attack in legal]
            if moves_states is None:
                moves_states = [(None, self.board.next_attacked_state(state, None))]

            if all(plays.get((player, attack)) for attack, State in moves_states):
                # If we have stats on all the legal moves here, use them
                log_total = log(
                    sum(plays[(player, p)] for p, S in moves_states))

                answer = max(plays, key=lambda x: (wins[x] / plays[x] + C * sqrt(log_total / plays[x])))
                move = answer[1]
                state = self.board.next_attacked_state(state, move)

            else:
                # Otherwise, just make an arbitrary decision
                move, state = random.choice(moves_states)

            states_copy.append(state)

            # 'player' here and below refers to the player who moved in that state
            if expand and (player, move) not in plays:
                expand = False
                plays[(player, move)] = 0
                wins[(player, move)] = 0
                if t > self.max_depth:
                    self.max_depth = t

            visited_states.add((player, move))

            player = self.board.current_player(state)
            winner = self.board.winner(states_copy)
            if winner:
                break

        for player, attack in visited_states:
            if (player, attack) not in plays:
                continue
            plays[(player, attack)] += 1
            if player == winner:
                wins[(player, attack)] += 1

    def run_fortification_simulation(self):
        plays, wins, C = self.plays, self.wins, self.C

        visited_states = set()
        states_copy = self.states[:]
        state = states_copy[-1]
        player = self.board.current_player(state)

        winner = 0
        expand = True
        for t in xrange(1, self.max_moves + 1):
            legal = self.board.legal_fortifications(states_copy)
            moves_states = [(fort, self.board.next_fortified_state(state, fort)) for fort in legal]
            if moves_states is None:
                moves_states = [(None, self.board.next_fortified_state(state, None))]

            if all(plays.get((player, fort)) for fort, State in moves_states):
                # If we have stats on all the legal moves here, use them
                log_total = log(
                    sum(plays[(player, p)] for p, S in moves_states))

                answer = max(plays, key=lambda x: (wins[x] / plays[x] + C * sqrt(log_total / plays[x])))
                move = answer[1]
                state = self.board.next_fortified_state(state, move)

            else:
                # Otherwise, just make an arbitrary decision
                move, state = random.choice(moves_states)

            states_copy.append(state)

            # 'player' here and below refers to the player who moved in that state
            if expand and (player, move) not in plays:
                expand = False
                plays[(player, move)] = 0
                wins[(player, move)] = 0
                if t > self.max_depth:
                    self.max_depth = t

            visited_states.add((player, move))

            player = self.board.current_player(state)
            winner = self.board.winner(states_copy)
            if winner:
                break

        for player, fort in visited_states:
            if (player, fort) not in plays:
                continue
            plays[(player, fort)] += 1
            if player == winner:
                wins[(player, fort)] += 1

    def run_reinforcement_simulation(self):
        plays, wins, C = self.plays, self.wins, self.C

        visited_states = set()
        states_copy = self.states[:]
        state = states_copy[-1]
        player = self.board.current_player(state)

        winner = 0
        expand = True
        for t in xrange(1, self.max_moves + 1):
            legal = self.board.legal_reinforcements(states_copy)
            moves_states = [(rein, self.board.next_reinforced_state(state, rein)) for rein in legal]
            if moves_states is None:
                moves_states = [(None, self.board.next_reinforced_state(state, None))]

            if all(plays.get((player, rein)) for rein, State in moves_states):
                # If we have stats on all the legal moves here, use them
                log_total = log(
                    sum(plays[(player, p)] for p, S in moves_states))

                answer = max(plays, key=lambda x: (wins[x] / plays[x] + C * sqrt(log_total / plays[x])))
                move = answer[1]
                state = self.board.next_reinforced_state(state, move)

            else:
                # Otherwise, just make an arbitrary decision
                move, state = random.choice(moves_states)

            states_copy.append(state)

            # 'player' here and below refers to the player who moved in that state
            if expand and (player, move) not in plays:
                expand = False
                plays[(player, move)] = 0
                wins[(player, move)] = 0
                if t > self.max_depth:
                    self.max_depth = t

            visited_states.add((player, move))

            player = self.board.current_player(state)
            winner = self.board.winner(states_copy)
            if winner:
                break

        for player, rein in visited_states:
            if (player, rein) not in plays:
                continue
            plays[(player, rein)] += 1
            if player == winner:
                wins[(player, rein)] += 1
