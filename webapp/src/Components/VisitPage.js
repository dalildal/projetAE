import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { getUserData } from "./User";
import { RedirectUrl } from "./Router.js";
import { Modal } from "bootstrap";
import ToastWidget from "./Widgets/ToastWidget.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";

const API_VISITE_URL = "/api/visits/";
const API_FURNITURE_URL = "/api/furniture/";
const API_PICTURE_URL = "/api/pictures/";

let historyId;
if (history.state != null) {
    historyId = history.state.data;
}
let userData = getUserData();
let token;

let page = document.querySelector("#page");

const VisitPage = (id) => {
    const user = getUserSessionData();
    const userBis = getUserLocalData();

    if (!user && !userBis) {
        RedirectUrl("/login");
        PrintError(new Error("Vous devez être connecté"));
    } else {

        if (userData.type !== "admin") {
            PrintError("Vous devez être Admin");
            Navbar();
            RedirectUrl("/");
        }

        if (id != null) {
            historyId = id;
        }
        if (userBis) {
            token = userBis.token;
        } else if (user) {
            token = user.token;
        }

        onVisitPage();
    }
};

const onVisitPage = async () => {
    page.innerHTML = `
    <h3>${historyId}</h3>
    <div id="divTableFurniture"></div>
    <div class="modal fade" id="modalFurnitureDemande" tabindex="-1" aria-labelledby="modalFurnitureDemandeLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalFurnitureDemandeLabel">New message</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div id="divModalFurnitureDemande">
                </div>
            </div>
        </div>
    </div>`

    await createTableFurniture();

    var modalFurnitureDemande = document.getElementById('modalFurnitureDemande');
    var myModalFurnitureDemande = new Modal(modalFurnitureDemande);
    modalFurnitureDemande.addEventListener("show.bs.modal", (e) => {
        let idButton = e.relatedTarget.id;
        let divModalFurnitureDemande = document.getElementById('divModalFurnitureDemande');
        let modalTitleFurnitureDemande = modalFurnitureDemande.querySelector('.modal-title');
        var button = e.relatedTarget;
        var id = button.parentElement.parentElement.dataset.id;

        modalTitleFurnitureDemande.textContent = "Achat du meuble N°" + id;
        divModalFurnitureDemande.innerHTML = `
            <form id="formFurnitureAcheter">
                <div class="modal-body">
                        <div class="mb-3">
                            <label for="furnitureDate" class="col-form-label">Date de récupération</label>
                            <input type="date" class="form-control" id="furnitureDate" required>
                            <label for="furniturePrice" class="col-form-label">Prix d'achat</label>
                            <input type="number" class="form-control" id="furniturePrice" required>
                            <label for="furnitureDescription" class="col-form-label">Description du meuble</label>
                            <input type="text" class="form-control" id="furnitureDescription">
                        </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`;

        var formFurnitureAcheter = modalFurnitureDemande.querySelector('#formFurnitureAcheter');
        formFurnitureAcheter.addEventListener("submit", async (e) => {
            e.preventDefault();

            let fav = false;
            const linePicture = document.querySelectorAll(".furnLine");
            linePicture.forEach((line) => {
                if (line.querySelector('.furnBtn').id === idButton) {
                    const likeBtns = line.querySelectorAll(".like");
                    likeBtns.forEach((likeBtn) => {
                        let attribut = likeBtn.dataset.att;
                        if (attribut === "solid") {
                            fav = true;
                        }
                    })
                }
            })

            if (fav) {
                let furnitureDate = modalFurnitureDemande.querySelector('#furnitureDate');
                let furniturePrice = modalFurnitureDemande.querySelector('#furniturePrice');
                let furnitureDescription = modalFurnitureDemande.querySelector('#furnitureDescription');

                let description = "Pas de description";
                if (furnitureDescription.value != "") {
                    description = furnitureDescription.value;
                }

                let data = {
                    etat: "achete",
                    prix: null,
                    prixAntiquaire: null,
                    date: null,
                    dateRecuperation: furnitureDate.value,
                    prixAchat: furniturePrice.value,
                    description: description,
                    type: null,
                    idPicture: -1
                };

                try {
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                } catch (err) {
                    console.error("VisitPage::onVisitPage", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModalFurnitureDemande.hide();
                await createTableFurniture();
                ToastWidget("success", "Le meuble N° " + id + " a bien été acheté");
            } else {
                ToastWidget("fail", "Il est nécessaire d'avoir une photo favorite !");
            }
        })
    })

};

const createTableFurniture = async () => {
    let divTableFurniture = document.getElementById("divTableFurniture");
    let listFurniture = null;
    try {
        let visit = await callAPI(API_VISITE_URL + historyId, "GET", token);
        let url = API_FURNITURE_URL + "allFurnitureById?etat=demande&"
        for (let i = 0; i < visit.length; i++) {
            if (i == visit.length - 1) {
                url += "idList=" + visit[i];
            } else {
                url += "idList=" + visit[i] + "&";
            }
        }
        listFurniture = await callAPI(url, "GET", token);
    } catch (err) {
        console.error("VisitPage::createTableFurniture", err);
        divTableFurniture.innerText = "";
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }

    if (listFurniture == null) return;
    let table = `
    <div id="tableFurniture" class="table-responsive mt-3">
    <table class="table">
        <thead>
            <tr>
                <th>ID meuble</th>
                <th>type</th>
                <th>photos</th>
                <th></th>
            </tr>
        </thead>
        <tbody>`;

    const tmp = []
    listFurniture.forEach((element, i) => {
        tmp.push(createTableRow(element, i));
    });

    Promise.all(tmp).then((e) => {
        e.forEach((row) => {
            table += row;
        })
        table += `</tbody>
                </table>
                </div>`;
        divTableFurniture.innerHTML = table;
        const inadequatBtns = document.querySelectorAll(".inadequat");
        inadequatBtns.forEach((inadequatBtn) => {
            inadequatBtn.addEventListener("click", async (e) => {
                e.preventDefault();
                let data = {
                    etat: "inadequat",
                    prix: null,
                    prixAntiquaire: null,
                    date: null,
                    dateRecuperation: null,
                    prixAchat: null,
                    description: null,
                    type: null,
                    idPicture: -1
                };
                try {
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + e.target.parentElement.parentElement.dataset.id, "PUT", token, data);
                } catch (err) {
                    console.error("VisitPage::onVisitPage", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                await createTableFurniture();
                ToastWidget("success", "Le meuble N° " + e.target.parentElement.parentElement.dataset.id + " est desormait inadequat");
            });
        });

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

                    await createTableFurniture();

                    if (attribut === "solid") {
                        ToastWidget("success", "La photo N° " + id + " n'est desormait plus visible");
                    } else if (attribut === "nSolid") {
                        ToastWidget("success", "La photo N° " + id + " est desormait visible");
                    }
                } else {
                    ToastWidget("fail", "La photo N° " + id + " ne peut pas être la préféré et ne pas etre visible !");
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
                        ToastWidget("fail", "Il est nécessaire d'avoir une photo favorite !")

                    } else if (attribut === "nSolid") {
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

                await createTableFurniture();
                if (attribut === "nSolid") {
                    ToastWidget("success", "La photo N° " + id + " est desormait la photo préféré du meuble N° " + idFurniture);
                }
            });
        });
    })
};

