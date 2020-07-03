import numpy as np
import cv2
import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage
from pyrebase.pyrebase import Firebase
from PIL import Image



config = {
    "apiKey":"AIzaSyCbwjD-zqfeO2-5hsHIhF6jHoFTnzFuGQg",
   "authDomain": "project-1085473150640.firebaseapp.com",
   "serviceAccount":"/Users/goncalocardoso/PycharmProjects/OpenCV/tese-3ed7d-firebase-adminsdk-4xm5b-3176ad9835.json",
  "databaseURL": "https://tese-3ed7d.firebaseio.com/",
  "storageBucket": "tese-3ed7d.appspot.com"
}
firebase = Firebase(config)
storage = firebase.storage()


storage.child("images").download("downloaded.jpg")
#image = Image.open(r"downloaded.jpg")
#crop = image.crop((655, 1841, 835, 2034))
#crop.save('myphoto.jpg', 'JPEG')
image = cv2.imread("downloaded.jpg")
#image = cv2.imread("/Users/goncalocardoso/Desktop/black.png")


hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
# define range of blue color in HSV
lower_blue = np.array([100,50,50])
upper_blue = np.array([130,255,255])
lower_green, upper_green= np.array([36, 25, 25]), np.array([70, 255, 255])


# Threshold the HSV image to get only blue colors
#mask = cv2.inRange (hsv, np.array([36,25,25]), np.array([70, 255,255]))
# mask_blue = cv2.inRange (hsv, lower_blue,upper_blue )
# bluecnts = cv2.findContours(mask_blue.copy(),
#                               cv2.RETR_EXTERNAL,
#                               cv2.CHAIN_APPROX_SIMPLE)[-2]
#
#
# if len(bluecnts)>0:
#         blue_area = max(bluecnts, key=cv2.contourArea)
#         (xg,yg,wg,hg) = cv2.boundingRect(blue_area)
#         cv2.rectangle(image,(xg,yg),(xg+wg, yg+hg),(0,255,0),3)
#         tuple1 = (xg, yg)
#         tuple2 = (xg + wg, yg + hg)
#         areaBlue = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
#         cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (255, 0, 0), 9)
#
# #
# #
# mask_green = cv2.inRange (hsv, lower_green,upper_green )
# greencnts = cv2.findContours(mask_green.copy(),
#                               cv2.RETR_EXTERNAL,
#                               cv2.CHAIN_APPROX_SIMPLE)[-2]
#
#
#
# if len(greencnts)>0:
#         green_area = max(greencnts, key=cv2.contourArea)
#         (xg,yg,wg,hg) = cv2.boundingRect(green_area)
#         tuple1 = (xg, yg)
#         tuple2 = (xg + wg, yg + hg)
#         areaGreen= abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
#         cv2.rectangle(image,(xg,yg),(xg+wg, yg+hg),(0,255,0),9)
#
#
#
# lower_red1, upper_red1= np.array([0, 50, 20]), np.array([5, 255, 255])
# lower_red2, upper_red2= np.array([175, 50, 20]), np.array([180, 255, 255])
# #
# mask1 = cv2.inRange (hsv, lower_red1,upper_red1 )
# mask2 = cv2.inRange (hsv, lower_red2,upper_red2 )
# mask_red = mask1+mask2
# redcnts = cv2.findContours(mask_red.copy(),
#                               cv2.RETR_EXTERNAL,
#                               cv2.CHAIN_APPROX_SIMPLE)[-2]
#
#
# if len(redcnts)>0:
#         red_area = max(redcnts, key=cv2.contourArea)
#         (xg,yg,wg,hg) = cv2.boundingRect(red_area)
#         tuple1 = (xg, yg)
#         tuple2 = (xg + wg, yg + hg)
#         areaReds = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
#         cv2.rectangle(image,(xg,yg),(xg+wg, yg+hg),(0,0,255),9)
#
#
# #[50,50,100]
# black_lower, black_upper = np.array([0, 0, 0]), np.array([180, 255, 50])
#
#
# mask_black = cv2.inRange (hsv, black_lower,black_upper )
# #mask2 = cv2.inRange (hsv, black_upper,black_upper)
# #mask3 = mask1+mask2
#
# blackcnts = cv2.findContours(mask_black.copy(),
#                               cv2.RETR_EXTERNAL,
#                               cv2.CHAIN_APPROX_SIMPLE)[-2]
#
#
#
# if len(blackcnts)>0:
#         black_area = max(blackcnts, key=cv2.contourArea)
#         (xg,yg,wg,hg) = cv2.boundingRect(black_area)
#         tuple1 = (xg, yg)
#         tuple2 = (xg + wg, yg + hg)
#         areaBlacks = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
#         cv2.rectangle(image,(xg,yg),(xg+wg, yg+hg),(0,0,0),9)
#
# #print("areaBlue = "+str(areaBlue))
# print("areaGreen = "+str(areaGreen))
# print("areaReds = "+str(areaReds))
# print("areaBlacks = "+str(areaBlacks))

mask_orange = mask = cv2.inRange(hsv,(10, 100, 20), (25, 255, 255))

orangecnts = cv2.findContours(mask_orange.copy(),
                               cv2.RETR_EXTERNAL,
                               cv2.CHAIN_APPROX_SIMPLE)[-2]



if len(orangecnts)>0:
        orange_area = max(orangecnts, key=cv2.contourArea)
        (xg,yg,wg,hg) = cv2.boundingRect(orange_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaBlacks = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        cv2.rectangle(image,(xg,yg),(xg+wg, yg+hg),(255,0,0),9)






imS = cv2.resize(image, (700, 700))
cv2.imshow('frame',imS)


cv2.waitKey(0)
cv2.destroyAllWindows()