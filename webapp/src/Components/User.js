
let user = {
    id_utilisateur: null,
    pseudo : null,
    nom: null,
    prenom: null,
    email: null,
    type: null,
}

const getUserData = () => {
  return user;
};

const setUserData = (id_utilisateur, pseudo, nom, prenom, email, type) => {
  user.id_utilisateur = id_utilisateur;
  user.pseudo = pseudo;
  user.nom = nom;
  user.prenom = prenom;
  user.email = email;
  user.type = type;
};

export {
    getUserData,
    setUserData,
};