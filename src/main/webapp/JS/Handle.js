function initEmployeWebSocket(contextPath, matricule, role) {
    if (!matricule && !role) {
        console.warn("Aucun matricule fourni pour la connexion WebSocket.");
        return;
    }
    console.log(role);
    const protocol = window.location.protocol === "https:" ? "wss://" : "ws://";
    const wsUrl = protocol + window.location.host + contextPath + "/EmployeLog/" + matricule + "/" + role;

    let ws, params;

    function initWebSocket() {
        ws = new WebSocket(wsUrl);

        ws.onopen = () => {
            console.log("✅ WebSocket connecté pour l'employé :", matricule);
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                if (data.type === "refresh_data") {
                    console.log("🔄 Rafraîchissement des données demandé :", data.message);

                    // Selon la page active, on relance la fonction appropriée :
                    if (window.location.pathname.includes("/Articles-Types/Articles")) {
                        searchArticle();
                    } else if (window.location.pathname.includes("/Employes")) {
                        searchEmploye();
                    } else if (window.location.pathname.includes("/Fournisseurs")) {
                        searchFournisseur();
                    } else if (window.location.pathname.includes("/Articles-Types/Types")) {
                        searchType();
                    } else if (window.location.pathname.includes("/Mouvements/Entrees")) {
                        searchFlux("Entree");
                    } else if (window.location.pathname.includes("/Mouvements/Sorties")) {
                        searchFlux("Sortie");
                    }
                }
                if (data.type === "force_logout") {
                    params = new URLSearchParams({
                        activite : "desactivate"
                    })

                    alert(data.message || "Votre compte a été désactivé par l'administrateur.");

                    fetch(contextPath + "/ForceLogout?" + params.toString())
                        .then(res => {
                            if (!res.ok) throw new Error("Erreur de logout serveur");
                            return res.text();
                        })
                        .then(() => {
                            // 2️⃣ Une fois la requête HTTP terminée, notifier le serveur WebSocket
                            ws.send(JSON.stringify({
                                type: "passage_logout",
                                message: "Ok to log out"
                            }));
                        })
                        .finally(() => {
                            // 3️⃣ Rediriger ensuite
                            window.location.href = contextPath + "/Connexion";
                        });
                }
                console.log(data.message + " " + data.type);
                if (data.type === "notify_decision") {
                    if(confirm(data.message + "\n" + "Accepteririez - vous ce nouveau compte")){
                        ws.send(JSON.stringify({
                            type: "accept_admin",
                            message: "l'admin a accepté l'employé concerné en tant que nouvel utilisateur",
                            matricule: data.matricule
                        }));
                    }
                    else{
                        ws.send(JSON.stringify({
                            type: "deny_admin",
                            message: "l'admin a refusé l'employé concerné en tant en tant que nouvel utilisateur",
                            matricule: data.matricule
                        }));
                    }
                }
                if (data.type === "notify_info" || data.type === "modify_role_info") {
                    alert(data.message);
                }
            } catch (err) {
                console.error("Erreur de parsing du message WebSocket :", err);
            }
        };

        ws.onclose = (event) => {
            console.warn("❌ WebSocket déconnecté :", event.reason || "connexion fermée");

            setTimeout(initWebSocket, 5000);
        };

        ws.onerror = (err) => {
            console.error("⚠️ Erreur WebSocket :", err);
        };
    }

    initWebSocket();
}

function searchArticle() {
    const article = document.querySelector('input[name="nom_article"]').value;
    const type = document.querySelector('input[name="nom_type"]').value;

    const params = new URLSearchParams();
    if(article.trim() !== "") params.append("nom_article", article);
    if(type.trim() !== "") params.append("nom_type", type);

    const currentBody = document.querySelector("#result_article");
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch("/Stock/Articles?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            const newContent = tempDiv.querySelector("#result_article");
            currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun résultat trouvé.</p>";
        })
        .catch(err => {
            console.error(err);
            currentBody.innerHTML = "<p>Erreur de chargement.</p>";
        });
}

