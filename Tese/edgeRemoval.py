import cv2
import numpy as np


def printMatrix(matrix):
    for i in range(len(matrix)):
        print(matrix[i])


#img = cv2.imread("test.png")
img = cv2.imread("/Users/goncalocardoso/Desktop/foco.jpg")
blurred = cv2.blur(img, (3,3))

cv2.imshow("blur", blurred)

canny = cv2.Canny(blurred, 50, 200)

cv2.imshow("blur", blurred)

## find the non-zero min-max coords of canny
pts = np.argwhere(canny>0)
y1,x1 = pts.min(0)
y2,x2 = pts.max(0)

## crop the region
cropped = img[y1:y2, x1:x2]
cv2.imwrite("cropped.png", cropped)
cv2.imshow("cropped", cropped)

tagged = cv2.rectangle(img.copy(), (x1,y1), (x2,y2), (0,255,0), 3, cv2.LINE_AA)
cv2.imshow("tagged", tagged)
cv2.waitKey()