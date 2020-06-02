TARGET_COLORS = {"Red": (255, 0, 0), "Yellow": (255, 255, 0), "Green": (0, 255, 0)}

def color_difference (color1, color2):
    return sum([abs(component1-component2) for component1, component2 in zip(color1, color2)])

my_color = (33, 93, 153)

differences = [[color_difference(my_color, target_value), target_name] for target_name, target_value in TARGET_COLORS.items()]
differences.sort()  # sorted by the first element of inner lists
my_color_name = differences[0][1]

print(my_color_name)