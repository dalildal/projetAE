import { Router, RedirectUrl } from "./Components/Router.js";
import { getUserSessionData, getUserLocalData } from "./utils/session.js";
import { setUserData } from "./Components/User";
import PrintError from "./Components/PrintError.js";
import Navbar from "./Components/Navbar.js";
/* use webpack style & css loader*/
import "./stylesheets/style.css";
/* load bootstrap css (web pack asset management) */
import 'bootstrap/dist/css/bootstrap.css';
/* load bootstrap module (JS) */
import 'bootstrap';
/* load lightbox2 css (web pack asset management) */
import 'lightbox2/dist/css/lightbox.css'
/* load lightbox2 module (JS) */
import 'lightbox2/dist/js/lightbox-plus-jquery.js'
/* load fontawesome module (JS) */
import '@fortawesome/fontawesome-free/js/all.js'

import 'slick-carousel/slick/slick.css'
import 'slick-carousel/slick/slick.min.js'
import 'slick-carousel/slick/slick-theme.css'


import callAPI from "./utils/api.js";

const API_BASE_URL = "/api/users/";
const HEADER_TITLE = "Antiqu'App";
const FOOTER_TEXT = "© Create by Ramafiba SRL";

/* manage user API */
const verifyToken = async () => {
  const user = getUserSessionData();
  const userBis = getUserLocalData();

  if (user || userBis) {
    try {
      let token;
      if (user) {
        token = user.token;
      } else if (userBis) {
        token = userBis.token;
      }
      const userData = await callAPI(API_BASE_URL + "me", "GET", token)
      setUserData(userData.id, userData.pseudo, userData.firstName, userData.lastName, userData.email, userData.type);
    } catch (err) {
      RedirectUrl("/logout");
      console.error("UserListPage::onUserList", err);
      PrintError(err);
    }
  }

}

verifyToken().then(() => {
  Navbar();

  Router();
})

document.querySelector("#headerTitle").innerText = HEADER_TITLE;
document.querySelector("title").innerHTML = HEADER_TITLE;
document.querySelector("#footerText").innerText = FOOTER_TEXT;
