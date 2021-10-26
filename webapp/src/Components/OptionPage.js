import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { getUserData } from "./User";
import { RedirectUrl } from "./Router.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import ToastWidget from "./Widgets/ToastWidget.js";
const API_FURNITURE_URL = "/api/furniture/";
const API_OPTION_URL = "/api/options/";

let optionPage = `
    <div class="col col-md-12">

        <div class="col-md-6 mb-5">
            <h5> Meubles achetés </h5>
            <div class="input-group">
                <input id="searchFurniture" type="search" class="form-control rounded" placeholder="Rechercher..." aria-label="Search" aria-describedby="search-addon" />
            </div>
        </div>

        <div id="searchedFurnitures"></div>

        <h5 id="pageTitle">Mes Options</h5>
        <div id="optionTable"></div>

    </div>
`;

let userData = getUserData();
let token;

const OptionPage = async () => {
    let page = document.querySelector("#page");
    page.innerHTML = optionPage;
    const user = getUserSessionData();
    const userBis = getUserLocalData();

    if (userBis) {
        token = userBis.token;
    } else if (user) {
        token = user.token;
    }

    try {
        const listOption = await callAPI(API_OPTION_URL + userData.id_utilisateur, "GET", token);
        createTableOption(listOption);
        getFurniture(listOption);
    } catch (err) {
        console.error("OptionPage::optionPage", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }

};

const createTableOption = async (listOption) => {
    let optionTable = document.getElementById("optionTable");
    if (listOption == "") {
        optionTable.innerHTML = "<p>Vous n'avez aucune option en cours.</p>";
        return;
    } else {
        let table = `
                <div id="tableUser" class="table-responsive mt-3">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th width="5%">ID</th>
                                <th width="60%">Meubles</th>
                                <th width="20%">Prix</th>
                                <th width="10%">-</th>
                                <th width="5%">-</th>
                            </tr>
                        </thead>
                        <tbody>
        `;

        listOption.forEach((element) => {
            table += `
                <tr data-id="${element.idFurniture}">
                    <td>${element.idFurniture}</td>
                    <td id="desc${element.idFurniture}"></td>
                    <td id="prix${element.idFurniture}"></td>
                    <td><button id="btn${element.idFurniture}" class="btn btn-primary btn-sm mt-2">Voir</button></td>
                    <td><input class="checkbox" type="checkbox" id="${element.idFurniture}"></td>
                </tr>
            `;
        });

        table += `
                    </tbody>
                </table>
            </div>
        `;

        optionTable.innerHTML = table +
            '<button id="cancelBtn" class="btn btn-primary mt-2">Annuler les options cochées</button>';

        listOption.forEach((element) => {
            const line = document.getElementById("btn" + element.idFurniture);
            line.addEventListener("click", (e) => {
                RedirectUrl("/furniture", element.idFurniture);
            });
        });

        const checkBoxs = document.querySelectorAll(".checkbox");
        const cancelBtn = document.getElementById("cancelBtn");
        cancelBtn.addEventListener("click", (e) => {
            e.preventDefault();
            let tmp = 0;
            checkBoxs.forEach(async (checkBox) => {
                if (checkBox.checked) {
    
                    let data = {
                        idFurniture: checkBox.id,
                        idUser: userData.id_utilisateur,
                    }
                    try {
                        const optionUpdate = await callAPI(API_OPTION_URL, "PUT", token, data);
                    } catch (err) {
                        console.error("OptionPage::optionPage", err);
                        PrintError(err);
                        if (err.message == "Malformed token") RedirectUrl("/logout");
                    }
                } else {
                    tmp++;
                }
            });
            if(tmp == checkBoxs.length) {
                ToastWidget("fail", "Veuillez cocher au moins une option à annuler.");
            }
        })
        onOptionPage();
    }
};

const getFurniture = (listOption) => {
    listOption.forEach( async (element) => {
        try {
            const furniture = await callAPI(API_FURNITURE_URL + element.idFurniture, "GET", token);
            putFurnitureData(furniture);
        } catch (err) {
            console.error("FurniturePage::furniturePage", err);
            PrintError(err);
            if (err.message == "Malformed token") RedirectUrl("/logout");
        }
    });
}

const putFurnitureData = (furniture) => {
    let description = document.getElementById("desc"+furniture.idFurniture);
    description.innerHTML += furniture.description;
    let salesPrice = document.getElementById("prix"+furniture.idFurniture);
    salesPrice.innerHTML += furniture.salesPrice + " €";
};

const onOptionPage = () => {
    let divSearchFurnitures = document.getElementById("searchedFurnitures");
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
            furnitures = await callAPI(API_FURNITURE_URL + "searchedFurniturePurchase/" + userData.id_utilisateur + "/" + e.target.value, "GET", token);
        } catch (err) {
            console.error("OptionPage::searchBtn", err);
            if (err.message == "Malformed token") RedirectUrl("/logout");
        }
        createTableSearchedFurnitures(furnitures);
    })

    const createTableSearchedFurnitures = (furnitures) => {
        divSearchFurnitures = document.getElementById("searchedFurnitures");
        let table = `
    <div id="tableSearchedFurnitures" class="table-responsive mt-3">
    <table class="table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Description</th>
                <th>Etat</th>
            </tr>
        </thead>
        <tbody>`;
        if (furnitures) {
            furnitures.forEach((furniture) => {
                table += `<tr>
                        <td>${furniture.idFurniture}</td>
                        <td>${furniture.description}</td>
                        <td>${furniture.state}</td>
                      </tr> `
            })
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
                RedirectUrl("/infoFurniture", tr.id);
            });
        });
    }
}

export default OptionPage;