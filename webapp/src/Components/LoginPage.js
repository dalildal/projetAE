/* In a template literal, the ` (backtick), \ (backslash), and $ (dollar sign) characters should be 
escaped using the escape character \ if they are to be included in their template value. 
By default, all escape sequences in a template literal are ignored.*/
import { getUserLocalData, getUserSessionData, setUserLocalData, setUserSessionData } from "../utils/session.js";
import { setUserData } from "./User";
import { RedirectUrl } from "./Router.js";
import Navbar from "./Navbar.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import ToastWidget from "./Widgets/ToastWidget.js";
const API_BASE_URL = "/api/users/";

let loginPage = `
<div class="row">
  <div class="col col-md-3 mb-5"></div>
  <div class="col col-md-6 mb-5">
    <div class="formCase">
      <div id="formContent">
        <div id="formHeader">
          <h4 id="pageTitle">Se connecter</h4>
        </div>
        <div id=formFooter">
          <form>
            <div class="form-floating mb-2">
              <input class="form-control" id="pseudo" type="text" placeholder="Veuillez entrer votre pseudo" />
              <label for="pseudo">Pseudo</label>
            </div>
            <div class="form-floating mb-2">
              <input class="form-control" id="password" type="password" name="password" placeholder="Veuillez entrer votre mot de passe"  />
              <label for="password">Mot de passe</label>
            </div>
            <input class="btn-check" id="remember" type="checkbox" name="remember" />
            <label class="btn btn-outline-success" for="remember">Se souvenir de moi</label>
            <button class="btn btn-primary" id="btn" type="submit">Se connecter</button>
            <!-- Create an alert component with bootstrap that is not displayed by default-->
            <div class="alert alert-danger mt-2 d-none" id="messageBoard"></div>
          </form>
        </div>
      </div>
    </div>
  </div>
  <div class="col col-md-3 mb-5"></div>
</div>
`;

const LoginPage = () => {
  let page = document.querySelector("#page");
  page.innerHTML = loginPage;
  let loginForm = document.querySelector("form");
  const user = getUserSessionData();
  const userBis = getUserLocalData();
  if (user || userBis) {
    // re-render the navbar for the authenticated user
    Navbar();
    RedirectUrl("/");
  } else loginForm.addEventListener("submit", onLogin);
};

const onLogin = async (e) => {
  e.preventDefault();
  let pseudo = document.getElementById("pseudo");
  let password = document.getElementById("password");
  let remember = document.getElementById("remember").checked;

  let nextStep = true;
  pseudo.addEventListener("input", () => {
    if(pseudo.value == "") {
      pseudo.classList.add('is-invalid');
      nextStep = false;
    } else {
      pseudo.classList.remove('is-invalid');
      pseudo.classList.add('is-valid');
    }
  });
  if(pseudo.value == "") nextStep = false;
  password.addEventListener("input", () => {
    if(password.value == "") {
      password.classList.add('is-invalid');
      nextStep = false;
    } else {
      password.classList.remove('is-invalid');
      password.classList.add('is-valid');
    }
  });
  if(password.value == "") nextStep = false;

  if(nextStep) {
    let user = {
      pseudo: pseudo.value,
      password: password.value,
    };

    try {
      const userLogged = await callAPI(
        API_BASE_URL + "login",
        "POST",
        undefined,
        user
      );
      onUserLogin(userLogged, remember);
    } catch (err) {
      console.error("LoginPage::onLogin", err);
      PrintError(err);
    }
  } else {
    ToastWidget("fail", "Veuillez indiquez un pseudo et un mot de passe.");
  }
};

const onUserLogin = (userData, remember) => {
  const user = { token: userData.token, isAutenticated: true };
  let userJson = JSON.parse(userData.user);
  if (remember) {
    setUserData(userJson.id, userJson.pseudo, userJson.lastName, userJson.firstName, userJson.email, userJson.type);
    setUserLocalData(user);
  } else {
    setUserData(userJson.id, userJson.pseudo, userJson.lastName, userJson.firstName, userJson.email, userJson.type);
    setUserSessionData(user);
  }
  // re-render the navbar for the authenticated user
  Navbar();
  RedirectUrl("/");
};

export default LoginPage;
