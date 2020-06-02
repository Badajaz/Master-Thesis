import cv2
import numpy as np
img = cv2.imread("/Users/goncalocardoso/Desktop/matrixyellow.jpg")
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
edges = cv2.Canny(gray, 75, 150)
lines = cv2.HoughLinesP(edges, 1, np.pi/180, 50)
#h, w = img.shape

#print(str(h) +" "+ str(w))

for line in lines:
    x1,y1,x2,y2 = line[0]
    print(line[0])
    cv2.line(img,(x1, y1) ,(x2, y2) , (0,255,0), 3 )


cv2.imshow("gray",gray)
cv2.imshow("Edges",edges)
cv2.imshow("Image",img)
cv2.waitKey(0)