function searchEmploye() {
    const nomPrenoms = document.querySelector('input[name="nom_prenom"]').value;

    const params = new URLSearchParams();
    if(nomPrenoms.trim() !== "") params.append("nom_prenom", nomPrenoms);

    const currentBody = document.querySelector("#result_employe_connected");
    const currentBody1 = document.querySelector("#result_employe_not_connected");

    currentBody.innerHTML = "<p>Recherche en cours...</p>";
    currentBody1.innerHTML = "<p>Recherche en cours...</p>";

    fetch("/Stock/Employes?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;

            const newContent = tempDiv.querySelector("#result_employe_connected");
            const newContent1 = tempDiv.querySelector("#result_employe_not_connected");

            currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun employé en ligne trouvé.</p>";
            currentBody1.innerHTML = newContent1 ? newContent1.innerHTML : "<p>Aucun employé hors ligne trouvé.</p>";
        })
        .catch(err => {
            console.error(err);
            currentBody.innerHTML = "<p>Erreur de chargement.</p>";
        });
}

function searchFournisseur() {
    const fournisseur = document.querySelector('input[name="nom_fournisseur"]').value;

    const params = new URLSearchParams();
    if(fournisseur.trim() !== "") params.append("nom_fournisseur", fournisseur);

    const currentBody = document.querySelector("#result_fournisseur");
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch("/Stock/Fournisseurs?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            const newContent = tempDiv.querySelector("#result_fournisseur");
            currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun résultat trouvé.</p>";
        })
        .catch(err => {
            console.error(err);
            currentBody.innerHTML = "<p>Erreur de chargement.</p>";
        });
}

function searchType() {
    const type = document.querySelector('input[name="nom_type"]').value;

    const params = new URLSearchParams();
    if(type.trim() !== "") params.append("nom_type", type);

    const currentBody = document.querySelector("#result_type");
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch("/Stock/Types?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            const newContent = tempDiv.querySelector("#result_type");
            currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun résultat trouvé.</p>";
        })
        .catch(err => {
            console.error(err);
            currentBody.innerHTML = "<p>Erreur de chargement.</p>";
        });
}

function searchFlux(type) {
    let dateFlux = "", param = "", nomArticle = "", destinataire = "", expediteur = "";

    const nom = document.getElementById("nom_article").value;
    if (nom.trim() !== "") nomArticle = nom;

    const selectDate = document.querySelector('select[name="date"]').value;

    if (selectDate === "date") {
        const dateFlux1 = document.getElementById("date_flux").value;
        const precision = document.getElementById("precision_date").value;
        if (dateFlux1 && precision) {
            dateFlux = dateFlux1;
            param = precision;
        }
    } else if (selectDate === "mois") {

        const monthFlux = document.getElementById("month_flux").value;
        const yearFlux = document.getElementById("year_flux").value;

        if (monthFlux && yearFlux) {
            dateFlux = monthFlux + "/" + yearFlux;
        }
        param = "month";
    }
	
	if(type.trim() !== "" && type.trim() === "Sortie") {
		const destinataireInput = document.getElementById("destinataire").value;
		const expediteurInput = document.getElementById("expediteur").value;
		
		if(destinataireInput.trim() !== "") destinataire = destinataireInput; 
		if(expediteurInput.trim() !== "") expediteur = expediteurInput;
		
	}

    const data = new URLSearchParams({
        article: nomArticle,
        date_flux: dateFlux,
        date_params: param,
		destinataire: destinataire,
		expediteur: expediteur
    });

    console.log(data);

    if (type.trim() !== "") {

        const selector = type.trim() === "Entree" ? "#result_entree" : "#result_sortie"

        const currentBody = document.querySelector(selector);
        currentBody.innerHTML = "<p>Recherche en cours...</p>";

        fetch("/Stock" + (type.trim() === "Entree" ? "/Entrees" : "/Sorties") + "?" + data.toString())
            .then(res => res.text())
            .then(html => {
                const tempDiv = document.createElement("div");
                tempDiv.innerHTML = html;
                const newContent = tempDiv.querySelector(selector);
                currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun résultat trouvé.</p>";
            })
            .catch(err => {
                console.error(err);
                currentBody.innerHTML = "<p>Erreur de chargement.</p>";
            });
    }
}

