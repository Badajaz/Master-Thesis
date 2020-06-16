from ozopython.colorLanguageTranslator import ColorLanguageTranslator
from .ozopython import *



def run(filename):
    code = ozopython.compile(filename)

    colorcode = ColorLanguageTranslator.translate(code)
    return colorcode
    #
    # def load(prog, prog_bar):
    #     colormap = {
    #         'K': "#000000",
    #         'R': "#ff0000",
    #         'G': "#00ff00",
    #         'Y': "#ffff00",
    #         'B': "#0000ff",
    #         'M': "#ff00ff",
    #         'C': "#00ffff",
    #         'W': "#ffffff"
    #     }
    #
    #     head = prog[0]
    #     tail = prog[1:len(prog)]
    #     value = len(colorcode) - len(prog)
    #     return prog





