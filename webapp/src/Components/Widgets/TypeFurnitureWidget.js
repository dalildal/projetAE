import callAPI from "../../utils/api.js";
const API_FURNITURE_URL = "/api/furniture/";

let typeFurnitureWidget = `
`;

const TypeFurnitureWidget = async (index) => {
    if(index == "") {
        let data = null;
        try {
            data = await callAPI(API_FURNITURE_URL + "type", "GET");
        } catch (err) {
            console.error("HomePage::createSelect", err);
            PrintError(err);
        }

        if (!data) return;

        let select = `
            <div class="col-md">
                <div class="form-floating">
                    <select id="type_meuble" class="form-select form-select-sm">
                    <option value="tous">tous</option>
        `;
        data.forEach((element) => {
            select += `<option value="${element}"> ${element} </option>`
        });
        select += `
                    </select>
                    <label for"type_meuble">Sélectionnez le type de meuble souhaité</label>
                </div>
            </div>
        `;
        let mySelectDiv = document.getElementById("divSelect");
        mySelectDiv.innerHTML = select;
    } else {
        let data = null;
        try {
            data = await callAPI(API_FURNITURE_URL + "type", "GET");
        } catch (err) {
            console.error("HomePage::createSelect", err);
            PrintError(err);
        }
    
        if (!data) return;
    
        let select = `
            <div class="col-md">
                <div class="form-floating">
                    <select id="type_meuble${index}" class="form-select form-select-sm">
        `;
        data.forEach((element) => {
            select += `<option value="${element}"> ${element} </option>`
        });
        select += `
                    </select>
                    <label for"type_meuble">Sélectionnez le type de meuble souhaité</label>
                </div>
            </div>
        `;
        let mySelectDiv = document.getElementById("divSelect"+index);
        mySelectDiv.innerHTML = select;
    }
};

export default TypeFurnitureWidget;