function updateSearchFlux(style) {
	if(style.trim() !== "" || style.trim() === "Date_flux") {
	    const divDate1 = document.getElementById("date_search_1");
	    const divDate2 = document.getElementById("date_search_2");
	    const selectDate = document.querySelector('select[name="date"]').value;	

    	divDate1.style.display = (selectDate === "date") ? "block" : "none";
    	divDate2.style.display = (selectDate === "mois") ? "block" : "none";
	}
}

function setUpdateArticle(tag) {
    if (tag.trim() !== "") {
        console.log(tag)
        window.location.href = "/Stock/Articles-Types/SessionModifyArtType.jsp?section=Article&id=" + encodeURIComponent(tag);
    }
}

function setUpdateFournisseur(tag){
    const params = new URLSearchParams();
    if(tag.trim() !== ""){
        window.location.href = "/Stock/Fournisseur/SessionModifyFournisseur.jsp?id=" + encodeURIComponent(tag);
    }
}

function setUpdateType(tag) {
    if (tag.trim() !== "") {
        window.location.href = "/Stock/Articles-Types/SessionModifyArtType.jsp?section=Type&id=" + encodeURIComponent(tag);
    }
}

function logOut(contextPath,data) {
    fetch(contextPath + "/LogoutServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            matricule: data.dataset.matricule,
            email: data.dataset.email,
            mot_de_passe: data.dataset.mot_de_passe
        })
    })
        .then(res => {
            alert("Deconnexion pour l'employé " + data.dataset.matricule + " est terminé");
            window.location.href = contextPath + "/Connexion";
        })
        .catch(err => console.error("ErrorLogOut:", err));
}

function searchEntree(data){
    const params = new URLSearchParams();
    if (data.trim() === "article"){
        const article = document.getElementById("nom_article").value;
        if(article.trim() !== "") params.append("nom_article", article);
    }
    else{
        const fournisseur = document.getElementById("raison_sociale").value;
        if(fournisseur.trim() !== "") params.append("raison_sociale", fournisseur);
    }

    const currentBody = document.querySelector((data.trim() === "article") ? "#result_article" : "#result_expediteur");
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch("/Stock/Mouvements/Entrees/Creation?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            const newContent = tempDiv.querySelector((data.trim() === "article") ? "#result_article" : "#result_expediteur");
            currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun résultat trouvé.</p>";
        })
        .catch(err => {
            console.error(err);
            currentBody.innerHTML = "<p>Erreur de chargement.</p>";
        });
}

function searchSortie(data){
    const params = new URLSearchParams();
    if (data.trim() === "article"){
        const article = document.getElementById("nom_article").value;
        if(article.trim() !== "") params.append("nom_article", article);
    }
    else{
        const destinataire = document.getElementById("destinataire_search").value;
        if(destinataire.trim() !== "") params.append("nom_prenom_ou_matricule", destinataire);
    }

    const currentBody = document.querySelector((data.trim() === "article") ? "#result_article" : "#result_destinataire");
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch("/Stock/Mouvements/Sorties/Creation?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            const newContent = tempDiv.querySelector((data.trim() === "article") ? "#result_article" : "#result_destinataire");
            currentBody.innerHTML = newContent ? newContent.innerHTML : "<p>Aucun résultat trouvé.</p>";
        })
        .catch(err => {
            console.error(err);
            currentBody.innerHTML = "<p>Erreur de chargement.</p>";
        });
}

