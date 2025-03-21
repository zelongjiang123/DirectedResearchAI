import numpy as np
import matplotlib.pyplot as plt
import copy
import math
from typing import Tuple
from math_utils import decimal_to_trinary, trinary_to_decimal
from nash_game_solver import NashGameSolver
import multiprocessing

class GameSolver:
    def __init__(self, num_rows: int, num_cols: int, num_actions_player1: int, num_actions_player2: int,
                 reward: np.ndarray, crash: int,
                 small_num: int = -10000, alpha: float = 0.1, discount: float = 0.9, epsilon: float = 0.1,
                 episodes: int = 100):
        self.num_actions_player1 = num_actions_player1
        self.num_actions_player2 = num_actions_player2
        self.alpha = alpha
        self.DISCOUNT = discount
        self.epsilon = epsilon
        self.episodes = episodes
        self.REWARD = reward
        self.crash = crash

        self.small_num = small_num

        self.num_states = num_rows * num_cols * num_rows * num_cols
        self.num_rows = num_rows
        self.num_cols = num_cols

        self.q_table_player1 = np.zeros((self.num_states, num_actions_player1, num_actions_player2))
        self.q_table_player2 = np.zeros((self.num_states, num_actions_player1, num_actions_player2))

    def choose_action(self):
        if np.random.rand() < self.epsilon:
            return np.random.randint(self.num_actions_player1), np.random.randint(self.num_actions_player2)
        else:
            action1 = np.argmax(np.sum(self.q_table_player1, axis=1))
            action2 = np.argmax(np.sum(self.q_table_player2, axis=0))
            return action1, action2

    def calculate_next_positions(self, positions: np.ndarray, actions: np.ndarray):
        next_positions = positions.copy()  # Create a copy of the positions list
        for i in range(len(actions)):
            if actions[i] == 0:  # up
                next_positions[i * 2] -= 1
            elif actions[i] == 1:  # down
                next_positions[i * 2] += 1
            elif actions[i] == 2:  # left
                next_positions[i * 2 + 1] -= 1
            elif actions[i] == 3:  # right
                next_positions[i * 2 + 1] += 1
        return next_positions

    def is_valid_positions(self, positions: np.ndarray) -> bool:

        for pos in positions:
            if pos < 0 or pos >= self.num_rows:
                return False

        return True

    def construct_payoff_matrix(self, positions: np.ndarray) -> Tuple[np.ndarray, np.ndarray]:
        payoff_1 = np.zeros((self.num_actions_player1, self.num_actions_player2))
        payoff_2 = np.zeros((self.num_actions_player1, self.num_actions_player2))

        # Convert positions to a state using trinaryToDecimal (assuming this function is defined)
        state = trinary_to_decimal(positions)

        # if state >= 81:
        #     print(positions)
        # Fill in the payoff matrices based on the state and actions
        for i in range(self.num_actions_player1):
            for j in range(self.num_actions_player2):
                payoff_1[i, j] = self.q_table_player1[state][i][j]
                payoff_2[i, j] = self.q_table_player2[state][i][j]

        return payoff_1, payoff_2

    # def calculate_q_values(self, q_table_temp: np.ndarray, next_positions: np.ndarray, state: int, action1: int,
    #                        action2: int, player: int, reward: int):
    #     if not self.is_valid_positions(next_positions):
    #         q_table_temp[state][action1][action2] = self.small_num
    #     else:
    #         payoff_matrices = self.construct_payoff_matrix(next_positions)
    #         nash_game_solver = NashGameSolver(self.small_num)
    #         payoff1, payoff2, ratio1, ratio2 = nash_game_solver.calculate_nash(payoff_matrices[0],
    #                                                                            np.transpose(payoff_matrices[1]))
    #         q_table_temp[state][action1][action2] = reward + self.DISCOUNT * (payoff1 if player == 1 else payoff2)

    def update_q_values(self, q_table: np.ndarray, player: int) -> np.ndarray:
        q_table_temp = q_table.copy()
        processes = []
        for index in np.ndindex(q_table.shape):
            state, action1, action2 = index
            positions = decimal_to_trinary(state, 4)
            row1 = positions[0]
            col1 = positions[1]
            row2 = positions[2]
            col2 = positions[3]
            reward: int = self.REWARD[row1][col1] - self.REWARD[row2][col2]
            if player == 2:
                reward = self.REWARD[row2][col2] - self.REWARD[row1][col1]
            if row1 == row2 and col1 == col2:
                if player == 1:
                    reward += self.crash
                else:
                    reward -= self.crash

            next_positions = self.calculate_next_positions(np.array([row1, col1, row2, col2]),
                                                           np.array([action1, action2]))
            if not self.is_valid_positions(next_positions):
                q_table_temp[state][action1][action2] = self.small_num
            else:
                payoff_matrices = self.construct_payoff_matrix(next_positions)
                nash_game_solver = NashGameSolver(self.small_num)
                payoff1, payoff2, ratio1, ratio2 = nash_game_solver.calculate_nash(payoff_matrices[0],
                                                                                   np.transpose(payoff_matrices[1]))
                q_table_temp[state][action1][action2] = reward + self.DISCOUNT * (payoff1 if player == 1 else payoff2)
        return q_table_temp

    def calculate_average_q_difference(self, curr_Q, prev_Q) -> float:
        total_diff = 0.0

        for state in range(len(curr_Q)):
            for action1 in range(len(curr_Q[0])):
                for action2 in range(len(curr_Q[0][0])):
                    total_diff += abs(curr_Q[state][action1][action2] - prev_Q[state][action1][action2])

        return total_diff / (self.num_states * self.num_actions_player1 * self.num_actions_player2)

    def train(self):
        losses1 = []
        losses2 = []
        for i in range(self.episodes):
            print("Iteration", i, "start")
            q_table_temp1 = self.update_q_values(self.q_table_player1, 1)
            q_table_temp2 = self.update_q_values(self.q_table_player2, 2)

            loss1 = self.calculate_average_q_difference(q_table_temp1, self.q_table_player1)
            loss2 = self.calculate_average_q_difference(q_table_temp2, self.q_table_player2)
            losses1.append(loss1)
            losses2.append(loss2)

            self.q_table_player1 = q_table_temp1
            self.q_table_player2 = q_table_temp2

            print("Iteration", i, "is complete, loss is", loss1, loss2)

    def visualize_q_values(self):
        plt.figure(figsize=(10, 5))
        plt.subplot(1, 2, 1)
        plt.imshow(self.q_table_player1, cmap='coolwarm', interpolation='nearest')
        plt.colorbar()
        plt.title("Q-Table Player 1")

        plt.subplot(1, 2, 2)
        plt.imshow(self.q_table_player2, cmap='coolwarm', interpolation='nearest')
        plt.colorbar()
        plt.title("Q-Table Player 2")

        plt.show()


# Example usage
if __name__ == "__main__":
    num_actions_p1 = 3
    num_actions_p2 = 3

    reward = np.array([[1, 2, 3], [1, 1, 2], [3, 1, 2]])
    solver = GameSolver(num_rows=3, num_cols=3, num_actions_player1=4, num_actions_player2=4, reward=reward, crash=10)
    solver.train()
