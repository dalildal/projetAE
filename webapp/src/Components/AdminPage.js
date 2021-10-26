import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { getUserData } from "./User";
import { RedirectUrl } from "./Router.js";
import Navbar from "./Navbar.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import ToastWidget from "./Widgets/ToastWidget.js";
import { Modal } from "bootstrap";
import { Loader } from "@googlemaps/js-api-loader"
const API_USER_URL = "/api/users/";
const API_FURNITURE_URL = "/api/furniture/";
const API_VISITE_URL = "/api/visits/";


let token;

let adminPage = `
    <div class="col col-md-12 mt-2 mb-5">
        <h4 id="pageTitle">Zone Administrateur</h4>
            
        <div class="row">
            <div class="col col-md-6">
                <h5> Recherche d'utilisateurs </h5>
                <div class="input-group">
                    <input id="searchUser" type="search" class="form-control rounded" placeholder="Rechercher..." aria-label="Search" aria-describedby="search-addon" />
                </div>
            </div>

            <div class="col col-md-6">
                <h5> Recherche de meubles </h5>
                <div class="input-group">
                    <input id="searchFurniture" type="search" class="form-control rounded" placeholder="Rechercher..." aria-label="Search" aria-describedby="search-addon" />
                </div>
            </div>
        </div>

        <div id="searchedUsers"></div>
        <div id="searchedFurnitures"></div>

        <div class="row mt-5">
            <div class="col col-md-6">
                <div id="divTableUser"></div>
            </div>
            <div class="col col-md-6">
                <div id="map"></div>
            </div>
        </div>

        <div class="row mt-5">
            <div id="divTableFurniture"></div>
        </div>

        <div class="row mt-5">
            <div class="col col-md-6">
                <div id="divTableVisiteAttente"></div>
            </div>
            <div class="col col-md-6">
                <div id="divTableVisiteConfirmee"></div>
            </div>
        </div>
    </div>
`;

const AdminPage = () => {
    let page = document.querySelector("#page");

    page.innerHTML = adminPage;

    const userData = getUserData();
    if (userData.type == "admin") {
        // re-render the navbar for the authenticated user
        onAdmin();
    } else {
        Navbar();
        RedirectUrl("/");
    }
};


const onAdmin = async () => {
    const user = getUserSessionData();
    const userBis = getUserLocalData();

    if (userBis) {
        token = userBis.token;
    } else if (user) {
        token = user.token;
    }

    await createTableUser();
    await createTableFurniture();
    await createTableVisiteAttente();
    await createTableVisiteConfirmee();

    onAdminBtn();

};