function setDetails(e, action, type, data) {
    if (e) {
        e.preventDefault();

        let dialog;

        // Fonction utilitaire pour vider les champs d'un dialogue
        function resetFields(fields) {
            fields.forEach(field => {
                const el = document.getElementById(field);
                if (!el) return;
                if (el.tagName === "INPUT" || el.tagName === "TEXTAREA") {
                    el.value = "";
                } else {
                    el.textContent = "";
                }
            });
        }

        switch (type) {
            case "Article":
                dialog = document.getElementById("dialog_article");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_nom_article").textContent = data.dataset.nom_article;
                    document.getElementById("dialog_type_article").textContent = "Type: " + data.dataset.type_article;
                    document.getElementById("dialog_nombre_article").textContent = "Stock: " + data.dataset.stock_article;
                    document.getElementById("dialog_tag_article").value = data.dataset.tag_article;
                } else if (action === 'Close') {
                    resetFields(["dialog_nom_article", "dialog_type_article", "dialog_nombre_article", "dialog_tag_article"]);
                }
                break;

            case "Destinataire-Employe":
                dialog = document.getElementById("dialog_destinataire");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_destinataire_nom_complet").textContent =
                        data.dataset.nom + " " + data.dataset.prenom;
                    document.getElementById("dialog_destinataire_id").textContent = "Matricule: " + data.dataset.matricule;
                    document.getElementById("dialog_destinataire_role").textContent = "Role: " + data.dataset.role;
                    document.getElementById("dialog_destinataire_matricule").value = data.dataset.matricule;
                } else if (action === 'Close') {
                    resetFields([
                        "dialog_destinataire_nom_complet",
                        "dialog_destinataire_id",
                        "dialog_destinataire_role",
                        "dialog_destinataire_matricule"
                    ]);
                }
                break;

            case "Employe":
                dialog = document.getElementById("dialog_employe");
                const divPart1 = document.getElementById("employe_details");
                const divPart2 = document.getElementById("modify_role");
                const buttonRole = document.getElementById("modify_role_btn");
                if (action === 'Show' && data) {
                    divPart1.style.display = "block";
                    divPart2.style.display = "none";
                    document.getElementById("dialog_employe_nom_complet").textContent = data.dataset.nom_prenom;
                    document.getElementById("dialog_employe_adresse").textContent = "Adresse: " + data.dataset.adresse;
                    document.getElementById("dialog_employe_email").textContent = data.dataset.email;
                    document.getElementById("dialog_employe_telephone").textContent = "Téléphone: " + data.dataset.telephone;
                    document.getElementById("dialog_employe_date_naissance").textContent = "Date de naissance: " + data.dataset.date_naissance;
                    document.getElementById("dialog_employe_matricule").textContent = data.dataset.matricule;
                    document.getElementById("dialog_employe_role").textContent = data.dataset.role;
                    document.getElementById("dialog_employe_date_creation").textContent = "Date de création du compte: " + data.dataset.date_creation;
                    document.getElementById("dialog_employe_date_modification").textContent = "Date de modification du compte: " + data.dataset.date_modification;
                    if(data.dataset.role === "Administrateur"){
                        buttonRole.style.display = "none";
                    }
                    else{
                        buttonRole.style.display = "block";
                    }
                } else if (action === 'Close') {
                    divPart1.style.display = "none";
                    divPart2.style.display = "none";
                    resetFields([
                        "dialog_employe_nom_complet",
                        "dialog_employe_adresse",
                        "dialog_employe_email",
                        "dialog_employe_telephone",
                        "dialog_employe_date_naissance",
                        "dialog_employe_matricule",
                        "dialog_employe_role",
                        "dialog_employe_date_creation",
                        "dialog_employe_date_modification"
                    ]);
                }
                break;

            case "Expediteur-Destinataire":
                dialog = document.getElementById("dialog_employe");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_employe_nom_complet").textContent = data.dataset.nom_prenom;
                    document.getElementById("dialog_employe_adresse").textContent = "Adresse: " + data.dataset.adresse;
                    document.getElementById("dialog_employe_email").textContent = data.dataset.email;
                    document.getElementById("dialog_employe_telephone").textContent = "Téléphone: " + data.dataset.telephone;
                    document.getElementById("dialog_employe_date_naissance").textContent = "Date de naissance: " + data.dataset.date_naissance;
                    document.getElementById("dialog_employe_matricule").textContent = data.dataset.matricule;
                    document.getElementById("dialog_employe_role").textContent = data.dataset.role;
                    document.getElementById("dialog_employe_date_creation").textContent = "Date de création du compte: " + data.dataset.date_creation;
                    document.getElementById("dialog_employe_date_modification").textContent = "Date de modification du compte: " + data.dataset.date_modification;
                } else if (action === 'Close') {
                    resetFields([
                        "dialog_employe_nom_complet",
                        "dialog_employe_adresse",
                        "dialog_employe_email",
                        "dialog_employe_telephone",
                        "dialog_employe_date_naissance",
                        "dialog_employe_matricule",
                        "dialog_employe_role",
                        "dialog_employe_date_creation",
                        "dialog_employe_date_modification"
                    ]);
                }
                break;

            case "Expediteur-Administrateur":
                dialog = document.getElementById("dialog_expediteur");
                if (action === 'Close') {
                    resetFields([
                        "dialog_expediteur_raison_sociale",
                        "dialog_expediteur_email",
                        "dialog_expediteur_telephone",
                        "dialog_expediteur_tag_fournisseur"
                    ]);
                }
                break;

            case "Expediteur-Fournisseur":
                dialog = document.getElementById("dialog_expediteur");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_expediteur_raison_sociale").textContent = data.dataset.raison_sociale;
                    document.getElementById("dialog_expediteur_email").textContent = "Email: " + data.dataset.email_fournisseur;
                    document.getElementById("dialog_expediteur_telephone").textContent = "Téléphone: " + data.dataset.telephone_fournisseur;
                    document.getElementById("dialog_expediteur_tag_fournisseur").value = data.dataset.tag_fournisseur;
                } else if (action === 'Close') {
                    resetFields([
                        "dialog_expediteur_raison_sociale",
                        "dialog_expediteur_email",
                        "dialog_expediteur_telephone",
                        "dialog_expediteur_tag_fournisseur"
                    ]);
                }
                break;

            case "Fournisseur":
                dialog = document.getElementById("dialog_fournisseur");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_fournisseur_raison_sociale").textContent = data.dataset.raison_sociale;
                    document.getElementById("dialog_email_fournisseur").textContent = "Email: " + data.dataset.email;
                    document.getElementById("dialog_telephone_fournisseur").textContent = "Téléphone: " + data.dataset.telephone;
                    document.getElementById("dialog_tag_fournisseur").textContent = "Matricule: " + data.dataset.tag_fournisseur;
                } else if (action === 'Close') {
                    resetFields([
                        "dialog_fournisseur_raison_sociale",
                        "dialog_email_fournisseur",
                        "dialog_telephone_fournisseur",
                        "dialog_tag_fournisseur"
                    ]);
                }
                break;

            case "Type":
                dialog = document.getElementById("dialog_type_article");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_nom_article").textContent = data.dataset.nom_article;
                    document.getElementById("dialog_nom_type").textContent = "Type: " + data.dataset.nom_type;
                    document.getElementById("dialog_description_type").textContent = "Description du type: " + data.dataset.description_type;
                } else if (action === 'Close') {
                    resetFields(["dialog_nom_article", "dialog_nom_type", "dialog_description_type"]);
                }
                break;

            default:
                break;
        }

        if (!dialog) return;

        if (action === 'Show') {
            dialog.showModal();
        } else if (action === 'Close') {
            dialog.close();
        }
    }
}

