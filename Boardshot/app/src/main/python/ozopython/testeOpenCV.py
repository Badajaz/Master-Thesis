#!/usr/bin/env python
import cv2
import numpy as np
from matplotlib import pyplot as plt

def getColor(image):
    matrix = []
    for i in range(len(image)):
        for j in range(len(image[0])):

            # red color
            if 178 <= image[i][j][0] <= 255 and 179 <= image[i][j][1] <= 255 and 0 <= image[i][j][2] <= 255 :
               matrix.append(["red"])



            elif 110 <= image[i][j][0] <= 130 and 50 <= image[i][j][1] <= 255 and 50 <= image[i][j][2] <= 255 :
                 matrix.append(["blue"])

            else:
                matrix.append(["other"])

    return matrix;

def printMatrix(matrix):
    for i in range(len(matrix)):
        print(matrix[i])


image = cv2.imread("/Users/goncalocardoso/Desktop/yo5.jpg")

blurred = cv2.blur(image, (3,3))

cv2.imshow("blur", blurred)

canny = cv2.Canny(blurred, 50, 200)

cv2.imshow("canny", canny)

## find the non-zero min-max coords of canny
pts = np.argwhere(canny>20)
y1,x1 = pts.min(0)
y2,x2 = pts.max(0)

## crop the region
cropped = image[y1:y2, x1:x2]
cv2.imwrite("cropped.png", cropped)
cv2.imshow("cropped", cropped)

tagged = cv2.rectangle(image.copy(), (x1,y1), (x2,y2), (0,255,0), 3, cv2.LINE_AA)
cv2.imshow("tagged", tagged)

hsv = cv2.cvtColor(tagged, cv2.COLOR_BGR2HSV)
#mask1
lower_red_mask1 = np.array([0, 70, 50])
upper_red_mask1 = np.array([10, 255, 255])


mask1 = cv2.inRange(hsv, lower_red_mask1, upper_red_mask1)

lower_red_mask2 = np.array([170, 70, 50])
upper_red_mask2 = np.array([180, 255, 255])


mask2 = cv2.inRange(hsv, lower_red_mask2, upper_red_mask2)

mask_green = cv2.inRange(hsv, (36, 0, 0), (70, 255,255))


mask_sum = mask1+mask2
cv2.imshow("image", image)
cv2.imshow("mask", mask_sum)
cv2.imshow("mask", mask_green)

#edges = cv2.Canny(image, 100, 200)
edges = cv2.Canny(mask_sum+mask_green, 100, 200)

cv2.imshow("edges", edges)
#cv2.imshow("edges_image", edges_image)



lines = cv2.HoughLinesP(edges, 200, np.pi/180, 50)

#h, w = img.shape

#print(str(h) +" "+ str(w))

for line in lines:
    x1,y1,x2,y2 = line[0]
    cv2.line(image,(x1,y1) ,(x2,y2) , (0,255,0), 3 )

cv2.imshow("lines", image)




#coord = cv2.findNonZero(mask)
#h, w = mask.shape

#res = cv2.bitwise_and(hsv,hsv, mask)

#cv2.imshow("RES", res)


###################################### #################################

#
# #print(mask)
# gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
# edges = cv2.Canny(gray, 100, 200)
# printMatrix(edges)
#
# cv2.imshow("Edges", edges)
#
# indices = np.where(edges != [0])
# coordinates = zip(indices[0], indices[1])
# #print(coordinates[0])
#
#
# gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
#
# #gray_image = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
#
# # convert the grayscale image to binary image
# ret,thresh = cv2.threshold(gray_image,127,255,0)
#
# # find contours in the binary image
# contours, hierarchy = cv2.findContours(thresh,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
# for c in contours:
#    # calculate moments for each contour
#    M = cv2.moments(c)
#
#    # calculate x,y coordinate of center
#    if M["m00"] != 0:
#        cX = int(M["m10"] / M["m00"])
#        cY = int(M["m01"] / M["m00"])
#       # print("("+str(cX)+","+str(cY)+")")
#    else:
#        # set values as what you need in the situation
#        cX, cY = 0, 0
#    cv2.circle(image, (cX, cY), 5, (255, 255, 255), -1)
#    cv2.putText(image, "", (cX - 25, cY - 25),cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
#
#
# cv2.imshow("Centered points", image)

cv2.waitKey(0)
cv2.destroyAllWindows()
