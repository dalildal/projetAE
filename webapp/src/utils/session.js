import { setUserData } from "../Components/User";

const STORE_NAME = "user";
const THEME = "theme";

const getUserSessionData = () => {
  const retrievedUser = sessionStorage.getItem(STORE_NAME);
  if (!retrievedUser) return;
  return JSON.parse(retrievedUser);
};

const getUserLocalData = () => {
  const retrievedUser = localStorage.getItem(STORE_NAME);
  if (!retrievedUser) return;
  return JSON.parse(retrievedUser);
};

const setUserSessionData = (user) => {
  const storageValue = JSON.stringify(user);
  sessionStorage.setItem(STORE_NAME, storageValue);
};

const setUserLocalData = (user) => {
  const storageValue = JSON.stringify(user);
  localStorage.setItem(STORE_NAME, storageValue);
};

const getTheme = () => {
  const theme = localStorage.getItem(THEME);
  if (!theme) return;
  return JSON.parse(theme);
};

const setTheme = (theme) => {
  const storageValue = JSON.stringify(theme);
  localStorage.setItem(THEME, storageValue);
};

const removeSessionData = () => {
  setUserData(null,null,null,null,null,null);
  sessionStorage.removeItem(STORE_NAME);
  sessionStorage.removeItem(THEME);
};

const removeLocalData = () => {
  setUserData(null,null,null,null,null,null);
  localStorage.removeItem(STORE_NAME);
  localStorage.removeItem(THEME);
};

export {
  getUserSessionData,
  getUserLocalData,
  setUserSessionData,
  setUserLocalData,
  removeSessionData,
  removeLocalData,
  getTheme,
  setTheme,
};
