import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import storage
import requests
from pyrebase.pyrebase import Firebase

cred = credentials.Certificate("/Users/goncalocardoso/PycharmProjects/OpenCV/tese-3ed7d-firebase-adminsdk-4xm5b-3176ad9835.json");
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
'databaseURL': 'https://tese-3ed7d.firebaseio.com/',
"storageBucket": "gs://tese-3ed7d.appspot.com"
});
config = {
    "apiKey":"AIzaSyCbwjD-zqfeO2-5hsHIhF6jHoFTnzFuGQg",
   "authDomain": "project-1085473150640.firebaseapp.com",
   "serviceAccount":"/Users/goncalocardoso/PycharmProjects/OpenCV/tese-3ed7d-firebase-adminsdk-4xm5b-3176ad9835.json",
  "databaseURL": "https://tese-3ed7d.firebaseio.com/",
  "storageBucket": "tese-3ed7d.appspot.com"
}

ref = db.reference('/');
storage.bucket();

snapshot = ref.get()["sequencia"]
print(snapshot)
############ Fetch photo ###############################

firebase = Firebase(config)
storage = firebase.storage()
storage.child("images").download("downloaded.jpg")


