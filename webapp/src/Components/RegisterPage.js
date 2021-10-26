import { RedirectUrl } from "./Router.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import ToastWidget from "./Widgets/ToastWidget.js";
const API_BASE_URL = "/api/users/";

let registerPage = `
  <div class="row">
    <div class="col col-md-3 mb-5"></div>
    <div class="col col-md-6 mb-5">
      <div class="formCase">
        <div id="formContent">
          <div id="formHeader">
            <h4 id="pageTitle">Créer un compte</h4>
          </div>
          <div id=formFooter">
            <form>
              <div class="form-floating mb-2">
                <input class="form-control" id="pseudo" type="text" placeholder="Veuillez entrer pseudo" />
                <label for="pseudo">Pseudo<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="password" type="password" name="password" placeholder="Veuillez entrer un mot de passe" />
                <label for="password">Mot de passe<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="last_name" type="text" placeholder="Veuillez entrer votre nom" />
                <label for="last_name">Prénom<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="first_name" type="text" placeholder="Veuillez entrer votre prénom" />
                <label for="first_name">Nom<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="street" type="text" placeholder="Veuillez entrer votre adresse" />
                <label for="street">Adresse<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="street_number" type="text" min="1" placeholder="Veuillez entrer votre numéro" />
                <label for="street_number">Numéro<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="street_box" type="text" placeholder="Veuillez entrer votre boîte" />
                <label for="street_box">Boîte</label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="postal_code" type="number" min="1" placeholder="Veuillez entrer votre code postal" />
                <label for="postal_code">Code postal<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="municipality" type="text" placeholder="Veuillez entrer votre commune" />
                <label for="municipality">Commune<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="country" type="text" placeholder="Veuillez entrer votre pays" />
                <label for="country">Pays<span class="text-danger">*</span></label>
              </div>
              <div class="form-floating mb-2">
                <input class="form-control" id="email" type="email" placeholder="Veuillez entrer votre email" />
                <label for="email">Email<span class="text-danger">*</span></label>
              </div>
              <button class="btn btn-primary" id="btn" type="submit">S'inscrire</button>
              <!-- Create an alert component with bootstrap that is not displayed by default-->
              <div class="alert alert-danger mt-2 d-none" id="messageBoard"></div><span id="errorMessage"></span>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="col col-md-3 mb-5"></div>
  </div>
`;

const RegisterPage = () => {
  let page = document.querySelector("#page");
  page.innerHTML = registerPage;
  let registerForm = document.querySelector("form");
  registerForm.addEventListener("submit", onRegister);
};

const onRegister = async (e) => {
  e.preventDefault();

  let pseudo = document.getElementById("pseudo");
  let password = document.getElementById("password");
  let first_name = document.getElementById("first_name");
  let last_name = document.getElementById("last_name");
  let street = document.getElementById("street");
  let street_number = document.getElementById("street_number");
  let street_box = document.getElementById("street_box");
  let postal_code = document.getElementById("postal_code");
  let municipality = document.getElementById("municipality");
  let country = document.getElementById("country");
  let email = document.getElementById("email");

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
  first_name.addEventListener("input", () => {
    if(first_name.value == "") {
      first_name.classList.add('is-invalid');
      nextStep = false;
    } else {
      first_name.classList.remove('is-invalid');
      first_name.classList.add('is-valid');
    }
  });
  if(first_name.value == "") nextStep = false;
  last_name.addEventListener("input", () => {
    if(last_name.value == "") {
      last_name.classList.add('is-invalid');
      nextStep = false;
    } else {
      last_name.classList.remove('is-invalid');
      last_name.classList.add('is-valid');
    }
  });
  if(last_name.value == "") nextStep = false;
  street.addEventListener("input", () => {
    if(street.value == "") {
      street.classList.add('is-invalid');
      nextStep = false;
    } else {
      street.classList.remove('is-invalid');
      street.classList.add('is-valid');
    }
  });
  if(street.value == "") nextStep = false;
  street_number.addEventListener("input", () => {
    if(street_number.value == "") {
      street_number.classList.add('is-invalid');
      nextStep = false;
    } else {
      street_number.classList.remove('is-invalid');
      street_number.classList.add('is-valid');
    }
  });
  if(street_number.value == "") nextStep = false;
  postal_code.addEventListener("input", () => {
    if(postal_code.value == "") {
      postal_code.classList.add('is-invalid');
      nextStep = false;
    } else {
      postal_code.classList.remove('is-invalid');
      postal_code.classList.add('is-valid');
    }
  });
  if(postal_code.value == "") nextStep = false;
  municipality.addEventListener("input", () => {
    if(municipality.value == "") {
      municipality.classList.add('is-invalid');
      nextStep = false;
    } else {
      municipality.classList.remove('is-invalid');
      municipality.classList.add('is-valid');
    }
  });
  if(municipality.value == "") nextStep = false;
  country.addEventListener("input", () => {
    if(country.value == "") {
      country.classList.add('is-invalid');
      nextStep = false;
    } else {
      country.classList.remove('is-invalid');
      country.classList.add('is-valid');
    }
  });
  if(country.value == "") nextStep = false;
  email.addEventListener("input", () => {
    if(email.value == "") {
      email.classList.add('is-invalid');
      nextStep = false;
    } else if (validateEmail(email.value)) {
      email.classList.remove('is-invalid');
      email.classList.add('is-valid');
    } else {
      email.classList.add('is-invalid');
      nextStep = false;
    }
  });


  function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
  }

  if(nextStep) {
    let user = {
      pseudo: pseudo.value,
      pwd: password.value,
      firstName: first_name.value,
      lastName: last_name.value,
      street: street.value,
      num: street_number.value,
      box: street_box.value,
      postalCode: postal_code.value,
      municipality: municipality.value,
      country: country.value,
      email: email.value
    };

    try {
      const userRegistered = await callAPI(
        API_BASE_URL + "register",
        "POST",
        undefined,
        user
      );
      onUserRegistration(userRegistered);
    } catch (err) {
      console.error("RegisterPage::onRegister", err);
      PrintError(err);
    }
  } else {
    ToastWidget("fail", "Merci de remplir tous les champs obligatoires");
  }
};

const onUserRegistration = (userData) => {
  RedirectUrl("/login");
};

export default RegisterPage;
