import { RedirectUrl } from "./Router.js";
import callAPI from "../utils/api.js";
import PrintError from "./PrintError.js";
import TypeFurnitureWidget from "./Widgets/TypeFurnitureWidget.js";

const API_FURNITURE_URL = "/api/furniture/";
const API_PICTURE_URL = "/api/pictures/";

let homePage = `
    <div class="col col-md-12 mb-5">
        
        <div id="divSelect"></div>

        <div class="mt-3 mb-3">
            <div class="lazy" id="myCarousel"></div>
        </div>
        
        <div id="cadre_adresse" class="card col col-md-4 text-center">
            <h4 class="card-title"><u>Adresse du magasin</u></h4>
            <div class="card-body"> 
                <p class="card-text">Sentier des artistes, 1bis</br>4800 Verviers </p>
            </div>
        </div>

    </div>
`;

let page = document.querySelector("#page");
let $ = require("jquery");

const HomePage = async () => {
    page.innerHTML = homePage;
    await createSelect();
    $('.lazy').slick({
        infinite: true,
        slidesToShow: 3,
        slidesToScroll: 1,
        focusOnSelect: true,
        autoplay: true,
        autoplaySpeed: 10000,
        adaptiveHeight: true
    });
    onHomePage();

};

const onHomePage = async (type = "tous") => {
    try {
        let listFurniture = null;
        if (type === "tous") {
            listFurniture = await callAPI(API_FURNITURE_URL + "?etat=a vendre&etat=option&etat=vendu&etat=livre&etat=emporte&etat=reserve", "GET");
        } else {
            listFurniture = await callAPI(API_FURNITURE_URL + "?type=" + type + '&etat=' + "a vendre&etat=option&etat=vendu&etat=livre&etat=emporte&etat=reserve", "GET");
        }
        createCarousel(listFurniture);
    } catch (err) {
        console.error("HomePage::onHomePage", err);
        PrintError(err);
    }
};

const createSelect = async () => {
    await TypeFurnitureWidget("");

    const selectFurniture = document.getElementById("type_meuble");
    selectFurniture.addEventListener("change", (e) => {
        let type = e.target.options[e.target.selectedIndex].value
        onHomePage(type);
    });
};

const createCarousel = (data) => {
    if (!data) return;
    let myCarouselDiv = document.getElementById("myCarousel");

    const tmp = []
    data.forEach(async (element) => {
        tmp.push(pictureInCarousel(element));
    });

    let divCarousel = "";
    Promise.all(tmp).then((e) => {
        $('.lazy').slick('slickRemove', null, null, true);
        e.forEach((furniture) => {
            divCarousel += furniture;
        })
        $('.lazy').slick('slickAdd', divCarousel);

        const furnitureDivs = document.querySelectorAll(".furnitureDiv");
        furnitureDivs.forEach((furnitureDiv) => {
            furnitureDiv.addEventListener("click", () => {
                RedirectUrl("/furniture", furnitureDiv.id);
            });
        });

    })


};

const pictureInCarousel = async (element) => {
    let divCarousel = "";
    let picture;
    try {
        picture = await callAPI(API_PICTURE_URL + "id/" + element.favoritePicture, "GET");
    } catch (err) {
        console.error("HomePage::pictureInCarousel", err);
        PrintError(err);
    }

    divCarousel += `
        <div class="col-md topCarousel">
            <div class="cardCarousel">
                <div id="${element.idFurniture}" class="furnitureDiv">
                    <img class="imgCarousel" data-lazy="${picture.link}"/>
                    <p>${element.description}</p>
                </div>
            </div>
        </div>
    `;

    return divCarousel;
}

export default HomePage;