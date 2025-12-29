const ctx = "/StockIT";
let wsInstance = null;
let currentFormController = null;

// Fonction de mise à jour du menu en temps réel après un changement de rôle (Étape 2)
function updateNavigationMenu(newRoleName) {
    console.log(`Mise à jour du menu par WebSocket pour le rôle : ${newRoleName}`);
    
    // Assurez-vous d'utiliser la variable globale ctx
    const contextPath = ctx; 
    
    const isAdmin = newRoleName <= 2;
    
    const employeMenuDiv = document.querySelector('a[href*="/Employes"]').parentElement;
    const fournisseurMenuDiv = document.querySelector('a[href*="/Fournisseurs"]').parentElement;
	//const entreeMenuDiv = document.querySelector('a[href*="/Entrees/Creation"]').parentElement;
	//const sortieMenuDiv = document.querySelector('a[href*="/Sorties/Creation"]').parentElement;
	//const articleCreateMenuDiv = document.querySelector('a[href*="/Articles/Creation"]').parentElement;
	//const typeCreateMenuDiv = document.querySelector('a[href*="/Types/Creation"]').parentElement;

    if (employeMenuDiv && fournisseurMenuDiv) {
        employeMenuDiv.style.display = isAdmin ? 'block' : 'none';
        fournisseurMenuDiv.style.display = isAdmin ? 'block' : 'none';
		// entreeMenuDiv.style.display = isAdmin ? 'flex' : 'none';
		// sortieMenuDiv.style.display = isAdmin ? 'flex' : 'none';
		// articleCreateMenuDiv.style.display = isAdmin ? 'flex' : 'none';
		// typeCreateMenuDiv.style.display = isAdmin ? 'flex' : 'none';
		
        
        // Sécurité: Rediriger si l'utilisateur perd ses droits sur une page admin
        if (!isAdmin) {
			if( window.location.pathname.includes("/Employes") ) { alert("Accès refusé. Vous ne pouvez plus accéder aux Employés. Redirection vers les Articles."); window.location.href = contextPath + "/Acceuil";}
            if( window.location.pathname.includes("/Fournisseurs") ) { alert("Accès refusé. Vous ne pouvez plus accéder aux Fournisseurs. Redirection vers les Articles."); window.location.href = contextPath + "/Acceuil";}
			if( window.location.pathname.includes("/Entrees/Creation") ) { alert("Accès refusé. Vous ne pouvez plus créer des Entrées d'articles. Redirection vers les Articles."); window.location.href = contextPath + "/Entrees"; }
			if( window.location.pathname.includes("/Sorties/Creation") ) { alert("Accès refusé. Vous ne pouvez plus créer des Sorties d'articles. Redirection vers les Articles."); window.location.href = contextPath + "/Sorties"; }
			if( window.location.pathname.includes("/Articles/Creation") ) { alert("Accès refusé. Vous ne pouvez plus créer des Articles. Redirection vers les Articles."); window.location.href = contextPath + "/Articles"; }
			if( window.location.pathname.includes("/Types/Creation") ){ alert("Accès refusé. Vous ne pouvez plus créer des Types d'articles. Redirection vers les Articles."); window.location.href = contextPath + "/Types"; }
			if( window.location.pathname.includes("/Articles/Modification") ) { alert("Accès refusé. Vous ne pouvez plus modifier des Articles. Redirection vers les Articles."); window.location.href = contextPath + "/Articles"; }
			if( window.location.pathname.includes("/Types/Modification") ) { alert("Accès refusé. Vous ne pouvez plus modifier des Types d'articles. Redirection vers les Articles."); window.location.href = contextPath + "/Types";} 
        }
    } else {
        console.warn("Éléments de menu Employés/Fournisseurs non trouvés pour la mise à jour du DOM.");
    }
}


