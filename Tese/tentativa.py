import cv2
import numpy as np
from matplotlib import pyplot as plt
fgbg = cv2.createBackgroundSubtractorMOG2(10,2,False)
image = cv2.imread("/Users/goncalocardoso/Desktop/matrixyellow.jpg")
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

# Extract the foreground
edges_foreground = cv2.bilateralFilter(gray, 9, 75, 75)
foreground = fgbg.apply(edges_foreground)

# Smooth out to get the moving area
kernel = np.ones((50, 50), np.uint8)
foreground = cv2.morphologyEx(foreground, cv2.MORPH_CLOSE, kernel)

# Applying static edge extraction
edges_foreground = cv2.bilateralFilter(gray, 9, 75, 75)
edges_filtered = cv2.Canny(edges_foreground, 60, 120)

# Crop off the edges out of the moving area
cropped = (foreground // 255) * edges_filtered

# Stacking the images to print them together for comparison
images = np.hstack((gray, edges_filtered, cropped))

cv2.imshow("lines", images)


cv2.waitKey(0)
cv2.destroyAllWindows()