function passModification(){
    const divPart1 = document.getElementById("employe_details");
    const divPart2 = document.getElementById("modify_role");

    divPart1.style.display = "none";
    divPart2.style.display = "block";

    const email = document.getElementById("dialog_employe_email").textContent;
    const role = document.getElementById("dialog_employe_role").textContent;
    const matricule = document.getElementById("dialog_employe_matricule").textContent;

    document.getElementById("emp_email").value = email;
    document.getElementById("emp_matricule").value = matricule;

    const roleSelect = document.getElementById("emp_role");

    if (role === "Employe Simple") {
        roleSelect.value = "3";
    } else if (role === "Administrateur") {
        roleSelect.value = "2";
    } else {
        roleSelect.value = ""; // aucun rôle sélectionné
    }

}

function handleSubmitWithPasswordDialog(event, section) {
    event.preventDefault();
    const form = document.getElementById("modifyForm_employe");

    if (section === "1" || section === "2") {
        const dialog = document.getElementById("passwordDialog");
        dialog.showModal();

        const confirmBtn = dialog.querySelector("#confirmBtn");
        confirmBtn.onclick = function(e) {
            e.preventDefault();
            const password = document.getElementById("motDePasseDialog").value;

            let input = form.querySelector('input[name="mot_de_passe"]');
            if (!input) {
                input = document.createElement("input");
                input.type = "hidden";
                input.name = "mot_de_passe";
                form.appendChild(input);
            }
            input.value = password;

            dialog.close();

            setForm(null, 'Modification', 'Employe', 'Modification_total');
        };

        dialog.querySelector("#cancelBtn").onclick = function(e) {
            e.preventDefault();
            dialog.close();
        };

    } else {
        setForm(event, 'Modification', 'Employe', 'Modification_total');
    }
}