const createTableRow = async (element, i) => {
    try {
        let tabType = await callAPI(API_FURNITURE_URL + "type/" + element.idType, "GET", token);
        let tabPicture = await callAPI(API_PICTURE_URL + element.idFurniture, "GET", token);
        let type = tabType[0];
        let table = `<tr class="furnLine" data-id="${element.idFurniture}">
              <td>${element.idFurniture}</td>
              <td>${type}</td>
              <td id="tdPicture">`

        tabPicture.forEach((pic) => {
            if (element.favoritePicture == pic.idPicture) {
                table += `<div class="divPicture">
                                <a href="${pic.link}" data-lightbox="roadtrip" class="aPictureTable">
                                    <img class="pictureTable" src="${pic.link}"/>
                                </a>
                                <div data-id="${pic.idPicture}" data-att="solid" data-idfurniture="${element.idFurniture}" class="like iconPicture">
                                    <i class="fas fa-heart">
                                    </i>
                                </div>
                                <div data-id="${pic.idPicture}" data-att="solid" data-like="true" class="visibility iconPicture">
                                    <i class="fas fa-eye">
                                    </i>
                                </div>
                              </div>`
            } else if (pic.visibility == 1) {
                table += `<div class="divPicture">
                                <a href="${pic.link}" data-lightbox="roadtrip" class="aPictureTable">
                                    <img class="pictureTable" src="${pic.link}"/>
                                </a>
                                <div data-id="${pic.idPicture}" data-att="nSolid" data-idfurniture="${element.idFurniture}" class="like iconPicture">
                                    <i class="far fa-heart">
                                    </i>
                                </div>
                                <div data-id="${pic.idPicture}" data-att="solid" data-like="false" class="visibility iconPicture">
                                    <i class="fas fa-eye">
                                    </i>
                                </div>
                              </div>`

            } else {
                table += `<div class="divPicture">
                                <a href="${pic.link}" data-lightbox="roadtrip" class="aPictureTable">
                                    <img class="pictureTable" src="${pic.link}"/>
                                </a>
                                <div data-id="${pic.idPicture}" data-att="nSolid" data-idfurniture="${element.idFurniture}" class="like iconPicture">
                                    <i class="far fa-heart">
                                    </i>
                                </div>
                                <div data-id="${pic.idPicture}" data-att="nSolid" data-like="false" class="visibility iconPicture">
                                    <i class="far fa-eye">
                                    </i>
                                </div>
                               </div>`
            }
        })

        table += `
              </td>
              <td><button class="btn btn-primary btn-sm furnBtn" id="${i}" data-bs-toggle="modal" data-bs-target="#modalFurnitureDemande">Acheter</button><button class="btn btn-danger btn-sm inadequat">Inadequat</button></td>
              </tr>`;
        return table;
    } catch (err) {
        console.error("VisitPage::createTableFurniture", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }
}

export default VisitPage;