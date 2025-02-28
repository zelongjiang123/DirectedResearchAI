import numpy as np


def trinary_to_decimal(trinary: np.ndarray) -> int:
    """ Convert trinary (base-3) to decimal (base-10) """
    decimal = 0
    for digit in trinary:
        decimal = decimal * 3 + digit
    return decimal


def decimal_to_trinary(decimal: int, num_digits: int) -> np.ndarray:
    """ Convert decimal (base-10) to trinary (base-3) and return as a list """
    trinary = [0] * num_digits
    index = num_digits - 1
    while decimal > 0 and index >= 0:
        trinary[index] = decimal % 3
        decimal //= 3
        index -= 1
    return trinary
