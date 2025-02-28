import numpy as np
import matplotlib.pyplot as plt
from typing import Tuple
from multiprocessing import Process, Queue
from numba import njit
import time


class NashGameSolver:
    def __init__(self, small_num):
        self.SMALL_NUM = small_num

    def calculate_best_strategy(self, matrix: np.ndarray, opponent_strategy: np.ndarray, count: int) -> int:
        opponent_strategy_ratio = opponent_strategy * (1 / count)
        payoffs = np.dot(matrix, opponent_strategy_ratio.reshape(-1, 1))
        return np.argmax(payoffs)

    def print_ratio(self, ratio: np.ndarray):
        print(" ".join(map(str, ratio)))

    def visualize_best_response(self, numbers: np.ndarray, title: str):
        plt.plot(numbers)
        plt.title(title)
        plt.xlabel("Iterations")
        plt.ylabel("Best Response")
        plt.show()

    def calculate_nash(self, matrix_1: np.ndarray, matrix_2: np.ndarray) -> Tuple[float, float, np.ndarray, np.ndarray] :
        strategy1 = np.zeros(len(matrix_1), dtype=int)
        strategy2 = np.zeros(len(matrix_2), dtype=int)
        count = 1

        for i in range(len(matrix_1)):
            for j in range(len(matrix_1[0])):
                if matrix_1[i][j] != self.SMALL_NUM and matrix_2[j][i] != self.SMALL_NUM:
                    strategy1[i] = 1
                    strategy2[j] = 1
                    break
            else:
                continue
            break


        ITERATIONS = 3000
        for _ in range(ITERATIONS):
            best_response_1 = self.calculate_best_strategy(matrix_1, strategy2, count)
            best_response_2 = self.calculate_best_strategy(matrix_2, strategy1, count)


            # print(best_response_1)
            # print(best_response_2)

            strategy1[best_response_1] += 1
            strategy2[best_response_2] += 1
            count += 1

        ratio_1 = strategy1 / count
        ratio_2 = strategy2 / count

        # self.print_ratio(ratio_1)
        # self.print_ratio(ratio_2)

        payoff_1: float = 0.0
        payoff_2: float = 0.0
        for i in range(len(strategy1)):
            for j in range(len(strategy2)):
                if matrix_1[i][j] != self.SMALL_NUM:
                    payoff_1 += ratio_1[i] * ratio_2[j] * matrix_1[i][j]
                if matrix_2[j][i] != self.SMALL_NUM:
                    payoff_2 += ratio_1[i] * ratio_2[j] * matrix_2[j][i]

        return payoff_1, payoff_2, ratio_1, ratio_2

    """
        Test the calculate_nash function with predefined game matrices.
    """
    def test_calculate_nash(self):

        """
        (2, -2), (-1, 1), (-1, 1),
        (2, -2), (1, -1), (0, 0),
        (0, 0), (0, 0), (1, -1),
        """
        # Define game matrices
        matrix_1 = [
            [2, -1, -1],
            [2, 1, 0],
            [0, 0, 1]
        ]

        matrix_2 = [
            [-2, 1, 1],
            [-2, -1, 0],
            [0, 0, -1]
        ]

        start_time = time.time()
        self.calculate_nash(matrix_1, np.transpose(matrix_2))
        end_time = time.time()
        print("execution time is", (end_time - start_time) * 1000, "ms")

    def test_func_time(self):
        matrix_1 = [
            [2, -1, -1],
            [2, 1, 0],
            [0, 0, 1]
        ]
        strategy_2 = [5, 7, 3]
        matrix_1 = np.array(matrix_1)
        strategy_2 = np.array(strategy_2)
        start_time = time.time()
        for _ in range(3000):
            self.calculate_best_strategy(matrix_1, strategy_2, 15)
        end_time = time.time()
        print("execution time is", (end_time - start_time) * 1000, "ms")

if __name__ == "__main__":
    solver = NashGameSolver(-1000.00)
    solver.test_calculate_nash()
    # solver.test_func_time()
