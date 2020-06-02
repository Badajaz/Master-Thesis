import numpy as np
import cv2
from PIL import Image

def adjust_gamma(image, gamma=1.0):

   invGamma = 1.0 / gamma
   table = np.array([((i / 255.0) ** invGamma) * 255
      for i in np.arange(0, 256)]).astype("uint8")

   return cv2.LUT(image, table)

original = cv2.imread("downloaded.jpg", 1)

gamma = 2.5 # change the value here to get different result
adjusted = adjust_gamma(original, gamma=gamma)
cv2.imwrite("aa.jpg",adjusted)
imageCrop2 = Image.open(r"aa.jpg")
#(885, 1304, 1060, 1484)
#(1060, 1304, 1235, 1484)
#(1235, 1664, 1410, 1844)

crop2 = imageCrop2.crop(((545, 1444, 720, 1624)))
crop2.save('myphoto2.jpg', 'JPEG')
img2 = cv2.imread("myphoto2.jpg")




black_lower, black_upper = np.array([0, 0, 0]), np.array([180, 255, 50])

hsv = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)

lower_green, upper_green = np.array([36, 25, 25]), np.array([70, 255, 255])
mask_green = cv2.inRange(hsv, lower_green,upper_green)
greencnts = cv2.findContours(mask_green.copy(),
                              cv2.RETR_EXTERNAL,
                              cv2.CHAIN_APPROX_SIMPLE)[-2]



if len(greencnts)>0:
        green_area = max(greencnts, key=cv2.contourArea)
        (xg,yg,wg,hg) = cv2.boundingRect(green_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaGreen= abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        cv2.rectangle(img2,(xg,yg),(xg+wg, yg+hg),(0,255,0),9)



mask_black = cv2.inRange (hsv, black_lower,black_upper )
#mask2 = cv2.inRange (hsv, black_upper,black_upper)
#mask3 = mask1+mask2

blackcnts = cv2.findContours(mask_black.copy(),
                              cv2.RETR_EXTERNAL,
                              cv2.CHAIN_APPROX_SIMPLE)[-2]



if len(blackcnts)>0:
        black_area = max(blackcnts, key=cv2.contourArea)
        (xg,yg,wg,hg) = cv2.boundingRect(black_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaBlacks = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        cv2.rectangle(img2,(xg,yg),(xg+wg, yg+hg),(0,0,0),9)







lower_red1, upper_red1= np.array([0, 50, 20]), np.array([5, 255, 255])
lower_red2, upper_red2= np.array([175, 50, 20]), np.array([180, 255, 255])
#
mask1 = cv2.inRange (hsv, lower_red1,upper_red1 )
mask2 = cv2.inRange (hsv, lower_red2,upper_red2 )
mask_red = mask1+mask2
redcnts = cv2.findContours(mask_red.copy(),
                              cv2.RETR_EXTERNAL,
                              cv2.CHAIN_APPROX_SIMPLE)[-2]


if len(redcnts)>0:
        red_area = max(redcnts, key=cv2.contourArea)
        (xg,yg,wg,hg) = cv2.boundingRect(red_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaReds = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        cv2.rectangle(img2,(xg,yg),(xg+wg, yg+hg),(0,0,255),9)




lower_blue = np.array([100,50,50])
upper_blue = np.array([130,255,255])

mask_blue = cv2.inRange (hsv, lower_blue,upper_blue )
bluecnts = cv2.findContours(mask_blue.copy(),
                              cv2.RETR_EXTERNAL,
                              cv2.CHAIN_APPROX_SIMPLE)[-2]


if len(bluecnts)>0:
        blue_area = max(bluecnts, key=cv2.contourArea)
        (xg,yg,wg,hg) = cv2.boundingRect(blue_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaBlue = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        cv2.rectangle(img2, (xg, yg), (xg + wg, yg + hg), (255, 0, 0), 9)





imagemR = cv2.resize(img2, (500, 500))
cv2.imshow("image", imagemR)


cv2.waitKey(0)
cv2.destroyAllWindows()