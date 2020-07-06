import cv2
import numpy as np


def getFourCorners(tuple1,tuple2):
    #TOP,BOTTOM
    return [tuple1,(tuple2[0],tuple1[1]),(tuple1[0],tuple2[1]),tuple2]


def boardRecognition(file):
    image = cv2.imread(file)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    mask = cv2.inRange(hsv, np.array([36, 25, 25]), np.array([70, 255, 255]))
    bluecnts = cv2.findContours(mask.copy(),
                                cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)[-2]
    if len(bluecnts) > 0:
        blue_area = max(bluecnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(blue_area)
        cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 255, 0), 2)


    fourCorners = getFourCorners((xg, yg),(xg + wg, yg + hg))