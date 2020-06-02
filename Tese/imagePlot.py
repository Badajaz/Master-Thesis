# import pylab as plt
# #
# # # Load the image
# # img = plt.imread("/Users/goncalocardoso/Desktop/matrixyellow.jpg")
# # img.setflags(1)
# # # Grid lines at these intervals (in pixels)
# # # dx and dy can be different
# # dx, dy = 100,100
# #
# # # Custom (rgb) grid color
# # grid_color = [0,0,0]
# #
# # # Modify the image to include the grid
# # img[:,::dy,:] = grid_color
# # img[::dx,:,:] = grid_color
# #
# # # Show the result
# # plt.imshow(img)
# # plt.show()

############################## Funciona ################################

import cv2
img = cv2.imread("/Users/goncalocardoso/Desktop/matrixyellow.jpg")
GRID_SIZE = 150

height, width, channels = img.shape
for x in range(0, width -1, GRID_SIZE):
     cv2.line(img, (x, 0), (x, height), (0, 0, 255), 1, 1)

for y in range(0, height - 1, GRID_SIZE):

     cv2.line(img, (0, y), (width, y), (0, 0, 255), 1, 1)

cv2.imshow('Hehe', img)
key = cv2.waitKey(0)



