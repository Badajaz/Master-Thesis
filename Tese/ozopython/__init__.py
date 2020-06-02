from tkinter import ttk

from ozopython.colorLanguageTranslator import ColorLanguageTranslator
from .ozopython import *
from tkinter import *


def toplevel():
    top = Toplevel()
    top.title('Optimized Map')
    top.wm_geometry("794x370")
    optimized_canvas = Canvas(top)
    optimized_canvas.pack(fill=BOTH, expand=1)


def run(filename):
    code = ozopython.compile(filename)
    colorcode = ColorLanguageTranslator.translate(code)

    def load(prog, prog_bar):
        colormap = {
            'K': "#000000",
            'R': "#ff0000",
            'G': "#00ff00",
            'Y': "#ffff00",
            'B': "#0000ff",
            'M': "#ff00ff",
            'C': "#00ffff",
            'W': "#ffffff"
        }

        head, *tail = prog
        canvas.itemconfig(circle, fill=colormap[head])
        prog = tail
        prog_bar["value"] = len(colorcode) - len(prog)
        if len(prog) != 0:
            canvas.after(50, lambda: load(prog, prog_bar))

    window = Tk()

    progress = ttk.Progressbar(window,orient="horizontal", length='5c', mode="determinate")
    progress["value"] = 0
    progress["maximum"] = len(colorcode)

    button = Button(window, text="Load", command=lambda: load(colorcode, progress))
    button.pack(pady=5)

    exit = Button(window, text="Exit", command=lambda: quit())
    exit.pack(side="bottom", pady=5)

    progress.pack()

    canvas = Canvas(window, height='6c', width='6c')
    circle = canvas.create_oval('0.5c', '0.5c', '5.5c', '5.5c', fill="white")
    canvas.pack()
    # window2 = Tk()
    # canvas2 = Canvas(window2, height='20c', width='20c')
    # circle = canvas2.create_oval('0.5c', '0.5c', '5.5c', '5.5c', fill="white")
    # canvas2.pack()
    # window2.mainloop()

    window.mainloop()


