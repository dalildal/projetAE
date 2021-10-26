import { getUserLocalData, getUserSessionData } from "../utils/session.js";
import { RedirectUrl } from "./Router.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";

const API_FURNITURE_URL = "/api/furniture/";

let historyClientId;
if (history.state != null) {
    historyClientId = history.state.data;
}
let infoClientPage = `
<h2> <p> Les achetés : </p> </h2>
<div class="bought">
</div>
<h2> <p> Les vendus : </p> </h2>
<div class="sell">
</div>`;
let token;

const InfoClientPage = async (id) => {
    let page = document.querySelector("#page");
    page.innerHTML = infoClientPage;
    const user = getUserSessionData();
    const userBis = getUserLocalData();

    if (id != null) {
        historyClientId = id;
    }
    if (userBis) {
        token = userBis.token;
    } else if (user) {
        token = user.token;
    }

    try {
        onInfoClientPage(historyClientId);
    } catch (err) {
        console.error("InfoClientPage::infoClientPage", err);
        PrintError(err);
        if (err.message == "Malformed token") RedirectUrl("/logout");
    }
};

const onInfoClientPage = async (historyClientId) => {
    const listPurchaseFurniture = await callAPI(API_FURNITURE_URL + "allFurniturePurchase/" + historyClientId, "GET", token);
    const listSellFurniture = await callAPI(API_FURNITURE_URL + "allFurnitureSell/" + historyClientId, "GET", token);

    let bought = document.querySelector(".bought");
    let table = `
        <div id="tableSearchedUser" class="table-responsive mt-3">
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Id meuble</th>
                    <th>Description</th>
                    <th>Prix d'achat</th>
                    <th>Prix de vente</th> 
                </tr>
            </thead>
            <tbody>`;
    if (listPurchaseFurniture[0] != null) {
        listPurchaseFurniture.forEach(element => {
            table += `<tr>
                            <td>${element.idFurniture}</td>
                            <td>${element.description}</td>
                            <td>${element.purchasePrice}</td>
                            <td>${element.salesPrice}</td>
                        `
            table += `</tr>
                        `;
        })
        table += `  </tbody>
                                    </table>
                                    </div>`;
        bought.innerHTML = table;
    } else {
        let msg = `<h6>Aucun meuble acheté.</h6>`;
        bought.innerHTML = msg;
    }

    let sell = document.querySelector(".sell");
    let tableSell = `
        <div id="tableSearchedUser" class="table-responsive mt-3">
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Id meuble</th>
                    <th>Description</th>
                    <th>Prix d'achat</th>
                    <th>Prix de vente</th> 
                </tr>
            </thead>
            <tbody>`;
    if (listSellFurniture[0] != null) {
        listSellFurniture.forEach(element => {
            tableSell += `<tr>
                            <td>${element.idFurniture}</td>
                            <td>${element.description}</td>
                            <td>${element.purchasePrice}</td>
                            <td>${element.salesPrice}</td>
                        `
            tableSell += `</tr>
                        `;
        })
        tableSell += `  </tbody>
                                    </table>
                                    </div>`;
        sell.innerHTML = tableSell;
    } else {
        sell.innerHTML = `<h6>Aucun meuble vendu.</h6>`;
    }
}

export default InfoClientPage;