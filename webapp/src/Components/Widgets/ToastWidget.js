import { Toast } from "bootstrap";

let toastWidget = `
`;

const ToastWidget = (type,text) => {
    if(type == "success") {
        let divToast = document.getElementById("toastSuccess");
        let divToastText = document.getElementById("toastSuccessText");
        divToastText.innerText = text;
        let toast = new Toast(divToast);
        toast.show();
    }
    else if (type == "fail") {
        let divToast = document.getElementById("toastFail");
        let divToastText = document.getElementById("toastFailText");
        divToastText.innerText = text;
        let toast = new Toast(divToast);
        toast.show();
    }
    else if (type == "delete") {
        let divToast = document.getElementById("toastDelete");
        let divToastText = document.getElementById("toastDeleteText");
        divToastText.innerText = text;
        let toast = new Toast(divToast);
        toast.show();
    } else {
        console.error("ToastWidget::Please specify a correct type.");
    }
}

export default ToastWidget;