function initEmployeWebSocket(contextPath, m, r) {

	   const matricule = m || sessionStorage.getItem("ws_matricule");
	   const role = r || sessionStorage.getItem("ws_role");

	   if (!matricule || !role) {
	       console.warn("WebSocket non initialisé : infos manquantes");
	       return;
	   }

	   // 🔐 Persistance
	   sessionStorage.setItem("ws_matricule", matricule);
	   sessionStorage.setItem("ws_role", role);

	   // 🚫 Anti double connexion
	   if (wsInstance && wsInstance.readyState === WebSocket.OPEN) {
	       console.warn("WebSocket déjà actif");
	       return;
	   }

   const protocol = location.protocol === "https:" ? "wss://" : "ws://";
   const wsUrl = protocol + location.host + contextPath +
       "/EmployeLog/" + matricule + "/" + role;


    let params;

    function initWebSocket() {
        wsInstance = new WebSocket(wsUrl);

        wsInstance.onopen = () => {
            console.log("✅ WebSocket connecté pour l'employé :", matricule);
        };

        wsInstance.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                
                // 🛑 GESTION DU CHANGEMENT DE RÔLE (Mise à jour en temps réel du menu)
                if (data.type === "role_updated") {
                    
		            const newRoleInt = data.role_int;
		            const newRoleName = data.role_name;

                    // Mettre à jour la session storage pour la prochaine connexion/reconnexion
		            sessionStorage.setItem("ws_role", newRoleInt);
		            
		            // Mise à jour du menu (Appel de la fonction DOM)
		            updateNavigationMenu(newRoleInt); 
		            
		            alert(data.message || `Votre rôle a été mis à jour à : ${newRoleName}`);
		        }
                
                // ... (votre logique existante pour refresh_data) ...
                if (data.type === "refresh_data") {
                    console.log("🔄 Rafraîchissement des données demandé :", data.message);
                    // Selon la page active, on relance la fonction appropriée :
                    if (window.location.pathname.includes("/Articles")) {
                        searchArticle();
                    } else if (window.location.pathname.includes("/Employes")) {
                        searchEmploye();
                    } else if (window.location.pathname.includes("/Fournisseurs")) {
                        searchFournisseur();
                    } else if (window.location.pathname.includes("/Types")) {
                        searchType();
                    } else if (window.location.pathname.includes("/Entrees")) {
                        searchFlux("Entree");
                    } else if (window.location.pathname.includes("/Sorties")) {
                        searchFlux("Sortie");
                    }
                }
                
                // ... (votre logique existante pour force_logout) ...
                if (data.type === "force_logout") {
                    params = new URLSearchParams({
                        activite : "desactivate"
                    })
					
					if (currentFormController) {
                        currentFormController.abort();
                        console.log("Flux de formulaire interrompu pour déconnexion.");
                    }

                    alert(data.message || "Votre compte a été désactivé par l'administrateur.");

                    fetch(contextPath + "/ForceLogout?" + params.toString())
                        .then(res => {
                            if (!res.ok) throw new Error("Erreur de logout serveur");
                            return res.text();
                        })
                        .then(() => {
                            wsInstance.send(JSON.stringify({
                                type: "passage_logout",
                                message: "Ok to log out"
                            }));
                        })
                        .finally(() => {
                            window.location.href = contextPath + "/Connexion";
                        });
                }
                
                // ... (votre logique existante pour notify_decision, notify_info, modify_role_info) ...
                console.log(data.message + " " + data.type);
                if (data.type === "notify_decision") {
                    if(confirm(data.message + "\n" + "Accepteririez - vous ce nouveau compte")){
                        wsInstance.send(JSON.stringify({
                            type: "accept_admin",
                            message: "l'admin a accepté l'employé concerné en tant que nouvel utilisateur",
                            matricule: data.matricule
                        }));
                    }
                    else{
                        wsInstance.send(JSON.stringify({
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

        wsInstance.onclose = (event) => {
			if (event.code !== 1000) { 
                setTimeout(initWebSocket, 5000);
            }
        };

        wsInstance.onerror = (err) => {
            console.error("⚠️ Erreur WebSocket :", err);
        };
    }

    initWebSocket();
}

function closeEmployeWebSocket(force = false, contextPath = '/StockIT') {
    
    // 🛑 CORRECTION: Se déclenche si appelé par pagehide (force=true) ou déconnexion explicite
    if (!force) return; 

    if (wsInstance && wsInstance.readyState === WebSocket.OPEN) {
        wsInstance.close(1000, "Déconnexion utilisateur.");
        console.log("🚀 WebSocket fermé volontairement.");
    }

    if (navigator.sendBeacon) {
		const matricule = sessionStorage.getItem("ws_matricule");
		const data = new URLSearchParams();
		data.append("matricule", matricule);
        
        const logoutUrl = contextPath + "/LogoutServlet"; 
        
        // Utilisation de POST via sendBeacon pour le nettoyage en BDD
        const beaconSent = navigator.sendBeacon(logoutUrl, data);
        
        if (beaconSent) {
            console.log("📡 SendBeacon mis en file d'attente pour l'URL: " + logoutUrl);
        } else {
            console.error("🔴 SendBeacon : Échec de la mise en file d'attente.");
        }
    }

    sessionStorage.removeItem("ws_matricule");
    sessionStorage.removeItem("ws_role");
}

function closeForm(){
	const formPath = window.location.pathname;
	const form = document.querySelector("form");
	
	if(currentFormController){
		currentFormController.abort();		
	}
	
	let nextPath;
	if(formPath.includes("/Entrees/Creation")) nextPath = ctx + "/Entrees";
	if(formPath.includes("/Sorties/Creation")) nextPath = ctx + "/Sorties";
	if(formPath.includes("/Profil/Modification")) nextPath = ctx + "/Profil";
	if(formPath.includes("/Fournisseurs/Creation") || formPath.includes("/Fournisseurs/Modification")) nextPath = ctx + "/Fournisseurs";
	if(formPath.includes("/Articles/Creation") || formPath.includes("/Articles/Modification")) nextPath = ctx + "/Articles";
	if(formPath.includes("/Types/Creation") || formPath.includes("/Types/Modification"))  nextPath = ctx + "/Types";
	
	if(nextPath){
		if(confirm("Êtes-vous sûr de vouloir quitter le formulaire ? Les données non enregistrées seront perdues.")){
			if(form){
				form.reset();
			}
			window.location.href = nextPath;
		}
	}
}

function logOut(contextPath, matricule) {
    
	if(currentFormController) {
	    currentFormController.abort();
	}
		
	currentFormController = new AbortController();
	const signal = currentFormController.signal;
		
    fetch(contextPath + "/LogoutServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            matricule: matricule
        }),
		signal: signal
    })
        .then(res => {
            if (!res.ok) throw new Error("Échec de la déconnexion HTTP.");
            alert("Déconnexion pour l'employé " + matricule + " est terminée");
            window.location.href = contextPath + "/Connexion";
        })
        .catch(err => {
            console.error("ErrorLogOut:", err);
            window.location.href = contextPath + "/Connexion";
        });
}


/**
 * Gère la navigation interne sans recharger la page complète (maintient le WS actif).
 * @param {string} url - L'URL du contenu à charger (ex: /StockIT/Articles).
 */
function navigateTo(url) {
    const currentContentDiv = document.querySelector(".content");
    
    // Mise à jour visuelle et de l'URL
    currentContentDiv.innerHTML = "<p class='loading-indicator'>Chargement en cours...</p>";
    history.pushState(null, '', url); 
    
    fetch(url)
        .then(res => {
            if (!res.ok) throw new Error("Erreur de chargement de la page : " + res.status);
            return res.text();
        })
        .then(html => {
            // Créer un div temporaire pour trouver le nouveau contenu
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            
            // On extrait la DIV de contenu du HTML de la réponse
            const newContent = tempDiv.querySelector(".content");

            if (newContent) {
                currentContentDiv.innerHTML = newContent.innerHTML;
                updateActiveClass(url); 
                console.log(`Contenu de ${url} chargé (Soft Navigation).`);
                
                // --- Ré-exécution des scripts et fonctions spécifiques ---
                // Si votre contenu dynamique contient des scripts (event listeners, initialisations)
                // vous devrez les ré-exécuter manuellement ici après l'injection.
            } else {
                currentContentDiv.innerHTML = "<p>Erreur: Contenu non trouvé dans la réponse.</p>";
            }
        })
        .catch(err => {
            console.error("Erreur de navigation asynchrone:", err);
            currentContentDiv.innerHTML = `<p>Erreur lors du chargement de la page: ${url}</p>`;
        });
}

/**
 * Met à jour les classes 'active' et 'active-sub' dans la barre de navigation.
 * @param {string} currentUrl - L'URL actuelle.
 */
function updateActiveClass(currentUrl) {
    // Nettoyer toutes les classes actives
    document.querySelectorAll('nav a').forEach(link => {
        link.classList.remove('active', 'active-sub');
    });

    // Trouver et activer le nouveau lien
    document.querySelectorAll('nav a').forEach(link => {
        // Normaliser les chemins pour la comparaison
        const linkHref = link.getAttribute('href').replace(ctx, '').toLowerCase();
        const path = new URL(currentUrl).pathname.replace(ctx, '').toLowerCase();

        if (path === linkHref) {
            if (link.closest('.dropdown-content')) {
                // Si c'est un sous-lien, active-sub et ouvrir le parent
                link.classList.add('active-sub');
                link.closest('.dropdown-menu').classList.add('open');
            } else {
                // Si c'est un lien principal
                link.classList.add('active');
            }
        }
    });
}

function toggleIcon(buttonElement, isHovering) {
    // 1. Trouver l'icône Font Awesome à l'intérieur du bouton
    const iconElement = buttonElement.querySelector('i');

    if (iconElement) {
        if (isHovering) {
            // AU SURVOL (isHovering est true) : Afficher l'œil ouvert
            iconElement.classList.remove('fa-eye-slash'); // Supprime l'œil barré
            iconElement.classList.add('fa-eye');         // Ajoute l'œil ouvert
        } else {
            // LORSQUE LA SOURIS QUITTE (isHovering est false) : Revenir à l'œil barré
            iconElement.classList.remove('fa-eye');      // Supprime l'œil ouvert
            iconElement.classList.add('fa-eye-slash');   // Ajoute l'œil barré
        }
    }
}								

function searchArticle() {
    const article = document.querySelector('input[name="nom_article"]').value;
    const type = document.querySelector('input[name="nom_type"]').value;

    const params = new URLSearchParams();
    if(article.trim() !== "") params.append("nom_article", article);
    if(type.trim() !== "") params.append("nom_type", type);

    const currentBody = document.querySelector("#result_article");
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch(ctx+"/Articles?" + params.toString())
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

    fetch(ctx+"/Employes?" + params.toString())
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

    fetch(ctx+"/Fournisseurs?" + params.toString())
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

    fetch(ctx+"/Types?" + params.toString())
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
	
	if(type.trim() !== "") {
		
		if(type.trim() === "Sortie"){
			const destinataireInput = document.getElementById("destinataire").value;
			if(destinataireInput.trim() !== "") destinataire = destinataireInput;
		}
		const expediteurInput = document.getElementById("expediteur").value; 
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

        fetch(ctx + (type.trim() === "Entree" ? "/Entrees" : "/Sorties") + "?" + data.toString())
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

/**
 * Bascule l'affichage du contenu d'un fieldset pour créer un effet accordéon.
 * @param {HTMLElement} element - L'élément cliquable (ex: le <legend>) qui déclenche le basculement.
 */
function toggleFieldset(element, type) {
    // 1. Trouver le parent <fieldset>
    const fieldset = element.closest('fieldset');
    if (!fieldset) return; // Sécurité
	
	if(type == null || type === "") return;

    // 2. Trouver le contenu masquable/affichable
    const content = fieldset.querySelector('.fieldset-content');

    // 3. Basculer l'affichage
    if (content) {
        if (content.style.display === "none" || content.style.display === "") {
            content.style.display = type == "date" ? "block" : "flex" ; // Afficher
            // Optionnel: Mettre à jour l'icône
            element.querySelector('.fas').className = 'fas fa-caret-up';
        } else {
            content.style.display = "none"; // Masquer
            // Optionnel: Mettre à jour l'icône
            element.querySelector('.fas').className = 'fas fa-caret-down';
        }
    }
}

function setUpdateArticle(tag) {
    if (tag.trim() !== "") {
        console.log(tag)
        window.location.href = ctx + "/Articles-Types/SessionModifyArtType.jsp?section=Article&id=" + encodeURIComponent(tag);
    }
}

function setUpdateFournisseur(tag){
    const params = new URLSearchParams();
    if(tag.trim() !== ""){
        window.location.href = ctx + "/Fournisseur/SessionModifyFournisseur.jsp?id=" + encodeURIComponent(tag);
    }
}

function setUpdateType(tag) {
    if (tag.trim() !== "") {
        window.location.href = ctx + "/Articles-Types/SessionModifyArtType.jsp?section=Type&id=" + encodeURIComponent(tag);
    }
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
	
	if(currentBody.style.display === "none") currentBody.style.display = "flex";
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch(ctx+"/Entrees/Creation?" + params.toString())
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
	
	if(currentBody.style.display === "none") currentBody.style.display = "flex";
    currentBody.innerHTML = "<p>Recherche en cours...</p>";

    fetch(ctx + "/Sorties/Creation?" + params.toString())
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
                    resetFields([
						"dialog_nom_article", 
						"dialog_type_article", 
						"dialog_nombre_article", 
						"dialog_tag_article"
					]);
                }
                break;
			case "Article-Info":
                dialog = document.getElementById("dialog_info_article");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_nom_article_info").textContent = data.dataset.nom_article;
					document.getElementById("dialog_stock_actuel_article").textContent = "Stock actuel: " + data.dataset.stock_article;
					document.getElementById("dialog_consommation_moyen_article").textContent = "Consommation moyen journalière: " + data.dataset.cmd_article;
					document.getElementById("dialog_delai_reapprovisionnement_article").textContent = "Delai inter-réception moyen: " + data.dataset.dirm_article;
					document.getElementById("dialog_seuil_critique_article").textContent = "Quantité à seuil critique: " + data.dataset.seuil_stock_article;
					document.getElementById("dialog_situation_article").textContent = "Situation du stock: " + data.dataset.situation_article;
					document.getElementById("dialog_entree_article").textContent = data.dataset.derniere_entree_article;
					document.getElementById("dialog_sortie_article").textContent = data.dataset.derniere_sortie_article;
				} else if (action === 'Close') {
                    resetFields([
						"dialog_nom_article_info",
						"dialog_stock_actuel_article",
						"dialog_consommation_moyen_article",
						"dialog_delai_reapprovisionnement_article",
						"dialog_seuil_critique_article",
						"dialog_situation_article",
						"dialog_entree_article",
						"dialog_sortie_article" 
					]);
                }
                break;

            case "Destinataire-Employe":
                dialog = document.getElementById("dialog_destinataire");
                if (action === 'Show' && data) {
                    document.getElementById("dialog_destinataire_nom_complet").textContent = data.dataset.nom + " " + data.dataset.prenom;
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
				const currentMatricule = sessionStorage.getItem("ws_matricule");
				const currentRole = sessionStorage.getItem("ws_role");
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
                    if( data.dataset.role === "Administrateur" || (currentRole == 2 && data.dataset.role === "Sous Administrateur") || data.dataset.matricule === currentMatricule){
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
			
			case "List-Articles":
                dialog = document.getElementById("dialog_list_articles");
                break;
				
			case "List-Expediteurs":
                dialog = document.getElementById("dialog_list_expediteurs");
                break;
				
			case "List-Destinataires":
                dialog = document.getElementById("dialog_list_destinataires");
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

            setForm(event, 'Modification', 'Employe', 'Modification_total');
        };

        dialog.querySelector("#cancelBtn").onclick = function(e) {
            e.preventDefault();
            dialog.close();
        };

    } else {
        setForm(event, 'Modification', 'Employe', 'Modification_total');
    }
}

function setTag(e,type,subType,tag,name){
    if (e) {
        e.preventDefault();
        if (type.trim() === "Entree") {
            switch (subType) {
                case "Article": {
                    document.getElementById("tag_article").value = tag;
					document.getElementById("selected_article_tag").style.display = "inline-block"
					document.getElementById("selected_article_tag").textContent = "Vous avez sélectionné l'article: " + name +".";
                    break;
                }
                case "Fournisseur": {
                    document.getElementById("tag_fournisseur").value = tag;
					document.getElementById("selected_expediteur_tag").style.display = "inline-block"
					document.getElementById("selected_expediteur_tag").textContent = "Vous avez sélectionné le fournisseur: " + name +".";
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
					document.getElementById("selected_article_tag").style.display = "inline-block"
					document.getElementById("selected_article_tag").textContent = "Vous avez sélectionné l'article: " + name +".";
                    break;
                }
                case "Expediteur": {
                    document.getElementById("expediteur").value = tag;
                    break;
                }
                case "Destinataire": {
                    document.getElementById("destinataire").value = tag;
					document.getElementById("selected_destinataire_tag").style.display = "inline-block"
					document.getElementById("selected_destinataire_tag").textContent = "Vous avez sélectionné le destinataire: " + name +".";
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
            url = ctx + "/RapportExcel" + (type.trim() === "Entree" ? "/Entrees" : "/Sorties") + "?" + data.toString();
            window.location.href = url;
        }
    }
}

function setForm(e, style, type, subType){
    if(e != null) e.preventDefault();
    let form, submitButton, formData, data, url, nextUrl;
	
    if (currentFormController) {
        currentFormController.abort();
    }

    // Créer un nouveau contrôleur pour cette requête
    currentFormController = new AbortController();
    const signal = currentFormController.signal;
	
	let dialog;

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
            nextUrl = "/Articles";
            break;
        }

        case "Employe":
        {
            if(subType.trim() === "Modification_total")
            {
                form = document.getElementById(formPrefix + 'Form_employe');
                submitButton = form.querySelector('.submit_employe');
				dialog = document.getElementById("passwordDialog");
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
			else if(subType.trim() === "Ajout")
			{
				form = document.getElementById(formPrefix + 'Form_employe');
                submitButton = form.querySelector('.submit_employe');
                url = "/"+ urlPrefix + "EmployeServlet";
                nextUrl = "/Connexion";
				console.log("url: " + url + ", nextUrl: " + nextUrl);
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
                nextUrl = "/Entrees";
            }
            if(subType.trim() === "Sortie"){
                form = document.getElementById(formPrefix + 'Form_sortie');
                submitButton = form.querySelector('.submit_sortie');
                url = "/"+ urlPrefix + "SortieServlet";
                nextUrl = "/Sorties";
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
            nextUrl = "/Types";
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

    fetch( ctx + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: data.toString(),
		signal: signal
    })
        .then(response => {
            console.log(response);
            console.log(data);

            if (response.ok) {
                form.reset();
                if(subType !== "Modification_role"){
                    clearError(style,type,subType);
                }
				if(subType === "Modification_total")
				{
					dialog.close();				
				}
                window.location.href = ctx + nextUrl ;
            } else {
                return response.text().then(text => {
                    const erreurs = text.split('\n').filter(line => line.trim() !== '');
                    putError(erreurs, style, type, subType);
                });
            }
            //
        })
        .catch(error => {
            if (error.name === 'AbortError') {
                console.warn('Requête annulée par une nouvelle soumission de formulaire.');
			}
			else{
				console.error("Fetch error:", error.message);
	            alert('Erreur autre : ' + error.message);	
			}
        })
        .finally(() => {
			currentFormController = null;
            submitButton.disabled = false;
            submitButton.textContent = "Confirmer l'action";
        });
}

function removeData(data, type){
    console.log("🧩 removeData() appelé avec:", data, type)
    let url, nextUrl, tag;
	
    if (currentFormController) {
	    currentFormController.abort();
	}
	
	currentFormController = new AbortController();
	const signal = currentFormController.signal;
	
    switch (type){
        case "Article":
        {
            url = "/DeleteArticleServlet";
            nextUrl = "/Articles";
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
            nextUrl = "/Types";
            tag = "tag_type";
            break;
        }
        default: break;
    }

    let params = new URLSearchParams();
    params.append(tag, data);

    fetch( ctx + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString(),
		signal: signal
    })
        .then(response => {
            console.log(response);
            console.log(data);
                if (response.ok) {
                    alert("Suppression réussi !");
                    window.location.href = ctx + nextUrl ;
                } else {
                    return response.text().then(text => {
                        const erreurs = text.split('\n').filter(line => line.trim() !== '');
                        for (const err of erreurs) {
                            alert("Erreur : " + err);
                        }
                    });
                }
        })
        .catch(error => {
			if(error.name === 'AbortError')
			{
				console.warn('Requête annulée par une nouvelle soumission de formulaire.')	
			}
            alert('Erreur autre : ' + error.message);
        })
}

/**
   * Ajoute un message à la boîte de conversation (Côté client uniquement).
   */
  function appendMessage(sender, text) {
      const messageDiv = document.getElementById('message');
      if (!messageDiv) return;

      const p = document.createElement('p');
      // sender est ici en minuscule ('user' ou 'bot') pour JS
      p.className = sender === 'user' ? 'user-msg' : 'bot-msg'; 
      const formattedText = text.replace(/\n/g, '<br>').replace(/\r/g, ''); 
      p.innerHTML = "<b>" + (sender === 'user' ? 'Vous' : 'Bot') + ":</b> " + formattedText;
      messageDiv.appendChild(p);

      // Scroller vers le bas
      messageDiv.scrollTop = messageDiv.scrollHeight;
  }

  /**
   * Gère l'envoi de la question au serveur via AJAX (Fetch API).
   * Intègre la correction pour la gestion de l'état du bouton.
   */
  function submitQuestion(contextPath) {
      event.preventDefault();

      const form = document.getElementById("chatService");
      const formData = new FormData(form);
      const data = new URLSearchParams();
      const questionValue = formData.get("question");
      
      const submitButton = document.querySelector('.submit_chat');
	  
	  if(currentFormController)
		{
			currentFormController.abort();
		}
		
	  currentFormController = new AbortController();
	  const signal = currentFormController.signal;

      if (questionValue === null || questionValue.trim() === "") {
          alert("Veuillez saisir une question.");
          return;
      }
      
      // Désactiver le bouton au début de la soumission
      if (submitButton) {
          submitButton.disabled = true;
      }

      data.append("question", questionValue);

      // 1. Afficher le message de l'utilisateur immédiatement
      appendMessage('user', questionValue);

      form.reset();

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
                  // Si le serveur retourne un code d'erreur HTTP (4xx, 5xx)
                  return response.text().then(text => { throw new Error(text) });
              }
              return response.json();
          })
          .then(data => {
              if (data.statut === 'OK' && data.reponse) {
                  appendMessage('bot', data.reponse); // Afficher la réponse du bot
              } else if (data.statut && data.reponse) {
                  // Gérer les erreurs de logique métier (ErrorConfirmException)
                  appendMessage('bot', `[${data.statut}] ${data.reponse}`);
              } else {
                  appendMessage('bot', `Je n'ai pas pu obtenir de réponse.`);
              }
          })
          .catch(error => {
			if(error.name !== 'AbortError'){
			  console.error('Erreur lors de l\'envoi de la requête:', error);
              // Afficher l'erreur si la communication a échoué
              appendMessage('bot', `Désolé, une erreur de communication est survenue: ${error.message}`);	
			}
          })
          .finally(() => {
              // Réactiver le bouton de soumission, quoi qu'il arrive
              if (submitButton) {
                  submitButton.disabled = false;
              }
          });
  }