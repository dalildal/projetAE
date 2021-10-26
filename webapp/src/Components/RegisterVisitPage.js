import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { getUserData } from "./User";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import { RedirectUrl } from "./Router.js";
import TypeFurnitureWidget from "./Widgets/TypeFurnitureWidget.js";
import ToastWidget from "./Widgets/ToastWidget.js";
const API_FURNITURE_URL = "/api/furniture/";
const API_VISIT_URL = "/api/visits/";
const API_PICTURE_URL = "/api/pictures/";

let registerVisitPage = `
  <div class="row">
    <div class="col col-md-3 mb-5"></div>
      <div class="col col-md-6 mb-5">
        <div class="formCase">
          <div id="formContent">
            <div id="formHeader">
              <h5 id="pageTitle" class="visitPageTitle">Introduire une demande de visite</h5>
            </div>
            <div id=formFooter">
              <form>
                  <div class="form-floating mb-2">
                      <input class="form-control" id="timePeriod" type="text" placeholder="Plage horaire de la visite" />
                      <label for="timePeriod">Plage horaire<span class="text-danger">*</span></label>
                  </div>
                  <div class="form-floating mb-2">
                      <input class="form-control" id="street" type="text" placeholder="Veuillez entrer votre adresse" />
                      <label for="street">Adresse<span class="text-danger">*</span></label>
                  </div>
                  <div class="form-floating mb-2">
                      <input class="form-control" id="street_number" type="text" min="1"
                          placeholder="Veuillez entrer votre numéro" />
                      <label for="street_number">Numéro<span class="text-danger">*</span></label>
                  </div>
                  <div class="form-floating mb-2">
                      <input class="form-control" id="street_box" type="text" placeholder="Veuillez entrer votre boîte" />
                      <label for="street_box">Boîte</label>
                  </div>
                  <div class="form-floating mb-2">
                      <input class="form-control" id="postal_code" type="number" min="1"
                          placeholder="Veuillez entrer votre code postal" />
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
                  <h6>Ajouter un ou plusieurs meuble(s)<span class="text-danger">*</span></h6>
                  <div class="slider add-remove">
                  </div>
                  <div class="buttons">
                    <a href="javascript:void(0)" class="button js-add-slide">Ajouter un meuble</a>
                    <a href="javascript:void(0)" class="button js-remove-slide">Supprimer un meuble</a>
                  </div>
                  <br>
                  <button class="btn btn-primary" id="btn" type="enregistrer">Envoyer</button>
                  <!-- Create an alert component with bootstrap that is not displayed by default-->
                  <div class="alert alert-danger mt-2 d-none" id="messageBoard"></div>
              </form>
            </div>
          </div>
        </div>
      </div>
      <div class="col col-md-3 mb-5"></div>
    </div>
  </div>
`;

let token;
let furnitureList = [];
let picList = [];
let slideIndex = 0;

const RegisterVisitPage = () => {
  let page = document.querySelector("#page");
  page.innerHTML = registerVisitPage;
  const user = getUserSessionData();
  const userBis = getUserLocalData();

  if (userBis) {
    token = userBis.token;
  } else if (user) {
    token = user.token;
  }

  try {
    onRequestVisit();
  } catch (err) {
    console.error("RegisterVisitPage::registerVisitPage", err);
    PrintError(err);
    if (err.message == "Malformed token") RedirectUrl("/logout");
  }
  let registerVisitForm = document.querySelector("form");
  registerVisitForm.addEventListener('submit', onRegisterVisit);
};