const onAdminBtn = () => {

    //TableUser Fonctions

    const checkBoxsUser = document.querySelectorAll(".checkboxUser");

    const acceptBtn = document.getElementById("accepteBtn");
    acceptBtn.addEventListener("click", (e) => {
        e.preventDefault();
        checkBoxsUser.forEach(async (checkBox) => {

            if (checkBox.checked) {
                const select = document.getElementById("utilisateur" + checkBox.id);
                const typeIndex = select.selectedIndex;
                const type = select.options[typeIndex].value;
                let userUpdate = null;
                try {
                    userUpdate = await callAPI(API_USER_URL + "/" + checkBox.id, "PUT", token);
                    const t = await callAPI(API_USER_URL + "/" + checkBox.id + ":" + type, "PUT", token);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                await createTableUser();
                onAdminBtn();
                ToastWidget("success",userUpdate.pseudo + " a bien été accepté");
            }
        });
    })

    const supprimerBtn = document.getElementById("supprimerBtn");
    supprimerBtn.addEventListener("click", (e) => {
        e.preventDefault();
        checkBoxsUser.forEach(async (checkBox) => {
            if (checkBox.checked) {
                let userDelete = null;
                try {
                    userDelete = await callAPI(API_USER_URL + "/" + checkBox.id, "DELETE", token);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                await createTableUser();
                onAdminBtn();
                ToastWidget("delete", userDelete.pseudo + " a bien été supprimé");
            }
        });
    })



    let searchFurniture = document.getElementById("searchFurniture");
    searchFurniture.addEventListener('input', (e) => {
        if (e.target.value === "") {
            divSearchFurnitures.innerHTML = "";
        }
    })

    searchFurniture.addEventListener('keyup', async (e) => {
        e.preventDefault();
        let furnitures = null;
        try {
            furnitures = await callAPI(API_FURNITURE_URL + "search/" + e.target.value, "GET", token);
            console.log(furnitures);
        } catch (err) {
            console.error("HomePage::searchBtn", err);
            if (err.message == "Malformed token") RedirectUrl("/logout");
        }
        createTableSearchFurniture(furnitures);
    })

    let searchUser = document.getElementById("searchUser");
    searchUser.addEventListener('input', (e) => {
        if (e.target.value === "") {
            divSearchUsers.innerHTML = "";
        }
    })

    searchUser.addEventListener('keyup', async (e) => {
        e.preventDefault();
        let users = null;
        try {
            users = await callAPI(API_USER_URL + "search/" + e.target.value, "GET", token);
        } catch (err) {
            console.error("AdminPage::searchBtn", err);
            if (err.message == "Malformed token") RedirectUrl("/logout");
        }
        createTableSearchUser(users);
    })



    //TableFurniture Fonctions

    var modalFurniture = document.getElementById('modalFurniture');
    var myModal = new Modal(modalFurniture);
    modalFurniture.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var id = button.parentElement.parentElement.dataset.id;
        var description = button.parentElement.parentElement.dataset.description;
        var recipient = button.getAttribute('data-bs-whatever');
        var divModal = modalFurniture.querySelector("#divModal");
        var modalTitle = modalFurniture.querySelector('.modal-title');
        var today = new Date();
        var day = String(today.getDate()).padStart(2, '0');
        var month = String(today.getMonth() + 1).padStart(2, '0');
        var year = today.getFullYear();
        if (recipient == "Vente") {
            modalTitle.textContent = "Inserer le prix du meuble";

            divModal.innerHTML = `
            <form id="formVente">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="priceFurniture" class="col-form-label">Prix du meuble</label>
                        <input type="number" step="0.01" class="form-control" id="priceFurniture" required>
                        <label for="furnitureDescription" class="col-form-label">Description du meuble</label>
                        <input type="text" class="form-control" id="furnitureDescription" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`

            if (description != "Pas de description") {
                let furnitureDescription = modalFurniture.querySelector('#furnitureDescription')
                furnitureDescription.value = description;
            }

            var formVente = modalFurniture.querySelector('#formVente');
            formVente.addEventListener("submit", async (e) => {
                e.preventDefault();
                let prix = modalFurniture.querySelector('#priceFurniture');
                let furnitureDescription = modalFurniture.querySelector('#furnitureDescription');

                let data = {
                    etat: "a vendre",
                    prix: prix.value,
                    prixAntiquaire: null,
                    date: null,
                    dateRecuperation: null,
                    prixAchat: null,
                    description: furnitureDescription.value,
                    type: null,
                    idPicture: -1
                };
                try {
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModal.hide();
                await createTableFurniture();
                onAdminBtn();

            })
        } else if (recipient == "Disponible") {
            modalTitle.textContent = "Inserer la date de depot en magasin du meuble";

            divModal.innerHTML = `
            <form id="formDisponible">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="salesDateFurniture" class="col-form-label">Date de depot</label>
                        <input type="date" max="${year}-${month}-${day}" class="form-control" id="salesDateFurniture" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`

            var formDisponible = modalFurniture.querySelector('#formDisponible');
            formDisponible.addEventListener("submit", async (e) => {
                e.preventDefault();

                let date = modalFurniture.querySelector('#salesDateFurniture');

                let data = {
                    etat: "disponible",
                    date: date.value,
                    prix: null,
                    prixAntiquaire: null,
                    dateRecuperation: null,
                    prixAchat: null,
                    description: null,
                    type: null,
                    idPicture: -1
                };
                try {
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModal.hide();
                await createTableFurniture();
                onAdminBtn();
            })

        } else if (recipient == "Livre") {
            modalTitle.textContent = "Inserer la date de livraison du meuble";

            divModal.innerHTML = `
            <form id="formLivre">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="deliveryDateFurniture" class="col-form-label">Date de livraison</label>
                        <input type="date" max="${year}-${month}-${day}" class="form-control" id="deliveryDateFurniture" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`

            var formLivre = modalFurniture.querySelector('#formLivre');
            formLivre.addEventListener("submit", async (e) => {
                e.preventDefault();

                let date = modalFurniture.querySelector('#deliveryDateFurniture');

                let data = {
                    etat: "livre",
                    date: date.value,
                    prix: null,
                    prixAntiquaire: null,
                    dateRecuperation: null,
                    prixAchat: null,
                    description: null,
                    type: null,
                    idPicture: -1
                };
                try {
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModal.hide();
                await createTableFurniture();
                onAdminBtn();
            })

        } else if (recipient == "Emporte") {
            modalTitle.textContent = "Inserer la date de retrait du meuble";

            divModal.innerHTML = `
            <form id="formEmporte">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="recoveryDateFurniture" class="col-form-label">Date de retrait</label>
                        <input type="date" max="${year}-${month}-${day}" class="form-control" id="recoveryDateFurniture" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`

            var formEmporte = modalFurniture.querySelector('#formEmporte');
            formEmporte.addEventListener("submit", async (e) => {
                e.preventDefault();

                let date = modalFurniture.querySelector('#recoveryDateFurniture');

                let data = {
                    etat: "emporte",
                    date: date.value,
                    prix: null,
                    prixAntiquaire: null,
                    dateRecuperation: null,
                    prixAchat: null,
                    description: null,
                    type: null,
                    idPicture: -1
                };
                try {
                    const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModal.hide();
                await createTableFurniture();
                onAdminBtn();
            })
        } else if (recipient == "Vendu") {
            modalTitle.textContent = "Inserer le prix antiquaire et l'acheteur";

            divModal.innerHTML = `
            <form id="formVendu">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="DealerPriceFurniture" class="col-form-label">Prix antiquaire du meuble (Facultatif)</label>
                        <input type="number" step="0.01" class="form-control" id="DealerPriceFurniture">
                        <label for="EmailBuyerFurniture" class="col-form-label">Email de l'acheteur (Facultatif)</label>
                        <input type="email" class="form-control" id="EmailBuyerFurniture">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`

            var formVendu = modalFurniture.querySelector('#formVendu');
            formVendu.addEventListener("submit", async (e) => {
                e.preventDefault();

                let dealerPrice = modalFurniture.querySelector('#DealerPriceFurniture');
                let emailBuyerFurniture = modalFurniture.querySelector('#EmailBuyerFurniture');

                let dataEmail;
                let user;
                if (emailBuyerFurniture.value != "") {
                    try {
                        user = await callAPI(API_USER_URL + "/" + emailBuyerFurniture.value, "GET", token);
                    } catch (err) {
                        console.error("AdminPage::onAdminBtn", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }
                    dataEmail = {
                        idFurniture: id,
                        idUser: user.id
                    };

                } else {
                    dataEmail = {
                        idFurniture: id,
                        idUser: 0
                    };
                }

                if (dealerPrice.value !== "") {
                    if (user.type === "antiquaire") {

                        let data = {
                            etat: "vendu",
                            date: null,
                            prix: null,
                            prixAntiquaire: dealerPrice.value,
                            dateRecuperation: null,
                            prixAchat: null,
                            description: null,
                            type: null,
                            idPicture: -1
                        };
                        try {
                            const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                            ToastWidget("success", "Le meuble a bien été vendu à l'antiquaire.");
                        } catch (err) {
                            console.error("AdminPage::onAdminBtn", err);
                            PrintError(err);
                            if (err.message == "Malformed token") RedirectUrl("/logout");
                        }

                        try {
                            await callAPI(API_FURNITURE_URL + "addSales", "POST", token, dataEmail);
                        } catch (err) {
                            console.error("AdminPage::onAdminBtn", err);
                            PrintError(err);
                            if (err.message == "Malformed token") RedirectUrl("/logout");
                        }
                    } else {
                        ToastWidget("delete", "Ce client n'est pas un antiquaire !");
                    }
                } else {
                    let data = {
                        etat: "vendu",
                        date: null,
                        prix: null,
                        prixAntiquaire: dealerPrice.value,
                        dateRecuperation: null,
                        prixAchat: null,
                        description: null,
                        type: null,
                        idPicture: -1
                    };
                    try {
                        const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                        ToastWidget("success", "Le meuble a bien été vendu.");
                    } catch (err) {
                        console.error("AdminPage::onAdminBtn", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }

                    try {
                        await callAPI(API_FURNITURE_URL + "addSales", "POST", token, dataEmail);
                    } catch (err) {
                        console.error("AdminPage::onAdminBtn", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }
                }

                myModal.hide();
                await createTableFurniture();
                onAdminBtn();
            })
        } else if (recipient === "VenduAntiqu") {
            modalTitle.textContent = "Inserer le prix antiquaire et l'acheteur";

            divModal.innerHTML = `
            <form id="formVenduAntiqu">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="DealerPriceFurniture" class="col-form-label">Prix antiquaire du meuble</label>
                        <input type="number" step="0.01" class="form-control" id="DealerPriceFurniture" required>
                        <label for="EmailBuyerFurniture" class="col-form-label">Email de l'acheteur</label>
                        <input type="email" class="form-control" id="EmailBuyerFurniture" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                    <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                </div>
            </form>`

            var formVenduAntiqu = modalFurniture.querySelector('#formVenduAntiqu');
            formVenduAntiqu.addEventListener("submit", async (e) => {
                e.preventDefault();

                let dealerPrice = modalFurniture.querySelector('#DealerPriceFurniture');
                let emailBuyerFurniture = modalFurniture.querySelector('#EmailBuyerFurniture');

                let dataEmail;
                let user;

                try {
                    user = await callAPI(API_USER_URL + "/" + emailBuyerFurniture.value, "GET", token);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                dataEmail = {
                    idFurniture: id,
                    idUser: user.id
                };

                if (user.type === "antiquaire") {

                    let data = {
                        etat: "vendu",
                        date: null,
                        prix: null,
                        prixAntiquaire: dealerPrice.value,
                        dateRecuperation: null,
                        prixAchat: null,
                        description: null,
                        type: null,
                        idPicture: -1
                    };
                    try {
                        const furnitureUpdate = await callAPI(API_FURNITURE_URL + "/" + id, "PUT", token, data);
                    } catch (err) {
                        console.error("AdminPage::onAdminBtn", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }

                    try {
                        await callAPI(API_FURNITURE_URL + "addSales", "POST", token, dataEmail);
                    } catch (err) {
                        console.error("AdminPage::onAdminBtn", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }
                } else {
                    ToastWidget("delete", "Ce client n'est pas un antiquaire !");
                }

                myModal.hide();
                await createTableFurniture();
                onAdminBtn();
            })
        }

    })

    const restaurerBtns = document.querySelectorAll(".restaurer");
    restaurerBtns.forEach((restaurerBtn) => {
        restaurerBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            let data = {
                etat: "a restaurer",
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
                console.error("AdminPage::onAdminBtn", err);
                PrintError(err);
                if (err.message == "Malformed token") RedirectUrl("/logout");
            }
            await createTableFurniture();
            onAdminBtn();
        });
    });

    const retireBtns = document.querySelectorAll(".retire");
    retireBtns.forEach((retireBtn) => {
        retireBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            let data = {
                etat: "retire",
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
                console.error("AdminPage::onAdminBtn", err);
                PrintError(err);
                if (err.message == "Malformed token") RedirectUrl("/logout");
            }
            await createTableFurniture();
            onAdminBtn();
        });
    });

    const reserveBtns = document.querySelectorAll(".reserve");
    reserveBtns.forEach((reserveBtn) => {
        reserveBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            let data = {
                etat: "reserve",
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
                console.error("AdminPage::onAdminBtn", err);
                PrintError(err);
                if (err.message == "Malformed token") RedirectUrl("/logout");
            }
            await createTableFurniture();
            onAdminBtn();
        });
    });

    //TableVisitAttente Fonctions

    var modalVisiteAttente = document.getElementById('modalVisiteAttente');
    var myModalVisiteAttente = new Modal(modalVisiteAttente);
    modalVisiteAttente.addEventListener("show.bs.modal", (e) => {
        let divModalVisiteAttente = document.getElementById('divModalVisiteAttente');
        let modalTitleVisiteAttente = modalVisiteAttente.querySelector('.modal-title');
        var button = e.relatedTarget;
        var id = button.parentElement.parentElement.dataset.id;
        var recipient = button.getAttribute('data-bs-whatever');
        if (recipient == "Confirmer") {
            modalTitleVisiteAttente.textContent = "Inserer la date de la visite N° " + id;
            divModalVisiteAttente.innerHTML = `
                <form id="formVisistAccepte">
                    <div class="modal-body">
                            <div class="mb-3">
                                <label for="visitDate" class="col-form-label">Date de la visite</label>
                                <input type="datetime-local" class="form-control" id="visitDate" required>
                            </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                        <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                    </div>
                </form>`

            var formVisistAccepte = modalVisiteAttente.querySelector('#formVisistAccepte');
            formVisistAccepte.addEventListener("submit", async (e) => {
                e.preventDefault();

                let visitDate = modalVisiteAttente.querySelector('#visitDate');

                let data = {
                    date: visitDate.value,
                    raison: null
                };
                try {
                    const visitUpdate = await callAPI(API_VISITE_URL + "/" + id, "PUT", token, data);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModalVisiteAttente.hide();
                await createTableVisiteAttente();
                await createTableVisiteConfirmee()
                onAdminBtn();
                ToastWidget("success", "La visite N° " + id + " a bien été acceptée");
            })

        } else if (recipient == "Annuler") {
            modalTitleVisiteAttente.textContent = "Inserer la raison de l'annulation de la visite N° " + id;
            divModalVisiteAttente.innerHTML = `
                <form id="formVisistAnnule">
                    <div class="modal-body">
                            <div class="mb-3">
                                <label for="visitAnnulationRaison" class="col-form-label">Annulation de la visite</label>
                                <input type="text" class="form-control" id="visitAnnulationRaison" required>
                            </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button> 
                        <input id="vendreBtn" type="submit" value="Envoyer" class="btn btn-primary">
                    </div>
                </form>`

            var formVisistAnnule = modalVisiteAttente.querySelector('#formVisistAnnule');
            formVisistAnnule.addEventListener("submit", async (e) => {
                e.preventDefault();

                let visitAnnulationRaison = modalVisiteAttente.querySelector('#visitAnnulationRaison');

                let data = {
                    date: null,
                    raison: visitAnnulationRaison.value
                };
                try {
                    await callAPI(API_VISITE_URL + "/" + id, "PUT", token, data);
                    await callAPI(API_FURNITURE_URL + "visit/" + id, "PUT", token);
                } catch (err) {
                    console.error("AdminPage::onAdminBtn", err);
                    PrintError(err);
                    if (err.message == "Malformed token") RedirectUrl("/logout");
                }
                myModalVisiteAttente.hide();
                await createTableFurniture();
                await createTableVisiteAttente();
                await createTableVisiteConfirmee()
                onAdminBtn();
                ToastWidget("success", "La visite N° " + id + " a bien été annulée");
            })
        }
    })

    //TableVisitConfirmee Fonctions

    let listTr = document.querySelectorAll(".trVisiteConfirmee");
    listTr.forEach(async (tr) => {
        tr.addEventListener("click", (e) => {
            RedirectUrl("/visit", e.currentTarget.getAttribute('data-id'));
        })
    })


}

let divSearchUsers = document.getElementById("searchedUsers");
const createTableSearchUser = (users) => {
    divSearchUsers = document.getElementById("searchedUsers");
    let table = `<div id="tableSearchedUser" class="table-responsive mt-3">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Pseudo</th>
                                <th>Nom</th>
                                <th>Prenom</th>
                                <th>Ville</th> 
                                <th>Meubles achetés</th>
                                <th>Meubles vendus</th>
                             </tr>
                        </thead>
                    <tbody>`;
    if (users) {
        for (const [key, value] of Object.entries(users)) {
            table += `<tr class="trClient" id="${key}">
                    <td>${value[0]}</td>
                    <td>${value[1]}</td>
                    <td>${value[2]}</td>
                    <td>${value[3]}</td>
                    <td>${value[4]}</td>
                    <td>${value[5]}</td>
                 </tr> `
        }
        table += `</tbody>
                                </table>
                                </div>`;
        divSearchUsers.innerHTML = table;
    } else {
        divSearchUsers.innerHTML = "";
    }

    let trClient = document.querySelectorAll(".trClient");
    trClient.forEach((tr) => {
        tr.addEventListener('click', () => {
            RedirectUrl("/infoClient", tr.id);
        });
    });
}

let divSearchFurnitures = document.getElementById("searchedFurnitures");
const createTableSearchFurniture = (furnitures) => {
    divSearchFurnitures = document.getElementById("searchedFurnitures");
    let table = `
    <div id="tableSearchedFurnitures" class="table-responsive mt-3">
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>ID</th>
                <th>Description</th>
                <th>Etat</th>
                <th>Photo préférée</th>
            </tr>
        </thead>
        <tbody>`;
    if (furnitures) {
        for (const [key, value] of Object.entries(furnitures)) {
            table += `<tr class="trFurniture" id="${key}">
                        <td>${value[0]}</td>
                        <td>${value[1]}</td>
                        <td>${value[2]}</td>
                        <td><img class="pictureTable" src="${value[4]}" </img></td>
                    </tr> `
        }
        table += `  </tbody>
                        </table>
                        </div>`;
        divSearchFurnitures.innerHTML = table;
    } else {
        divSearchFurnitures.innerHTML = "";
    }
    let trFurniture = document.querySelectorAll(".trFurniture");
    trFurniture.forEach((tr) => {
        tr.addEventListener('click', () => {
            RedirectUrl("/furniture", tr.id);
        });
    });
}


const createTableUser = async () => {
    let data = null;
    try {
        data = await callAPI(API_USER_URL + "?etat=0", "GET", token);
    } catch (err) {
        console.error("AdminPage::createTableUser", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }
    if (!data) return;
    let table = `
        <div class="col col-md-12">
            <h5 id="pageTitle">Inscription(s) en attente(s)</h5>
            <div id="tableUser" class="table-responsive mt-3">
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th width="45%">Demande d'inscription</th>
                        <th width="45%">Type de compte</th>
                        <th width="10%">-</th>
                    </tr>
                </thead>
                <tbody>
    `;

    data.forEach((element) => {
        // ici appeler la méthode qui convertis
        table += `<tr data-id="${element.id}">
                  <td>${element.pseudo}</td>
                  <td><select id="utilisateur${element.id}">`
        if (element.type === "admin") {
            table +=
                `<option value="client">Client</option>
                        <option value="antiquaire">Antiquaire</option>
                        <option value="admin" selected>Admin</option>`
        } else if (element.type === "antiquaire") {
            table +=
                `<option value="client">Client</option>
                        <option value="antiquaire" selected>Antiquaire</option>
                        <option value="admin">Admin</option>`
        } else {
            table +=
                `<option value="client" selected>Client</option>
                        <option value="antiquaire">Antiquaire</option>
                        <option value="admin">Admin</option>`
        }
        table +=
            `</select>
                  </td>
                  <td> <input class="checkboxUser" type="checkbox" id="${element.id}"> </td>
                  </tr>
                  `;
    });

    table += `
            </tbody>
        </table>
        </div>
    </div>
    `;

    let divTableUser = document.getElementById("divTableUser");
    divTableUser.innerHTML =
        table +
        '<div class="btn-toolbar">' +
        '<button id="accepteBtn" class="btn btn-primary mx-1">Accepter</button>' +
        '<button id="supprimerBtn" class="btn btn-danger mx-1">Supprimer</button>' +
        '</div>';

    let map;
    let geocoder;
    let address;
    const loader = new Loader({
        apiKey: "AIzaSyBG8fpPMSvRJ6g6cUJ3DXumWIVyTPtqPKI",
        version: "weekly",
    });

    loader.load().then(() => {
        geocoder = new google.maps.Geocoder();
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 50.8466, lng: 4.3528 },
            zoom: 8,
        });
        data.forEach((user) => {
            address = user.street + " " + user.num + " " + user.municipality + " " + user.country;
            geocodeAddress(address, geocoder, map, user.pseudo);
        });
    });

    function geocodeAddress(address, geocoder, resultsMap, user) {
        geocoder.geocode({ address: address }, (results, status) => {
            if (status === "OK") {
                resultsMap.setCenter(results[0].geometry.location);
                new google.maps.Marker({
                    map: resultsMap,
                    position: results[0].geometry.location,
                    label: { color: '#00000', fontWeight: 'bold', fontSize: '14px', text: user }
                });
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
    }

}

const createTableFurniture = async () => {
    let data = null;
    try {
        data = await callAPI(API_FURNITURE_URL, "GET", token);
    } catch (err) {
        console.error("AdminPage::createTableFurniture", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }
    if (!data) return;
    let table = `
    <h5 id="pageTitle">Meuble(s)</h5>
    <div id="tableFurniture" class="table-responsive mt-3">
    <table class="table table-bordered">
        <thead>
            <tr>
                <th width="5%">ID</th>
                <th width="50%">Description</th>
                <th width="15%">Etat</th>
                <th width="30%">Action(s)</th>
            </tr>
        </thead>
        <tbody>`;
    data.forEach((element) => {
        table += `<tr data-id="${element.idFurniture}" data-description="${element.description}">
                  <td>${element.idFurniture}</td>
                  <td>${element.description}</td>
                  <td><span class="badge rounded-pill bg-secondary">${element.state}</span></td>`;

        if (element.state == "achete") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm disponible" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Disponible">Disponible</button> <button  class="btn btn-primary btn-sm restaurer">Restaurer</button> <button class="btn btn-primary btn-sm vendu" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="VenduAntiqu">Vendu</button> </td>`
        } else if (element.state == "a restaurer") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm disponible" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Disponible">Disponible</button> <button class="btn btn-primary btn-sm vendu" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="VenduAntiqu">Vendu</button> </td>`
        } else if (element.state == "disponible") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm vendre" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Vente">A vendre</button> <button class="btn btn-primary btn-sm vendu" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="VenduAntiqu">Vendu</button> </td>`
        } else if (element.state == "a vendre") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm retire">Retirer</button> <button class="btn btn-primary btn-sm vendu" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Vendu">Vendu</button> </td>`
        } else if (element.state == "vendu") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm emporte" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Emporte">Emporté</button> <button class="btn btn-primary btn-sm livre" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Livre">Livré</button> <button class="btn btn-primary btn-sm reserve">Reservé</button> </td>`
        } else if (element.state == "reserve") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm vendre" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Vente">A vendre</button> <button class="btn btn-primary btn-sm emporte" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Emporte">Emporté</button> </td>`
        } else if (element.state == "option") {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button><button class="btn btn-primary btn-sm vendu" data-bs-toggle="modal" data-bs-target="#modalFurniture" data-bs-whatever="Vendu">Vendu</button> </td>`
        } else {
            table += `<td> <button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mx-1">Voir</button></td>`
        }
        table += `</tr>`;
    });

    table += `</tbody>
    </table>
    </div>`;

    let divTableFurniture = document.getElementById("divTableFurniture");
    divTableFurniture.innerHTML =
        table +
        `<div class="modal fade" id="modalFurniture" tabindex="-1" aria-labelledby="modalFurnitureLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="modalFurnitureLabel">New message</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div id="divModal">
            </div>
          </div>
        </div>
      </div>
    `;

    data.forEach((element) => {
        const line = document.getElementById("btn" + element.idFurniture);
        line.addEventListener("click", (e) => {
            RedirectUrl("/furniture", element.idFurniture);
        });
    });
}

const createTableVisiteAttente = async () => {
    let data = null;
    try {
        data = await callAPI(API_VISITE_URL + "?etat=attente", "GET", token);
    } catch (err) {
        console.error("AdminPage::createTableVisiteAttente", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }
    if (!data) return;
    let table = `
    <h5 id="pageTitle">Visiste(s) en attente(s)</h5>
    <div id="tableVisiteAttente" class="table-responsive mt-3">
    <table class="table table-bordered">
        <thead>
            <tr>
                <th width="5%">ID</th>
                <th width="20%">Date</th>
                <th width="25%">Plage horaire</th>
                <th width="30%">Adresse</th>
                <th width="20%"></th>
            </tr>
        </thead>
        <tbody>`;

    data.forEach((element) => {
        let date = new Date(element.creationDate).toLocaleDateString("fr-FR")
        let time = new Date(element.creationDate).toLocaleTimeString("fr-FR")
        table += `<tr data-id="${element.idVisit}">
                  <td>${element.idVisit}</td>
                  <td>${date} ${time}</td>
                  <td>${element.timePeriod}</td>
                  <td>${element.streetVisit} ${element.numVisit} ${element.municipalityVisit} ${element.postalCodeVisit}</td>
                  <td><button id="accepteVisiteBtn" class="btn btn-primary btn-sm mb-1" data-bs-toggle="modal" data-bs-target="#modalVisiteAttente" data-bs-whatever="Confirmer">Confirmer</button> <button id="annulerVisiteBtn" class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#modalVisiteAttente" data-bs-whatever="Annuler">Annuler</button></td>
                  </tr>
                  `;
    });

    table += `</tbody>
    </table>
    </div>`;

    let divTableVisiteAttente = document.getElementById("divTableVisiteAttente");
    divTableVisiteAttente.innerHTML =
        table +
        `<div class="modal fade" id="modalVisiteAttente" tabindex="-1" aria-labelledby="modalVisiteAttenteLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalVisiteAttenteLabel">New message</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div id="divModalVisiteAttente">
                    </div>
                </div>
            </div>
        </div>`;
}

const createTableVisiteConfirmee = async () => {
    let data = null;
    try {
        data = await callAPI(API_VISITE_URL + "?etat=confirmee", "GET", token);
    } catch (err) {
        console.error("AdminPage::createTableVisiteConfirmee", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }
    if (!data) return;
    let table = `
    <h5 id="pageTitle">Visiste(s) confirmée(s)</h5>
    <div id="tableVisiteConfirmee" class="table-responsive mt-3">
    <table class="table table-bordered">
        <thead>
            <tr>
                <th width="5%">ID</th>
                <th width="25%">Date</th>
                <th width="35%">Plage horaire</th>
                <th width="35%">Adresse</th>
            </tr>
        </thead>
        <tbody>`;

    data.forEach((element) => {
        let date = new Date(element.visitDate).toLocaleDateString("fr-FR")
        let time = new Date(element.visitDate).toLocaleTimeString("fr-FR")
        table += `<tr class="trVisiteConfirmee" data-id="${element.idVisit}">
                  <td>${element.idVisit}</td>
                  <td>${date} ${time}</td>
                  <td>${element.timePeriod}</td>
                  <td>${element.streetVisit} ${element.numVisit} ${element.municipalityVisit} ${element.postalCodeVisit}</td>
                  </tr>`;
    });

    table += `</tbody>
    </table>
    </div>`;

    let divTableVisiteConfirmee = document.getElementById("divTableVisiteConfirmee");
    divTableVisiteConfirmee.innerHTML = table;
}

export default AdminPage;
