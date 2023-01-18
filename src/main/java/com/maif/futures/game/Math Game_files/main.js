const problemElement = document.querySelector(".problem")

let state = {
    score: 0,
    mauvaisesReponses: 0
}

function nouveauProbleme() {
    state.probleme = genererUnProbleme();
    problemElement.innerHTML =
        `${state.probleme.premierChiffre} ${state.probleme.operateur} ${state.probleme.deuxiemeChiffre}`
}

nouveauProbleme();

function genererNombre(max) {
    return Math.floor(Math.random() * (max + 1));
}

function genererUnProbleme() {
    return {
        premierChiffre: genererNombre(10),
        deuxiemeChiffre: genererNombre(10),
        operateur: ['+', '-', 'x'][genererNombre(2)]
    }
}