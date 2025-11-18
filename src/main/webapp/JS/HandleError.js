function clearError(style, type, subType) {
    let form;
    let formPrefix = style != null && style.trim() === "Ajout" ? "add" : "modify";

    switch (type) {
        case 'Article':     form = document.getElementById(formPrefix + 'Form_article'); break;
        case 'Employe':
        {
            if(subType.trim() === "Ajout" || subType.trim() === "Modification_total")
            {
                form = document.getElementById(formPrefix + 'Form_employe');
            }
            else if(subType.trim() === "Modification_role")
            {
                form = document.getElementById("modify_role");
            }
            else
            {
                form = document.getElementById('loginForm_employe');
            }
            break;
        }
        case 'Fournisseur': form = document.getElementById(formPrefix + 'Form_fournisseur'); break;
        case 'Type':        form = document.getElementById(formPrefix + 'Form_type'); break;
        case 'Flux':
            form = document.getElementById(formPrefix + 'Form_' + subType.toLowerCase());
            break;
    }

    // Efface d'abord toutes les erreurs visibles
    form.querySelectorAll("span[id^='error_']").forEach(span => {
        span.textContent = "";
    });
}

function putError(messages, style, type, subType) {
    let form;
    let formPrefix = style != null && style.trim() === "Ajout" ? "add" : "modify";

    console.log(style, type, subType.toLowerCase(), messages);

    switch (type) {
        case 'Article':     form = document.getElementById(formPrefix + 'Form_article'); break;
        case 'Employe':
        {
            if(subType.trim() === "Ajout" || subType.trim() === "Modification_total")
            {
                form = document.getElementById(formPrefix + 'Form_employe');
            }
            else if(subType.trim() === "Modification_role")
            {
                form = document.getElementById("modify_role");
            }
            else
            {
                form = document.getElementById('loginForm_employe');
            }
        break;
        }
        case 'Fournisseur': form = document.getElementById(formPrefix + 'Form_fournisseur'); break;
        case 'Type':        form = document.getElementById(formPrefix + 'Form_type'); break;
        case 'Flux':        form = document.getElementById('addForm_' + subType.toLowerCase()); break;
    }

    if (!form || !Array.isArray(messages)) return;

    // Efface d'abord toutes les erreurs visibles
    form.querySelectorAll("span[id^='error_']").forEach(span => {
        span.textContent = "";
    });

    // Affiche chaque erreur
    messages.forEach(error => {
        // Structure attendue : "tag_fournisseur: Ce champ est obligatoire"
        const [field, msg] = error.split(":").map(s => s.trim());
        const span = form.querySelector(`#error_${field}`);
        if (span) {
            console.log(msg);
            span.textContent = msg;
        }
    });
}


function testMessage(message, type, subtype) {
    switch(type) {
        case "Employe": {
            let mot1, mot2, form;
            if (subtype === "Ajout") {
                form = document.getElementById('addForm_employe');
                mot1 = document.getElementById("mot_de_passe").value;
                mot2 = document.getElementById("mot_de_passe confirm").value;
            }
            else if (subtype === "Modification") {
                form = document.getElementById('modifyForm_employe');
                mot1 = document.getElementById("nouveau_mot_de_passe").value;
                mot2 = document.getElementById("nouveau_mot_de_passe confirm").value;
            }

            const errorSpan = form.querySelector("#error_mot_de_passe");

            if (mot2 !== null && mot2.trim() !== "") {
                if (mot1 === null || mot1.trim() === "") {
                    errorSpan.textContent = "Veuillez saisir le mot de passe à confirmer";
                } else if (mot1 !== mot2) {
                    errorSpan.textContent = "Veuillez vérifier le mot de passe confirmé";
                } else {
                    errorSpan.textContent = "";
                }
            } else {
                // Si mot2 est vide, on ne fait rien
                errorSpan.textContent = "";
            }
            break;
        }
    }
}