const onRegisterVisit = async (e) => {
  e.preventDefault();
  let userData = getUserData();

  let timePeriod = document.getElementById("timePeriod");
  let street = document.getElementById("street");
  let street_number = document.getElementById("street_number");
  let street_box = document.getElementById("street_box");
  let postal_code = document.getElementById("postal_code");
  let municipality = document.getElementById("municipality");
  let country = document.getElementById("country");

  let nextStep = true;
  timePeriod.addEventListener("input", () => {
    if(timePeriod.value == "") {
      timePeriod.classList.add('is-invalid');
      nextStep = false;
    } else {
      timePeriod.classList.remove('is-invalid');
      timePeriod.classList.add('is-valid');
    }
  });
  if(timePeriod.value == "") nextStep = false;
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
  if(picList.length == 0) nextStep = false;

  if(nextStep) {

    for (let i = 1; i <= slideIndex; i++) {
      furnitureList.push({
        'idType': document.getElementById("type_meuble" + i).selectedIndex + 1,
        'description': "Pas de description",
      });
    }

    let visit = {
      idUser: userData.id_utilisateur,
      timePeriod: timePeriod.value,
      streetVisit: street.value,
      numVisit: street_number.value,
      boxVisit: street_box.value,
      postalCodeVisit: postal_code.value,
      municipalityVisit: municipality.value,
      countryVisit: country.value,
    }

    try {

      let count = 0;
      let tmp;
      picList.forEach((element) => {
        if (tmp != element.idFurniture) {
          tmp = element.idFurniture;
          count++;
        }
      });
      if (count != furnitureList.length) {
        PrintError(new Error("Veuillez introduire minimum une photo"));
      } else {

        const visitCreated = await callAPI(API_VISIT_URL + "add", "POST", token, visit);

        let furnitureCreated = [];
        const listTmp = [];
        furnitureList.forEach((element, i) => {
          listTmp.push(createFurniture(element, furnitureCreated, i));
        });

        Promise.all(listTmp).then(() => {

          furnitureCreated.sort(compare);

          function compare(a, b) {
            if (a.id < b.id) {
              return -1;
            }
            if (a.id > b.id) {
              return 1;
            }
            return 0;
          }

          let cmpt = 0;
          let tmp = picList[0].idFurniture;

          picList.forEach(async (pic) => {
            if (tmp != pic.idFurniture) cmpt = 0;
            let picture = {
              idFurniture: furnitureCreated[pic.idFurniture - 1].idFurniture,
              link: pic.pic,
            }
            if (cmpt == 0) {
              const pictureCreated = await callAPI(API_PICTURE_URL + "add", "POST", token, picture);
              let data = {
                etat: null,
                date: null,
                prix: null,
                prixAntiquaire: null,
                dateRecuperation: null,
                prixAchat: null,
                description: null,
                type: null,
                idPicture: pictureCreated
              };
              const furnitureUpdated = await callAPI(API_FURNITURE_URL + furnitureCreated[pic.idFurniture - 1].idFurniture, "PUT", token, data);
            } else {
              const pictureCreated = await callAPI(API_PICTURE_URL + "add", "POST", token, picture);
            }
            cmpt++;
          });

          picList = [];

          furnitureCreated.forEach(async (element) => {
            let furnitureVisit = {
              idVisit: visitCreated,
              idFurniture: element.idFurniture,
            }

            const furnitureVisitCreated = await callAPI(API_VISIT_URL + "addFurnitureVisit", "POST", token, furnitureVisit);
            onVisitRegistered(visitCreated);
          });

        });
      }
    } catch (error) {
      console.error("RegisterVisitPage:: onRegisterVisit", error);
    }

    slideIndex = 0;
    furnitureList = [];
  } else {
    ToastWidget("fail", "Merci de remplir tous les champs obligatoires");
  }

};

const onVisitRegistered = (visitData) => {
  RedirectUrl("/");
};

const onRequestVisit = () => {
  createCarousel();
};

const createCarousel = () => {

  let $ = require('jquery');
  $('.add-remove').slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    focusOnSelect: true,
    dots: true,
  });
  $('.js-add-slide').on('click', function () {
    if (slideIndex < 5) {
      slideIndex++;
      $('.add-remove').slick('slickAdd', addElementCarousel(slideIndex));
      dragAndDrop(slideIndex);
      TypeFurnitureWidget(slideIndex);
    } else {
      ToastWidget("fail", "Vous ne pouvez ajouter que 5 meubles par visite");
    }
  });
  $('.js-remove-slide').on('click', function () {
    $('.add-remove').slick('slickRemove', slideIndex - 1);
    deletePics(slideIndex);
    if (slideIndex !== 0) {
      slideIndex--;
    }
  });

};

