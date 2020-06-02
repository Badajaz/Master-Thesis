from tkinter import *
from tkinter import ttk
window2 = Tk()
canvas2 = Canvas(window2, height='20c', width='20c')
circle = canvas2.create_oval('0.5c', '0.5c', '5.5c', '5.5c', fill="white")
canvas2.pack()
window2.mainloop()
