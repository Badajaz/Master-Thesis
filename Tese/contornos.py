import numpy as np
import cv2
# load image
image = cv2.imread("/Users/goncalocardoso/Desktop/matrixyellow.jpg")# create hsv
hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

 # set lower and upper color limits
low_val = (60,180,160)
high_val = (179,255,255)
# Threshold the HSV image
mask = cv2.inRange(hsv, low_val,high_val)
# find contours in mask
contours, hierarchy = cv2.findContours(mask,cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

# select the largest contour
largest_area = 0
for cnt in contours:
    if cv2.contourArea(cnt) > largest_area:
        cont = cnt
        largest_area = cv2.contourArea(cnt)

# get the parameters of the boundingbox
x,y,w,h = cv2.boundingRect(cont)

# create and show subimage
roi = image[y:y+h, x:x+w]
cv2.imshow("Result", roi)

#  draw box on original image and show image
cv2.rectangle(image, (x,y),(x+w,y+h), (0,0,255),2)
cv2.imshow("Image", image)

cv2.waitKey(0)
cv2.destroyAllWindows()