const deletePics = (index) => {
  picList.sort(compare);

  let cmp = 0;
  picList.forEach((element) => {
    if (element.idFurniture == index) cmp++;
  });
  for (let i = 0; i < cmp; i++) {
    picList.pop();
  }

  function compare(a, b) {
    if (a.idFurniture < b.idFurniture) {
      return -1;
    }
    if (a.idFurniture > b.idFurniture) {
      return 1;
    }
    return 0;
  }
};

const addElementCarousel = (index) => {
  let carousel = `
  <div>
    <div class="form-floating mb-2" id="divPicture">
      <p>Meuble n°${index}</p>
      <div class="drop-zone" id="${index}">
          <span class="drop-zone__prompt">Glissez un fichier ou cliquer pour upload</span>
          <input type="file" id="${index}" name="image" class="drop-zone__input drop-zone__input${index}" multiple>
      </div>
    </div>
    <br>
    <div class="form-floating mb-2" id="divSelect${index}"></div>
  </div>
  `;

  return carousel;
};

const dragAndDrop = (index) => {
  document.querySelectorAll(".drop-zone__input" + index).forEach(inputElement => {
    const dropZoneElement = inputElement.closest(".drop-zone");

    dropZoneElement.addEventListener("click", (e) => {
      inputElement.click();
    });

    inputElement.addEventListener("change", (e) => {
      if (inputElement.files.length) {
        updateThumbnail(dropZoneElement, inputElement.files[0]);
      }
      for (let i = 0; i < inputElement.files.length; i++) {
        toBase64(inputElement.files[i], inputElement.id);
      }
    });

    dropZoneElement.addEventListener("dragover", e => {
      e.preventDefault();
      dropZoneElement.classList.add("drop-zone--over");
    });

    ["dragleave", "dragend"].forEach(type => {
      dropZoneElement.addEventListener(type, e => {
        dropZoneElement.classList.remove("drop-zone--over");
      });
    });

    dropZoneElement.addEventListener("drop", (e) => {
      e.preventDefault();
      if (e.dataTransfer.files.length) {
        inputElement.files = e.dataTransfer.files;
        updateThumbnail(dropZoneElement, e.dataTransfer.files[0]);
        for (let i = 0; i < e.dataTransfer.files.length; i++) {
          toBase64(e.dataTransfer.files[i], dropZoneElement.id);
        }
      }
      dropZoneElement.classList.remove("drop-zone--over");
    });
  });

  /**
 * Updates the thumbnail on a drop zone element.
 *
 * @param {HTMLElement} dropZoneElement
 * @param {File} file
 */
  function updateThumbnail(dropZoneElement, file) {
    let thumbnailElement = dropZoneElement.querySelector(".drop-zone__thumb");

    if (dropZoneElement.querySelector(".drop-zone__prompt")) {
      dropZoneElement.querySelector(".drop-zone__prompt").remove();
    }

    // First time - si le thumbnail element n'exisite pas on le crée
    if (!thumbnailElement) {
      thumbnailElement = document.createElement("div");
      thumbnailElement.classList.add("drop-zone__thumb");
      dropZoneElement.appendChild(thumbnailElement);
    }

    thumbnailElement.dataset.label = file.name;

    if (file.type.startsWith("image/")) {
      const reader = new FileReader();

      reader.readAsDataURL(file);
      reader.onload = () => {
        thumbnailElement.style.backgroundImage = `url('${reader.result}')`;
      };
    } else {
      thumbnailElement.style.backgroundImage = null;
    }
  }
  /**
 * Convert to base64.
 *
 * @param {File} file
 */
  function toBase64(file, id) {
    if (file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onloadend = () => {
        picList.push({
          'idFurniture': id,
          'pic': reader.result,
        });
      };
      reader.readAsDataURL(file)
    }
  }
}

async function createFurniture(element, furnitureCreated, i) {
  let idFurniture = await callAPI(API_FURNITURE_URL + "add", "POST", token, element);
  furnitureCreated.push({
    'id': i,
    'idFurniture': idFurniture,
  });
}

export default RegisterVisitPage;