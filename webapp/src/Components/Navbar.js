let navBar = document.querySelector("#navBar");
import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { getUserData } from "./User";
import logoFav from "../images/logo-fav.png";
// destructuring assignment
const Navbar = () => {
  let navbar;
  let user = getUserSessionData();
  let userData = getUserData();
  let userBis = getUserLocalData();
  if ((user || userBis) && userData.type == "admin") {
    navbar = `<nav class="navbar navbar-expand-lg navbar-light bg-light mb-2" id="navBar">
  <a class="navbar-brand" href="/" data-uri="/"><img src="${logoFav}" width="50px" height="50px"></a>
  <button
    class="navbar-toggler"
    type="button"
    data-toggle="collapse"
    data-target="#navbarNavAltMarkup"
    aria-controls="navbarNavAltMarkup"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
    <div class="navbar-nav">
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/">Accueil</a>    
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/option">Mes Options</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/admin">Zone Admin</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/registerVisit">Demander une visite</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/logout">Se déconnecter</a>
  <a class="nav-item nav-link disabled" href="#">${userData.pseudo}</a>
    </div>
  </div>
  </nav>`;
  } else if (user || userBis) {
    navbar = `<nav class="navbar navbar-expand-lg navbar-light bg-light mb-2" id="navBar">
  <a class="navbar-brand" href="/" data-uri="/"><img src="${logoFav}" width="50px" height="50px"></a>
  <button
    class="navbar-toggler"
    type="button"
    data-toggle="collapse"
    data-target="#navbarNavAltMarkup"
    aria-controls="navbarNavAltMarkup"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
    <div class="navbar-nav">
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/">Accueil</a>    
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/option">Mes Options</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/registerVisit">Demander une visite</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/logout">Se déconnecter</a>
  <a class="nav-item nav-link disabled" href="#">${userData.pseudo}</a>
    </div>
  </div>
  </nav>`;
  } else {
    navbar = `<nav class="navbar navbar-expand-lg navbar-light bg-light mb-2" id="navBar">
  <a class="navbar-brand" href="/" data-uri="/"><img src="${logoFav}" width="50px" height="50px"></a>
  <button
    class="navbar-toggler"
    type="button"
    data-toggle="collapse"
    data-target="#navbarNavAltMarkup"
    aria-controls="navbarNavAltMarkup"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
    <div class="navbar-nav">
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/">Accueil</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/register">S'inscrire</a>
      <a class="nav-item nav-link nav-bar" href="#" data-uri="/login">Se connecter</a> 
    </div>
  </div>
  </nav>`;
  }

  return (navBar.innerHTML = navbar);
};

export default Navbar;
