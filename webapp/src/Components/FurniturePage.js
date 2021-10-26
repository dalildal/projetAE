import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { getUserData } from "./User";
import { RedirectUrl } from "./Router.js";
import { Modal } from "bootstrap";
import { Carousel } from "bootstrap";
import ToastWidget from "./Widgets/ToastWidget.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import TypeFurnitureWidget from "./Widgets/TypeFurnitureWidget.js";

const API_FURNITURE_URL = "/api/furniture/";
const API_OPTION_URL = "/api/options/";
const API_PICTURE_URL = "/api/pictures/";
const API_USER_URL = "/api/users/";

let furniturePage = `
    <div class="col col-md-12 mb-5">
        <div class="row">
            <div class="col-md-1"></div>
            <div class="col-md-10">
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th class="text-center" colspan="12"><h4 id="meubleID"></h4><span class="badge rounded-pill bg-secondary" id="furnitureState"></span></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="6">
                                <table class="table table-bordered mb-0">
                                    <tbody>
                                        <tr>
                                            <td colspan="4">Prix du meuble</td>
                                            <td id="furniturePrice" colspan="8"></td>
                                        </tr>
                                        <tr>
                                            <td colspan="4">Type de meuble</td>
                                            <td colspan="8"><div class="badge badge-default badge-outlined" id="furnitureType"></div></td>
                                        </tr>
                                        <tr>
                                            <td colspan="4">Description</td>
                                            <td id="furnitureDescription" colspan="8"></td>
                                        </tr>
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <td id="optionBtn" colspan="12"></td>
                                        </tr>
                                        <tr>
                                            <td id="options" colspan="12"></td>
                                        </tr>
                                    </tfoot>
                                </table>
                            </td>
                            <td id="carouselImg" colspan="6"></td>
                        </tr>
                    </tbody>
                </table>
                <div id="adminPanel"></div>
                <div class="modal fade" id="modalOption" tabindex="-1" aria-labelledby="modalOptionLabel" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="modalOptionLabel">New Message</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div id="divModal">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-1"></div>
        </div>
    </div>
`;

let historyFurnitureId;
if (history.state != null) {
    historyFurnitureId = history.state.data;
}
let userData = getUserData();
let token;
let picList = [];
let page = document.querySelector("#page");

const FurniturePage = async (id) => {
    const user = getUserSessionData();
    const userBis = getUserLocalData();
    page.innerHTML = furniturePage;

    if (!user && !userBis) {
        RedirectUrl("/login");
        PrintError(new Error("Vous devez être connecté"));
    } else {

        if (id != null) {
            historyFurnitureId = id;
        }
        if (userBis) {
            token = userBis.token;
        } else if (user) {
            token = user.token;
        }
        try {
            const furniture = await callAPI(API_FURNITURE_URL + historyFurnitureId, "GET", token);
            onFurniturePage(furniture);
        } catch (err) {
            console.error("FurniturePage::furniturePage", err);
            PrintError(err);
            if (err.message == "Malformed token") RedirectUrl("/logout");
        }
    }
};

