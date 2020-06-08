import cv2
import numpy as np

def getBlackCounts(image):
    image = cv2.imread(image)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    black_lower, black_upper = np.array([0, 0, 0]), np.array([180, 255, 50])

    lower_yellow = np.array([22, 93, 0])
    upper_yellow = np.array([45, 255, 255])
    mask_black = cv2.inRange(hsv, lower_yellow, upper_yellow)

    areaBlacks = -1
    # mask2 = cv2.inRange (hsv, black_upper,black_upper)
    # mask3 = mask1+mask2

    blackcnts = cv2.findContours(mask_black.copy(),
                                 cv2.RETR_EXTERNAL,
                                 cv2.CHAIN_APPROX_SIMPLE)[-2]


    if len(blackcnts) > 0:
        black_area = max(blackcnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(black_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaBlacks = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        #cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 0, 0), 9)
    return areaBlacks