function setTag(e,type,subType,tag){
    if (e) {
        e.preventDefault();
        if (type.trim() === "Entree") {
            switch (subType) {
                case "Article": {
                    document.getElementById("tag_article").value = tag;
                    break;
                }
                case "Fournisseur": {
                    document.getElementById("tag_fournisseur").value = tag;
                    break;
                }
                default:
                    break;
            }
        }
        if (type.trim() === "Sortie") {
            switch (subType) {
                case "Article": {
                    document.getElementById("tag_article").value = tag;
                    break;
                }
                case "Expediteur": {
                    document.getElementById("expediteur").value = tag;
                    break;
                }
                case "Destinataire": {
                    document.getElementById("destinataire").value = tag;
                    break;
                }
                default:
                    break;
            }
        }
    }
}

function setExcelTransform(type){
    let dateFlux = "", param = "", nomArticle = "", url = "";

    const nom = document.getElementById("nom_article").value;
    if (nom.trim() !== "") nomArticle = nom;

    const selectDate = document.querySelector('select[name="date"]').value;

    if (selectDate === "date") {
        const dateFlux1 = document.getElementById("date_flux").value;
        const precision = document.getElementById("precision_date").value;
        if (dateFlux1 && precision) {
            dateFlux = dateFlux1;
            param = precision;
        }
    } else if (selectDate === "mois") {
        const monthFlux = document.getElementById("month_flux").value;
        const yearFlux = document.getElementById("year_flux").value;

        if (monthFlux && yearFlux) {
            dateFlux = monthFlux + "/" + yearFlux;
        }
        param = "month";
    }

    if(nomArticle || (dateFlux && param)){
        const data = new URLSearchParams({
            article: nomArticle,
            date_flux: dateFlux,
            date_params: param
        });

        if(type.trim() !== ""){
            url = "/Stock/RapportExcel" + (type.trim() === "Entree" ? "/Entrees" : "/Sorties") + "?" + data.toString();
            window.location.href = url;
        }
    }

}