const onFurniturePage = async (furniture) => {

    let idFurniture = document.getElementById("meubleID");
    let salesPrice = document.getElementById("furniturePrice");
    let idType = document.getElementById("furnitureType");
    let description = document.getElementById("furnitureDescription");
    let furnitureState = document.getElementById("furnitureState");

    let type;
    try {
        type = await callAPI(API_FURNITURE_URL + "type/" + furniture.idType, "GET", token);
    } catch (err) {
        console.error("FurniturePage::onfurniturePage", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }

    idFurniture.innerHTML = "<h4>Meuble n°" + furniture.idFurniture + "</h4>";
    salesPrice.innerHTML = furniture.salesPrice + " €";
    idType.innerHTML = type;
    description.innerHTML = furniture.description;
    furnitureState.innerHTML = furniture.state;

    createCaroussel(furniture);

    let optionBtn = document.getElementById("optionBtn");
    if (furniture.state !== "option" && furniture.state !== "vendu" && furniture.state !== "emporte" && furniture.state !== "livre" && furniture.state !== "reserve") {

        optionBtn.innerHTML = `
            <p>Vous pouvez prendre une option sur ce meuble (max. 5j) :</p>
            <form id="myForm">
                <div class="form-floating mb-2">
                    <input type="number" class="form-control" id="nbrJOptions" min="1" max="5"> 
                    <label for="nbrJOptions">Durée souhaithée</label>
                </div>
                <input id="prendreOption" type="submit" value="Prendre une option" class="btn btn-primary">
            </form>
        `;

        let myForm = document.getElementById("myForm");
        myForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            let nbrJOptions = document.getElementById("nbrJOptions");

            let nextStep = true;
            nbrJOptions.addEventListener("input", () => {
                if(nbrJOptions.value == "") {
                nbrJOptions.classList.add('is-invalid');
                nextStep = false;
                } else {
                nbrJOptions.classList.remove('is-invalid');
                nbrJOptions.classList.add('is-valid');
                }
            });
            if(nbrJOptions.value == "") nextStep = false;

            if(nextStep) {
                let data = {
                    idFurniture: historyFurnitureId,
                    idUser: userData.id_utilisateur,
                    time: nbrJOptions.value,
                }
                try {
                    const option = await callAPI(API_OPTION_URL + furniture.state, "POST", token, data);
                    FurniturePage();
                } catch (err) {
                    console.error("FurniturePage::onfurniturePage", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
            } else {
                ToastWidget("fail", "Veuillez indiquer une durée pour l'option.");
            }
        })
    }

    let optionValid = null;
    if (userData.type === "admin") {

        let table = `
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th class="text-center" colspan="12"><h4>Panel Administrateur</h4></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td id="descAdmin" colspan="6"></td>
                        <td id="btnAdmin" colspan="6"></td>
                    </tr>
                    <tr>
                        <td id="userSale" colspan="12"></td>
                    </tr>
                    <tr>
                        <td id="userBuy" colspan="12"></td>
                    </tr>
                </tbody>
            </table>
        `;
        
        let adminPanel = document.getElementById("adminPanel");
        adminPanel.innerHTML = table;

        let descAdmin = document.getElementById("descAdmin");
        descAdmin.innerHTML = `
            <h5>Description du meuble</h5>
            <form id="formModifiedDescription">
                <textarea id="description" name="description">${furniture.description}</textarea>
                <input id="saveDescription" type="submit" value="Sauvegarder" class="btn btn-primary">
            </form>
        `;

        let btnAdmin = document.getElementById("btnAdmin");
        btnAdmin.innerHTML = `
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalOption" data-bs-whatever="Type">Modifier type</button>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalOption" data-bs-whatever="Price">Modifier prix</button>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalOption" data-bs-whatever="Picture">Modifier photos</button>
        `;

        if (furniture.state === "option") {

            try {
                optionValid = await callAPI(API_OPTION_URL + "furniture/" + historyFurnitureId, "GET", token);
            } catch (err) {
                console.error("FurniturePage::onFurniturePage", err);
                PrintError(err);
                if (err.message == "Malformed token") RedirectUrl("/logout");
            }

            var date = new Date(1617784321025).toLocaleDateString("fr-FR");
            var time = new Date(1617784321025).toLocaleTimeString("fr-FR");

            let options = document.getElementById("options");
            options.innerHTML = `
                <div>
                    <p>Option présente :</p>
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th width="10%">ID</th>
                                <th width="40%">Date</th>
                                <th width="20%">Durée</th>
                                <th width="30%">-</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>${optionValid.idUser}</td>
                                <td>${date + " " + time}</td>
                                <td>${optionValid.time} jour(s)</td>
                                <td><button class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#modalOption" data-bs-whatever="Delete">Supprimer</button></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            `;

        }

        let modalOption = document.getElementById("modalOption");
        var myModal = new Modal(modalOption);
        modalOption.addEventListener('hide.bs.modal', function (event) {
            FurniturePage(furniture.idFurniture);
        });

        modalOption.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var recipient = button.getAttribute('data-bs-whatever');
            var divModal = modalOption.querySelector("#divModal");
            var modalTitle = modalOption.querySelector('.modal-title');
            if (recipient == "Delete") {
                modalTitle.textContent = "Supprimer";
                divModal.innerHTML =
                    `<form id="formSupOption">
                    <div class="modal-body">
                        <div class="mb-3">
                            <p>Êtes-vous sûr de vouloir supprimer cette option ?</p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button> 
                        <input id="supprimerOption" type="submit" value="Supprimer" class="btn btn-danger">
                    </div>
                </form>`

                var formSupOption = modalOption.querySelector('#formSupOption');
                formSupOption.addEventListener("submit", async (e) => {
                    e.preventDefault();

                    let data = {
                        idFurniture: historyFurnitureId,
                        idUser: optionValid.idUser
                    }
                    try {
                        const optionUpdate = await callAPI(API_OPTION_URL, "PUT", token, data);
                    } catch (err) {
                        console.error("FurniturePage::onFurniturePage", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }
                    myModal.hide();
                })
            } else if (recipient == "Type") {
                modalTitle.textContent = "Changer le type";
                divModal.innerHTML = `
                    <form id="formTypeFurniture">
                        <div class="modal-body">
                            <div class="mb-3">
                                <div id="divSelect"></div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button> 
                            <input id="modifierType" type="submit" value="Envoyer" class="btn btn-primary">
                        </div>
                    </form>
                `;

                //createSelect(furniture.idType);
                TypeFurnitureWidget("");

                var formTypeFurniture = modalOption.querySelector('#formTypeFurniture');
                formTypeFurniture.addEventListener("submit", async (e) => {
                    e.preventDefault();

                    let select = document.getElementById("type_meuble");

                    let data = {
                        etat: null,
                        prix: null,
                        prixAntiquaire: null,
                        date: null,
                        dateRecuperation: null,
                        prixAchat: null,
                        description: null,
                        type: select.value,
                        idPicture: -1
                    }
                    try {
                        const typeUpdate = await callAPI(API_FURNITURE_URL + historyFurnitureId, "PUT", token, data);
                    } catch (err) {
                        console.error("FurniturePage::onFurniturePage", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }

                    myModal.hide();
                })
            } else if (recipient == "Price") {
                modalTitle.textContent = "Changer le Prix";
                divModal.innerHTML =
                    `<form id="formPriceFurniture">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="priceFurniture" class="col-form-label">Prix du meuble</label>
                            <input type="number" class="form-control" id="priceFurniture" value="${furniture.salesPrice}" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button> 
                        <input id="modifierPrix" type="submit" value="Envoyer" class="btn btn-primary">
                    </div>
                </form>`

                var formPriceFurniture = modalOption.querySelector('#formPriceFurniture');
                formPriceFurniture.addEventListener("submit", async (e) => {
                    e.preventDefault();

                    let priceFurniture = document.getElementById("priceFurniture");

                    let data = {
                        etat: null,
                        prix: priceFurniture.value,
                        prixAntiquaire: null,
                        date: null,
                        dateRecuperation: null,
                        prixAchat: null,
                        description: null,
                        type: null,
                        idPicture: -1
                    }
                    try {
                        const priceUpdate = await callAPI(API_FURNITURE_URL + historyFurnitureId, "PUT", token, data);
                        ToastWidget("success", "Le prix a correctement été modifié");
                    } catch (err) {
                        console.error("FurniturePage::onFurniturePage", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }

                    myModal.hide();
                })
            } else if (recipient == "Picture") {
                modalTitle.textContent = "Modifier les photos";
                divModal.innerHTML =
                    `<form id="formPictureFurniture">
                    <div class="modal-body">
                        <div class="mb-3">
                            <h6>Liste des photos</h6>
                            <div id="pictureList"></div>
                            <h6>Ajouter photos</h6>
                            <div class="drop-zone2">
                                <span class="drop-zone__prompt">Glissez un fichier ou cliquer pour upload</span>
                                <input type="file" id="image" name="image" class="drop-zone__input" multiple>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button> 
                        <input id="ajouterPhoto" type="submit" value="Envoyer" class="btn btn-primary">
                    </div>
                </form>`

                createListPicture(furniture);
                dragAndDrop();
                var formPictureFurniture = modalOption.querySelector('#formPictureFurniture');
                formPictureFurniture.addEventListener("submit", async (e) => {
                    e.preventDefault();

                    picList.forEach(async (pic) => {
                        let picture = {
                            idFurniture: historyFurnitureId,
                            link: pic,
                        }
                        const pictureCreated = await callAPI(API_PICTURE_URL + "add", "POST", token, picture);
                    });

                    picList = [];

                    myModal.hide();
                })
            }
        });

        let formModifiedDescription = document.getElementById("formModifiedDescription");
        formModifiedDescription.addEventListener("submit", async (e) => {
            e.preventDefault();

            let modifiedDescription = document.getElementById("description");
            let data = {
                etat: null,
                prix: null,
                prixAntiquaire: null,
                date: null,
                dateRecuperation: null,
                prixAchat: null,
                description: modifiedDescription.value,
                type: null,
                idPicture: -1
            }
            try {
                const descriptionUpdate = await callAPI(API_FURNITURE_URL + historyFurnitureId, "PUT", token, data);
            } catch (err) {
                console.error("FurniturePage::onFurniturePage", err);
                PrintError(err);
                if (err.message == "Malformed token") RedirectUrl("/logout");
            }
            FurniturePage(furniture.idFurniture);

        });

        const userBuyer = await callAPI(API_USER_URL + "userBuyer/" + historyFurnitureId, "GET", token);
        const userSeller = await callAPI(API_USER_URL + "userSeller/" + historyFurnitureId, "GET", token);

        let buyer = document.getElementById("userSale");
        table = `
            <div id="tableBuyer" class="table-responsive mt-3">
            <h4>L'acheteur :</h4>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Id client</th>
                        <th>Pseudo</th>
                        <th>Nom</th>
                        <th>Prenom</th>
                        <th>Email</th>  
                    </tr>
                </thead>
                <tbody>`;
        if (userBuyer.id != 0) {
            table += `<tr>
                                <td>${userBuyer.id}</td>
                                <td>${userBuyer.pseudo}</td>
                                <td>${userBuyer.lastName}</td>
                                <td>${userBuyer.firstName}</td>
                                <td>${userBuyer.email}</td>
                            `
            table += `</tr>
                            `;
            table += `  </tbody>
                                        </table>
                                        </div>`;
            buyer.innerHTML = table;
        } else {
            let msg = `
                <h4>L'acheteur :</h4>
                <p>Le meuble n'a pas encore été acheté.</p>
            `;
            buyer.innerHTML = msg;

        }

        let seller = document.getElementById("userBuy");
        let tableSeller = `
            <div id="tableBuyer" class="table-responsive mt-3">
            <h4>Le vendeur :</h4>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Id client</th>
                        <th>Pseudo</th>
                        <th>Nom</th>
                        <th>Prenom</th>
                        <th>Email</th>  
                    </tr>
                </thead>
                <tbody>`;
        if (userSeller.id != 0) {
            tableSeller += `<tr>
                                <td>${userSeller.id}</td>
                                <td>${userSeller.pseudo}</td>
                                <td>${userSeller.lastName}</td>
                                <td>${userSeller.firstName}</td>
                                <td>${userSeller.email}</td>
                            `
            tableSeller += `</tr>
                            `;
            tableSeller += `  </tbody>
                                        </table>
                                        </div>`;
            seller.innerHTML = tableSeller;
        } else {
            let msg = `
                <h4>Le vendeur :</h4>
                <p>Aucune information sur le client</p>
            `;
            seller.innerHTML = msg;

        }

    } else {
        let divModifiedDescription = document.getElementById("modified_description");
        divModifiedDescription.innerHTML = `${furniture.description}`;
    }


};

const createCaroussel = async (furniture) => {

    let divCaroussel = document.getElementById("carouselImg");
    divCaroussel.innerHTML = `<div id="carouselExampleIndicators" class="carousel slide" data-bs-ride="carousel">
    <div class="carousel-indicators" id="carouselIndicators">
    </div>
    <div class="carousel-inner" id="carouselInner">
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide="prev">
      <span class="carousel-control-prev-icon" aria-hidden="true"></span>
      <span class="visually-hidden">Previous</span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide="next">
      <span class="carousel-control-next-icon" aria-hidden="true"></span>
      <span class="visually-hidden">Next</span>
    </button>
  </div>`;
    let tabImg;
    try {
        tabImg = await callAPI(API_PICTURE_URL + "visible/" + historyFurnitureId, "GET", token);
    } catch (err) {
        console.error("FurniturePage::createCaroussel", err);
        PrintError(err);
    }
    let divCarouselInner = document.getElementById("carouselInner");
    let divCarouselIndicators = document.getElementById("carouselIndicators");
    let cpt = 0;
    tabImg.forEach((img) => {
        if (furniture.favoritePicture == img.idPicture) {
            divCarouselIndicators.innerHTML += `
            <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="${cpt}" class="active" aria-current="true" aria-label="Slide ${cpt + 1}"></button>`
            divCarouselInner.innerHTML += `
            <div class="carousel-item active">
                <img src="${img.link}" class="d-block w-100 imgFurniture">
            </div>`;
        } else {
            divCarouselIndicators.innerHTML += `
            <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="${cpt}" aria-label="Slide ${cpt + 1}"></button>`
            divCarouselInner.innerHTML += `
            <div class="carousel-item">
                <img src="${img.link}" class="d-block w-100">
            </div>`;
        }

        cpt++;
    })

    new Carousel(divCaroussel, {
        interval: 15000,
        wrap: false
    });

}

const createSelect = async (idType) => {
    let data = null;
    let tabType = null;
    let type = null;
    try {
        tabType = await callAPI(API_FURNITURE_URL + "type/" + idType, "GET", token);
        data = await callAPI(API_FURNITURE_URL + "type", "GET");
        type = tabType[0];
    } catch (err) {
        console.error("Furniture::createSelect", err);
        PrintError(err);
    }

    if (!data) return;
    let select =
        `<select id="type_meuble" class="form-select" aria-label="Select Type">`;
    data.forEach((element) => {
        if (element === type) {
            select += `<option value="${element}" selected> ${element} </option>`
        } else {
            select += `<option value="${element}"> ${element} </option>`
        }
    });
    select += `</select>`
    let mySelectDiv = document.getElementById("divSelect");
    mySelectDiv.innerHTML = select;
};

const createListPicture = async (furniture) => {
    let tabPicture = null;
    try {
        tabPicture = await callAPI(API_PICTURE_URL + historyFurnitureId, "GET", token);
    } catch (err) {
        console.error("FurniturePage::createListPicture", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }

    if (!tabPicture) return;
    let div = "";
    tabPicture.forEach((pic) => {
        if (furniture.favoritePicture == pic.idPicture) {
            div += `<div class="divPicture">
                        <a href="${pic.link}" data-lightbox="roadtrip" class="aPictureTable">
                            <img class="pictureTable" src="${pic.link}"/>
                        </a>
                        <div data-id="${pic.idPicture}" data-att="solid" data-idfurniture="${historyFurnitureId}" class="like iconPicture">
                            <i class="fas fa-heart">
                            </i>
                        </div>
                        <div data-id="${pic.idPicture}" data-att="solid" data-like="true" class="visibility iconPicture">
                            <i class="fas fa-eye">
                            </i>
                        </div>
                    </div>`

        } else if (pic.visibility == 1) {
            div += `<div class="divPicture">
                        <a href="${pic.link}" data-lightbox="roadtrip" class="aPictureTable">
                            <img class="pictureTable" src="${pic.link}"/>
                        </a>
                        <div data-id="${pic.idPicture}" data-att="nSolid" data-idfurniture="${historyFurnitureId}" class="like iconPicture">
                            <i class="far fa-heart">
                            </i>
                        </div>
                        <div data-id="${pic.idPicture}" data-att="solid" data-like="false" class="visibility iconPicture">
                            <i class="fas fa-eye">
                            </i>
                        </div>
                    </div>`

        } else {
            div += `<div class="divPicture">
                        <a href="${pic.link}" data-lightbox="roadtrip" class="aPictureTable">
                            <img class="pictureTable" src="${pic.link}"/>
                        </a>
                        <div data-id="${pic.idPicture}" data-att="nSolid" data-idfurniture="${historyFurnitureId}" class="like iconPicture">
                            <i class="far fa-heart">
                            </i>
                        </div>
                        <div data-id="${pic.idPicture}" data-att="nSolid" data-like="false" class="visibility iconPicture">
                            <i class="far fa-eye">
                            </i>
                        </div>
                    </div>`
        }
    });

    let pictureList = document.getElementById("pictureList");
    pictureList.innerHTML = div;

    const visibilityBtns = document.querySelectorAll(".visibility");
    visibilityBtns.forEach((visibilityBtn) => {
        visibilityBtn.addEventListener("click", async (e) => {
            let id = visibilityBtn.dataset.id;
            let like = visibilityBtn.dataset.like;

            if (like === "false") {

                let visibility;
                let attribut = visibilityBtn.dataset.att;

                if (attribut === "solid") {
                    visibility = {
                        visibility: 0
                    }
                } else if (attribut === "nSolid") {
                    visibility = {
                        visibility: 1
                    }
                }

                try {
                    const pictureUpdate = await callAPI(API_PICTURE_URL + "/" + id, "PUT", token, visibility);
                } catch (err) {
                    console.error("VisitPage::onVisitPage", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }

                await createListPicture(furniture);

                if (attribut === "solid") {
                    ToastWidget("success", "La photo N° " + id + " n'est desormait plus visible");
                } else if (attribut === "nSolid") {
                    ToastWidget("success", "La photo N° " + id + " est desormait visible");
                }
            } else {
                ToastWidget("success", "La photo N° " + id + " ne peut pas être la préféré et ne pas etre visible !");
            }
        });
    });

    const likeBtns = document.querySelectorAll(".like");
    likeBtns.forEach((likeBtn) => {
        likeBtn.addEventListener("click", async (e) => {
            let id = likeBtn.dataset.id;
            let idFurniture = likeBtn.dataset.idfurniture;
            let attribut = likeBtn.dataset.att;

            let data;
            try {
                if (attribut === "solid") {
                    ToastWidget("fail", "Il est nécessaire d'avoir une photo favorite !");
                } else if (attribut === "nSolid") {
                    furniture.favoritePicture = id;
                    let visibility = {
                        visibility: 1
                    }
                    const pictureUpdate = await callAPI(API_PICTURE_URL + "/" + id, "PUT", token, visibility);
                    data = {
                        etat: null,
                        prix: null,
                        prixAntiquaire: null,
                        date: null,
                        dateRecuperation: null,
                        prixAchat: null,
                        description: null,
                        type: null,
                        idPicture: id
                    }
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + idFurniture, "PUT", token, data);
                }
            } catch (err) {
                console.error("VisitPage::onVisitPage", err);
                PrintError(err);
                if (err.message == "Malformed token") RedirectUrl("/logout");
            }

            await createListPicture(furniture);

            if (attribut === "nSolid") {
                ToastWidget("success", "La photo N° " + id + " est desormait la photo préféré du meuble N° " + idFurniture);
            }
        });
    });
};

const dragAndDrop = () => {
    document.querySelectorAll(".drop-zone__input").forEach(inputElement => {
        const dropZoneElement = inputElement.closest(".drop-zone2");

        dropZoneElement.addEventListener("click", e => {
            inputElement.click();
        });

        inputElement.addEventListener("change", e => {
            if (inputElement.files.length) {
                updateThumbnail(dropZoneElement, inputElement.files[0]);
            }
            for (let i = 0; i < inputElement.files.length; i++) {
                toBase64(inputElement.files[i]);
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
                    toBase64(e.dataTransfer.files[i]);
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
        let thumbnailElement = dropZoneElement.querySelector(".drop-zone__thumb2");

        if (dropZoneElement.querySelector(".drop-zone__prompt")) {
            dropZoneElement.querySelector(".drop-zone__prompt").remove();
        }

        // First time - si le thumbnail element n'exisite pas on le crée
        if (!thumbnailElement) {
            thumbnailElement = document.createElement("div");
            thumbnailElement.classList.add("drop-zone__thumb2");
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
    function toBase64(file) {
        if (file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onloadend = () => {
                picList.push(reader.result);
            };
            reader.readAsDataURL(file)
        }
    }
}


export default FurniturePage;