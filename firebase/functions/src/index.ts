import * as admin from 'firebase-admin';
import * as functions from "firebase-functions";

const app = admin.initializeApp();
const firestore = app.firestore();

const COL_USERS = "users";

export const createUserDoc = functions.region("asia-southeast2").auth.user().onCreate((user) => {
  firestore.collection("users").doc();
});

export const isPhoneRegistered = functions.https.onCall()