function setForm(e, style, type, subType){
    if(e != null) e.preventDefault();
    let form, submitButton, formData, data, url, nextUrl;

    let urlPrefix, formPrefix;
    if(style != null){
        if(style.trim() === "Ajout"){
            formPrefix = "add";
            urlPrefix = "Add";
        } else if(style.trim().includes("Modification") ){
            formPrefix = "modify";
            urlPrefix = "Update";
        }
    }


    switch (type){
        case "Article":
        {
            form = document.getElementById(formPrefix + 'Form_article');
            submitButton = form.querySelector('.submit_article');
            url = "/"+ urlPrefix + "ArticleServlet";
            nextUrl = "/Articles-Types/Articles";
            break;
        }

        case "Employe":
        {
            if(subType.trim() === "Ajout" || subType.trim() === "Modification_total")
            {
                form = document.getElementById(formPrefix + 'Form_employe');
                submitButton = form.querySelector('.submit_employe');
                url = "/"+ urlPrefix + "EmployeServlet";
                nextUrl = "/Profil";
            }
            else if(subType.trim() === "Modification_role")
            {
                form = document.getElementById("modify_role");
                submitButton = form.querySelector('.submit_employe');
                url = "/"+ urlPrefix + "Role" + "EmployeServlet";
                nextUrl = "/Employes";
            }
            else
            {
                form = document.getElementById('loginForm_employe');
                submitButton = form.querySelector('.submit_login');
                url = "/LoginServlet";
                nextUrl = "/Acceuil";
            }
            break;
        }

        case "Flux":
        {
            if(subType.trim() === "Entree"){
                form = document.getElementById(formPrefix + 'Form_entree');
                submitButton = form.querySelector('.submit_entree');
                url = "/"+ urlPrefix + "EntreeServlet";
                nextUrl = "/Mouvements/Entrees";
            }
            if(subType.trim() === "Sortie"){
                form = document.getElementById(formPrefix + 'Form_sortie');
                submitButton = form.querySelector('.submit_sortie');
                url = "/"+ urlPrefix + "SortieServlet";
                nextUrl = "/Mouvements/Sorties";
            }
            break;
        }

        case "Fournisseur":
        {
            form = document.getElementById(formPrefix + 'Form_fournisseur');
            submitButton = form.querySelector('.submit_fournisseur');
            url = "/"+ urlPrefix + "FournisseurServlet";
            nextUrl = "/Fournisseurs";
            break;
        }

        case "Type":
        {
            form = document.getElementById(formPrefix + 'Form_type');
            submitButton = form.querySelector('.submit_type');
            url = "/"+ urlPrefix + "TypeServlet";
            nextUrl = "/Articles-Types/Types";
            break;
        }
        default: break;
    }

    // 🔒 Désactivation du bouton cliqué
    submitButton.disabled = true;
    submitButton.value = "Traitement en cours...";

    formData = new FormData(form);
    data = new URLSearchParams();
    for (const [key, val] of formData.entries()) {
        data.append(key, val);
    }

    if(subType !== "Modification_role"){
        clearError(style,type,subType);
    }

    fetch( "/Stock" + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: data.toString()
    })
        .then(response => {
            console.log(response);
            console.log(data);

            if (response.ok) {
                form.reset();
                if(subType !== "Modification_role"){
                    clearError(style,type,subType);
                }
                window.location.href = "/Stock" + nextUrl ;
            } else {
                return response.text().then(text => {
                    const erreurs = text.split('\n').filter(line => line.trim() !== '');
                    putError(erreurs, style, type, subType);
                });
            }
            //
        })
        .catch(error => {
            alert('Erreur autre : ' + error.message);
        })
        .finally(() => {
            submitButton.disabled = false;
            submitButton.textContent = "Confirmer";
        });
}

function removeData(data, type){
    console.log("🧩 removeData() appelé avec:", data, type)
    let url, nextUrl, tag;
    switch (type){
        case "Article":
        {
            url = "/DeleteArticleServlet";
            nextUrl = "/Articles-Types/Articles";
            tag = "tag_article";
            break;
        }

        case "Employe":
        {
            url = "/DeleteEmployeServlet";
            nextUrl = "/Employes";
            tag = "matricule";
            break;
        }

        case "Fournisseur":
        {
            url = "/DeleteFournisseurServlet";
            nextUrl = "/Fournisseurs";
            tag = "tag_fournisseur";
            break;
        }

        case "Type":
        {
            url = "/DeleteTypeServlet";
            nextUrl = "/Articles-Types/Types";
            tag = "tag_type";
            break;
        }
        default: break;
    }

    let params = new URLSearchParams();
    params.append(tag, data);

    fetch( "/Stock" + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
        .then(response => {
            console.log(response);
            console.log(data);
                if (response.ok) {
                    alert("Suppression réussi !");
                    window.location.href = "/Stock" + nextUrl ;
                } else {
                    return response.text().then(text => {
                        const erreurs = text.split('\n').filter(line => line.trim() !== '');
                        for (const err of erreurs) {
                            alert("Erreur : " + err);
                        }
                        // putError(erreurs, type, subType);
                    });
                }
        })
        .catch(error => {
            alert('Erreur autre : ' + error.message);
        })
}

function getChatStorageKey() {
    const CHAT_STORAGE_KEY_PREFIX = 'chat_history_';
    const matricule = window.currentMatricule;
    if (!matricule || matricule === "null" || matricule === "undefined") {
        console.error("Matricule non défini. L'historique ne sera pas isolé par utilisateur.");
        return CHAT_STORAGE_KEY_PREFIX + 'generic';
    }
    return CHAT_STORAGE_KEY_PREFIX + currentMatricule;
}

function getChatHistoryFromSession() {
    try {
        const key = getChatStorageKey();
        const historyJson = sessionStorage.getItem(key);
        return JSON.parse(historyJson || '[]');
    } catch (e) {
        console.error("Erreur de lecture du sessionStorage", e);
        return [];
    }
}

// -----------------------------------------------------------------
// FONCTIONS COMMUNES D'AFFICHAGE ET D'UX (MODIFIÉES)
// -----------------------------------------------------------------

/**
 * Ajoute un message à la boîte de conversation ET au sessionStorage.
 * @param {string} sender - 'user' ou 'bot'.
 * @param {string} text - Le contenu du message.
 */
function appendMessage(sender, text) {
    const messageDiv = document.getElementById('message');
    if (!messageDiv) return;

    // 1. Mise à jour du DOM (affichage)
    const p = document.createElement('p');
    p.className = sender === 'user' ? 'user-msg' : 'bot-msg';
    const formattedText = text.replace(/\n/g, '<br>').replace(/\r/g, '');
    p.innerHTML = "<b>" + (sender === 'user' ? 'Vous' : 'Bot') + ":</b> " + formattedText;
    messageDiv.appendChild(p);

    // Scroller vers le bas
    messageDiv.scrollTop = messageDiv.scrollHeight;
    let history = getChatHistoryFromSession();
    history.push({
        id: Date.now(),
        type: sender,
        content: text,
        time: new Date().toISOString()
    });
    sessionStorage.setItem(getChatStorageKey(), JSON.stringify(history));
}

/**
 * Gère l'envoi de la question au serveur via AJAX (Fetch API).
 * (Aucun changement dans la logique d'envoi, seulement dans appendMessage)
 */
function submitQuestion(contextPath) {
    event.preventDefault();

    const form = document.getElementById("chatService");
    const formData = new FormData(form);
    const data = new URLSearchParams();
    const questionValue = formData.get("question");

    if (questionValue === null || questionValue.trim() === "") {
        alert("Veuillez saisir une question.");
        return;
    }
    data.append("question", questionValue);

    // 1. Afficher le message de l'utilisateur (Sauvegarde via appendMessage)
    appendMessage('user', questionValue);

    form.reset();
    const submitButton = document.querySelector('.submit_chat');
    submitButton.disabled = true;

    // 2. Envoyer la requête AJAX
    fetch(contextPath + "/ChatServlet", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: data.toString(),
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            if (data.statut === 'OK' && data.reponse) {
                appendMessage('bot', data.reponse); // Sauvegarde via appendMessage
            } else if (data.statut === 'ERREUR' && data.reponse) {
                appendMessage('bot', `Erreur: ${data.reponse}`);
            } else if (data.statut === 'ERREUR_INVALIDE' && data.reponse) {
                appendMessage('bot', `${data.reponse}`);
            } else {
                appendMessage('bot', `Je n'ai pas pu obtenir de réponse.`);
            }
        })
        .catch(error => {
            console.error('Erreur lors de l\'envoi de la requête:', error);
            appendMessage('bot', `Désolé, une erreur de communication est survenue: ${error.message}`);
        })
        .finally(() => {
            submitButton.disabled = false;
        });
}

/**
 * Charge l'historique du chat depuis sessionStorage et l'affiche.
 * Fonction appelée au chargement de la page Chatbot.jsp.
 */
function loadChatHistoryFromSession() {
    const messageDiv = document.getElementById('message');
    if (!messageDiv) return;

    const history = getChatHistoryFromSession();

    messageDiv.innerHTML = ''; // Vider le contenu actuel

    if (history.length > 0) {
        history.forEach(chat => {
            const p = document.createElement('p');
            p.className = chat.type === 'user' ? 'user-msg' : 'bot-msg';
            const formattedText = chat.content.replace(/\n/g, '<br>').replace(/\r/g, '');
            p.innerHTML = "<b>" + (chat.type === 'user' ? 'Vous' : 'Bot') + ":</b> " + formattedText;
            messageDiv.appendChild(p);
        });
    }

    messageDiv.scrollTop = messageDiv.scrollHeight;
}