package modele.facade;

import modele.elements.*;

import modele.elements.actions.IAction;
import modele.elements.actions.construire_une_station.ConstruireUneStation;
import modele.elements.actions.decouvrir_remede.DecouvrirRemede;
import modele.elements.actions.deplacement.DeplacementNavette;
import modele.elements.actions.deplacement.DeplacementVoiture;
import modele.elements.actions.deplacement.DeplacementVolCharter;
import modele.elements.actions.deplacement.DeplacementVolDirect;
import modele.elements.actions.partager_connaissance.DonnerConnaissance;
import modele.elements.actions.partager_connaissance.PrendreConnaissance;
import modele.elements.actions.planificateur_urgence.EntreposerEvenementRolePlanificateur;
import modele.elements.actions.traiter_maladie.TraiterMaladie;
import modele.elements.cartes.CarteEpidemie;
import modele.elements.cartes.CarteEvenement;
import modele.elements.cartes.CarteJoueur;
import modele.elements.cartes.CartePropagation;
import modele.elements.cartes.CarteVille;
import modele.elements.cartes.evenements.*;
import modele.elements.cartes.roles.*;
import modele.elements.enums.CouleurPionsRole;
import modele.elements.enums.EtatVirus;
import modele.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacadePandemic9ImplTest {

    private FacadePandemic9Impl instance;
    private PionJoueur pionJoueur;
    private PionJoueur pionJoueur2;
    private Ville atlanta;
    private Ville chicago;
    private Ville paris;
    private Ville alger;
    private Ville milan;
    private Ville tokyo;
    private Ville istanbul;
    private Ville miami;
    private Plateau plateau;


    @BeforeEach
    void setUp() throws RoleIntrouvableException, VilleIntrouvableException, EvenementInnexistantException, VirusIntrouvableException, FileNotFoundException {
        instance = new FacadePandemic9Impl();
        instance.creerPartieQuatreJoueurs("1234abcd");
        plateau = instance.partie.getPlateau();
        pionJoueur = instance.partie.getJoueurActuel();
        pionJoueur2 = instance.partie.accesJoueurSuivant();
        atlanta = plateau.getVilleByName("Atlanta");
        chicago = plateau.getVilleByName("Chicago");
        paris = plateau.getVilleByName("Paris");
        alger = plateau.getVilleByName("Alger");
        milan = plateau.getVilleByName("Milan");
        tokyo = plateau.getVilleByName("Tokyo");
        istanbul = plateau.getVilleByName("Istanbul");
        miami = plateau.getVilleByName("Miami");


        // trop de conflit avec les initialisations des cartes distribu?? aux joueurs cr????s et les assert/throw qui check si
        // le joueur poss??dent la carte dite
        // donc on vide les deck des deux joueurs qui poss??dent chacun 2 cartes ?? la base pour une partie de 4.
        pionJoueur.getDeckJoueur().remove(0);
        pionJoueur.getDeckJoueur().remove(0);
        pionJoueur2.getDeckJoueur().remove(0);
        pionJoueur2.getDeckJoueur().remove(0);
    }

    @Test
    void truc(){

    }


    @Test
    void creationPartie4Joueurs(){
        Assertions.assertDoesNotThrow(()-> this.instance.creerPartieQuatreJoueurs("1234abcd"));
        // on verifie que l'on a bien 4 pionsJoueur creer dans la partie
        assertEquals(4,instance.partie.getJoueurs().size());
        // que les 4 cartes roles ont bien ??t?? distribu?? parmis les 7 pr??sente dans le plateau de base lors de la cr??ation
        assertEquals(3,instance.partie.getPlateau().getToutesLesCartesRolesExistante().size());
        // que la piocheCartePropagation contient 48 cartes - les 9 retourn?? ?? l'initialisation du jeu
        assertEquals(39,instance.partie.getPlateau().getPiocheCartePropagation().size());
    }


//=============================================================================================================================
//                                                ACTION DeplacementVoiture
//=============================================================================================================================

//------------ 1- Tests jouerAction() avec l'action DeplacementVoiture OK
    @Test
    void jouerTourActionDeplacementVoitureOK() {
        IAction action = new DeplacementVoiture(chicago);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action));
        assertEquals(pionJoueur.getVilleActuelle(), chicago);
    }

//------------ 2- Tests jouerAction() avec l'action DeplacementVoiture KO
    @Test
    void jouerTourActionDeplacementVoitureVilleDestinationEstVilleActuelle() {
        IAction action = new DeplacementVoiture(atlanta);
        Assertions.assertThrows(VilleDestinationEstVilleActuelleException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementVoitureNbTourMaxAtteint() {
        IAction action = new DeplacementVoiture(chicago);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action));
        IAction action1 = new DeplacementVoiture(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementVoiture(chicago);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementVoiture(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 = new DeplacementVoiture(chicago);
        Assertions.assertThrows(NbActionsMaxTourAtteintException.class,
                () -> this.instance.jouerAction(pionJoueur,action4));
    }

    @Test
    void jouerTourActionDeplacementVoitureVilleIntrouvable() {
        IAction action = new DeplacementVoiture(new Ville("Introuvable"));
        Assertions.assertThrows(VilleIntrouvableException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementVoitureVilleNonVoisine() {
        IAction action = new DeplacementVoiture(paris);
        Assertions.assertThrows(VilleNonVoisineException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

//=============================================================================================================================
//                                                ACTION DeplacementVolDirect
//=============================================================================================================================

//------------ 1- Tests jouerAction() avec l'action DeplacementVolDirect OK
    @Test
    void jouerTourActionDeplacementVolDirectOK() {
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        IAction action = new DeplacementVolDirect(paris);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action));
        assertEquals(pionJoueur.getVilleActuelle(), paris);
    }

//------------ 2- Tests jouerAction() avec l'action DeplacementVolDirect KO
    @Test
    void jouerTourActionDeplacementVolDirectVilleDestinationEstVilleActuelle() {
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new DeplacementVolDirect(atlanta);
        Assertions.assertThrows(VilleDestinationEstVilleActuelleException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementVolDirectVilleIntrouvable() {
        IAction action = new DeplacementVolDirect(new Ville("ville_introuvable"));
        Assertions.assertThrows(VilleIntrouvableException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void actionSeDeplacerVolDirectVilleInexistanteDeckJoueur() {
        IAction action = new DeplacementVolDirect(paris);
        Assertions.assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

//=============================================================================================================================
//                                                ACTION DeplacementVolCharter
//=============================================================================================================================

//------------ 1- Tests jouerAction() avec l'action DeplacementVolCharter OK

    @Test
    void jouerTourActionDeplacementVolCharterOK() {
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new DeplacementVolCharter(paris);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action));
        assertEquals(pionJoueur.getVilleActuelle(), paris);
    }

//------------ 2- Tests jouerAction() avec l'action DeplacementVolCharter KO

    @Test
    void jouerTourActionDeplacementVolCharterPasDeCarteVilleActuel() {
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));

        IAction action = new DeplacementVolCharter(milan);
        Assertions.assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,() -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementVolCharterVilleIntrouvable() {
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        IAction action = new DeplacementVolCharter(new Ville("ae"));
        Assertions.assertThrows(VilleIntrouvableException.class,() -> this.instance.jouerAction(pionJoueur,action));
    }

//=============================================================================================================================
//                                                ACTION ConstruireUneStation
//=============================================================================================================================

//-----------------------------------------------------------------------------------------------------------------------------
// - Les roles sont distribu??s al??atoirement entre les joueurs en d??but de partie
// - Etant une action qui s'execute differemment si le role du joueur est : "EXPERT_AUX_OPERATIONS"
// Alors, pour effectuer certains tests on est contraint de set un role autre que "EXPERT_AUX_OPERATIONS"
// (Tests fonctionnent avec n'importe quel autre role mis ?? part EXPERT_AUX_OPERATIONS, qui lui a un/des tests propres)
//-----------------------------------------------------------------------------------------------------------------------------

//------------ 1- Tests jouerAction() avec l'action ConstruireUneStation OK

    @Test
    void jouerTourActionConstruireUneStationOK() {
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur.setVilleActuelle(alger);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(alger));
        IAction action = new ConstruireUneStation();
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action));
        assertTrue(pionJoueur.getVilleActuelle().isStationDeRechercheVille());
        assertFalse(pionJoueur.isVilleOfCarteVilleDeckJoueur(alger));
    }

    @Test
    void jouerTourActionConstruireUneStationDeplacementStationOK(){
        atlanta.setStationDeRechercheVille(false);
        chicago.setStationDeRechercheVille(true);
        paris.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        milan.setStationDeRechercheVille(true);
        tokyo.setStationDeRechercheVille(true);
        istanbul.setStationDeRechercheVille(true);
        pionJoueur.setVilleActuelle(atlanta);
        IAction action = new ConstruireUneStation(chicago);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action));
        assertTrue(atlanta.isStationDeRechercheVille());
        assertFalse(chicago.isStationDeRechercheVille());
    }

//------------ 2- Tests jouerAction() avec l'action ConstruireUneStation KO

    @Test
    void jouerTourActionConstruireUneStationNbActionsMaxTourAtteint() {
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(alger));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        IAction action1 = new DeplacementVolDirect(chicago);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementVolDirect(paris);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementVolDirect(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 =  new DeplacementVolDirect(milan);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action4));
        IAction action5 = new ConstruireUneStation();
        Assertions.assertThrows(NbActionsMaxTourAtteintException.class,
                () -> this.instance.jouerAction(pionJoueur,action5));
    }

    @Test
    void jouerTourActionConstruireUneStationDeplacementStationVilleAvecAucuneStationDeRecherche(){
        atlanta.setStationDeRechercheVille(false);
        istanbul.setStationDeRechercheVille(false);
        chicago.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        paris.setStationDeRechercheVille(true);
        milan.setStationDeRechercheVille(true);
        tokyo.setStationDeRechercheVille(true);
        miami.setStationDeRechercheVille(true);
        pionJoueur.setVilleActuelle(atlanta);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new ConstruireUneStation(istanbul);
        Assertions.assertThrows(VilleAvecAucuneStationDeRechercheException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionConstruireUneStationDeplacementStationVilleActuellePossedeDejaUneStationDeRecherche(){
        atlanta.setStationDeRechercheVille(true);
        chicago.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        paris.setStationDeRechercheVille(true);
        milan.setStationDeRechercheVille(true);
        tokyo.setStationDeRechercheVille(true);
        miami.setStationDeRechercheVille(true);
        pionJoueur.setVilleActuelle(atlanta);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new ConstruireUneStation(chicago);
        Assertions.assertThrows(VilleActuellePossedeDejaUneStationDeRechercheException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionConstruireUneStationCarteVilleInexistanteDansDeckJoueur(){
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur.setVilleActuelle(alger);
        IAction action = new ConstruireUneStation();
        Assertions.assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionConstruireUneStationVilleActuellePossedeDejaUneStationDeRecherche(){
        atlanta.setStationDeRechercheVille(true);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new ConstruireUneStation();
        Assertions.assertThrows(VilleActuellePossedeDejaUneStationDeRechercheException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

//=============================================================================================================================
//                                                 ACTION DeplacementNavette
//=============================================================================================================================

//-----------------------------------------------------------------------------------------------------------------------------
// - Les roles sont distribu??s al??atoirement entre les joueurs en d??but de partie
// - Etant une action qui s'execute differemment si le role du joueur est : "EXPERT_AUX_OPERATIONS"
// Alors, pour effectuer certains tests on est contraint de set un role autre que "EXPERT_AUX_OPERATIONS"
// (Tests fonctionnent avec n'importe quel autre role mis ?? part EXPERT_AUX_OPERATIONS, qui lui a un/des tests propres)
//-----------------------------------------------------------------------------------------------------------------------------

//------------ 1- Tests jouerAction() avec l'action DeplacementNavette OK

    @Test
    void jouerTourActionDeplacementNavetteOK() {
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        IAction action = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action));
        assertEquals(pionJoueur.getVilleActuelle(), alger);
    }

//------------ 2- Tests jouerAction() avec l'action DeplacementNavette KO

    @Test
    void jouerTourActionDeplacementNavetteVilleAvecAucuneStationDeRechercheVilleDestination() {
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(false);
        pionJoueur.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        IAction action = new DeplacementNavette(alger);
        Assertions.assertThrows(VilleAvecAucuneStationDeRechercheException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementNavetteVilleAvecAucuneStationDeRechercheVilleActuelle() {
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(false);
        pionJoueur.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        IAction action = new DeplacementNavette(alger);
        Assertions.assertThrows(VilleAvecAucuneStationDeRechercheException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementNavetteVilleDestinationEstVilleActuelle() {
        atlanta.setStationDeRechercheVille(true);
        IAction action = new DeplacementNavette(atlanta);
        Assertions.assertThrows(VilleDestinationEstVilleActuelleException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void jouerTourActionDeplacementNavetteNbActionsMaxTourAtteint() {
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        IAction action1 = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementNavette(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 = new DeplacementNavette(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action4));
        IAction action5 = new DeplacementNavette(alger);
        Assertions.assertThrows(NbActionsMaxTourAtteintException.class,
                () -> this.instance.jouerAction(pionJoueur,action5));
    }

//=============================================================================================================================
//                                                 ACTION TraiterMaladie
//=============================================================================================================================

//-----------------------------------------------------------------------------------------------------------------------------
// - Les roles sont distribu??s al??atoirement entre les joueurs en d??but de partie
// - Etant une action qui s'execute differemment si le role du joueur est : "MEDECIN"
// Alors, pour effectuer certains tests on est contraint de set un role autre que "MEDECIN"
// (Tests fonctionnent avec n'importe quel autre role mis ?? part MEDECIN, qui lui a un/des tests propres)
//-----------------------------------------------------------------------------------------------------------------------------

//------------ 1- Tests jouerAction() avec l'action TraiterMaladie OK

    @Test
    void jouerTourActionTraiterMaladieOK(){
        Virus virusBleu = plateau.getLesVirus().get("BLEU");
        atlanta.getNbCubeVirusVille().put(virusBleu.getVirusCouleur(),0);
        // pour simplifier le test, on choisit la ville qui se propage plut??t que de tester la propagation random
        Assertions.assertDoesNotThrow(() -> this.pionJoueur.getPlateau().propagationMaladie(atlanta, 2));
        pionJoueur.setRoleJoueur(new CarteRepartiteur(CouleurPionsRole.ROSE));
        IAction traiter = new TraiterMaladie(virusBleu);
        pionJoueur.setVilleActuelle(atlanta);
        Assertions.assertDoesNotThrow(() -> instance.jouerAction(pionJoueur,traiter));
        Assertions.assertEquals(1, atlanta.getNbCubeVirusVille().get(virusBleu.getVirusCouleur()));
    }

//------------ 2- Tests jouerAction() avec l'action TraiterMaladie OK (test sp??cifique au Role : "MEDECIN")

    @Test
    void jouerTourActionTraiterAvecMedecinMaladieOK() {
        Assertions.assertDoesNotThrow(() -> this.pionJoueur.getPlateau().propagationMaladie(atlanta, 3));
        Virus virusBleu = plateau.getLesVirus().get("BLEU");
        pionJoueur.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        IAction traiter = new TraiterMaladie(virusBleu);
        Assertions.assertDoesNotThrow(() -> instance.jouerAction(pionJoueur, traiter));
        Assertions.assertEquals(0, atlanta.getNbCubeVirusVille().get(virusBleu.getVirusCouleur()));
    }

//------------ 4- Tests jouerAction() avec l'action TraiterMaladie KO
    @Test
    void jouerTourActionTraiterMaladieNbActionsMaxTourAtteint(){
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        IAction action1 = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementNavette(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 = new DeplacementNavette(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action4));

        Virus virusBleu = plateau.getLesVirus().get("BLEU");
        IAction action5 = new TraiterMaladie(virusBleu);
        Assertions.assertThrows(NbActionsMaxTourAtteintException.class,() -> this.instance.jouerAction(pionJoueur,action5));
    }

//=============================================================================================================================
//                                          ACTION DonnerConnaissance (PartagerConnaissance)
//=============================================================================================================================

//-----------------------------------------------------------------------------------------------------------------------------
// - Les roles sont distribu??s al??atoirement entre les joueurs en d??but de partie
// - Etant une action qui s'execute differemment si le role du joueur est : "CHERCHEUSE"
// Alors, pour effectuer certains tests on est contraint de set un role autre que "CHERCHEUSE"
// (Tests fonctionnent avec n'importe quel autre role mis ?? part CHERCHEUSE, qui lui a un/des tests propres)
//-----------------------------------------------------------------------------------------------------------------------------

//------------ 1- Tests jouerAction() avec l'action DonnerConnaissance OK

    @Test
    void donnerConnaissanceOk(){
        // pionJoueur : Joueur qui va donner
        // pionJoueur2 : Joueur qui va prendre
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        IAction donnerConaissance = new DonnerConnaissance(pionJoueur2);
        assertDoesNotThrow(() -> instance.jouerAction(pionJoueur,donnerConaissance));
        assertFalse(pionJoueur.isVilleOfCarteVilleDeckJoueur(atlanta));
        assertTrue(pionJoueur2.isVilleOfCarteVilleDeckJoueur(atlanta));
    }

//------------ 2- Tests jouerAction() avec l'action DonnerConnaissance OK (test sp??cifique au Role : "CHERCHEUSE")

    @Test
    void donnerConnaissanceChercheuseOk(){
        // pionJoueur : Role: CHERCHEUSE : Joueur qui va donner
        // pionJoueur2 : Joueur qui va prendre
        pionJoueur.setRoleJoueur(new CarteChercheuse(CouleurPionsRole.MARRON));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action = new DonnerConnaissance(pionJoueur2, chicago);
        assertDoesNotThrow(() -> instance.jouerAction(pionJoueur,action));
        assertFalse(pionJoueur.isVilleOfCarteVilleDeckJoueur(chicago));
        assertTrue(pionJoueur2.isVilleOfCarteVilleDeckJoueur(chicago));
    }

//------------ 3- Tests jouerAction() avec l'action DonnerConnaissance KO

    @Test
    void donnerConnaissanceNbActionsMaxTourAtteint(){
        pionJoueur2.setVilleActuelle(atlanta);
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action1 = new DeplacementNavette(alger);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementNavette(atlanta);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementNavette(alger);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 = new DeplacementNavette(atlanta);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action4));
        IAction action5 = new DonnerConnaissance(pionJoueur2);
        assertThrows(NbActionsMaxTourAtteintException.class,
                () -> this.instance.jouerAction(pionJoueur,action5));
    }

    @Test
    void donnerConnaissanceJoueursNonPresentMemeVille(){
        pionJoueur2.setVilleActuelle(chicago);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new DonnerConnaissance(pionJoueur2);
        assertThrows(JoueursNonPresentMemeVilleException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void donnerConnaissanceCarteVilleInexistanteDansDeckJoueur(){
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        IAction action = new DonnerConnaissance(pionJoueur2,atlanta);
        assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

//------------ 4- Tests jouerAction() avec l'action DonnerConnaissance KO (tests sp??cifiques au Role : "CHERCHEUSE")

    @Test
    void donnerConnaissanceChercheuseDonneeManquante(){
        pionJoueur.setRoleJoueur(new CarteChercheuse(CouleurPionsRole.MARRON));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action = new DonnerConnaissance(pionJoueur2);
        assertThrows(DonneeManquanteException.class,
                () -> this.instance.jouerAction(pionJoueur, action));
    }

    @Test
    void donnerConnaissanceChercheuseCarteVilleInexistanteDansDeckJoueur(){
        pionJoueur.setRoleJoueur(new CarteChercheuse(CouleurPionsRole.MARRON));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new DonnerConnaissance(pionJoueur2, chicago);
        assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,
                () -> this.instance.jouerAction(pionJoueur, action));
    }

//=============================================================================================================================
//                                          ACTION PrendreConnaissance (PartagerConnaissance)
//=============================================================================================================================

//-----------------------------------------------------------------------------------------------------------------------------
// - Les roles sont distribu??s al??atoirement entre les joueurs en d??but de partie
// - Etant une action qui s'execute differemment si le role du joueur est : "CHERCHEUSE"
// Alors, pour effectuer certains tests on est contraint de set un role autre que "CHERCHEUSE"
// (Tests fonctionnent avec n'importe quel autre role mis ?? part CHERCHEUSE, qui lui a un/des tests propres)
//-----------------------------------------------------------------------------------------------------------------------------

//------------ 1- Tests jouerAction() avec l'action PrendreConnaissance OK

    @Test
    void prendreConnaissanceOk(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Joueur qui va donner
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur2.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction prendreConnaissance = new PrendreConnaissance(pionJoueur2);
        assertDoesNotThrow(() -> instance.jouerAction(pionJoueur,prendreConnaissance));
        assertFalse(pionJoueur2.isVilleOfCarteVilleDeckJoueur(atlanta));
        assertTrue(pionJoueur.isVilleOfCarteVilleDeckJoueur(atlanta));
    }

//------------ 2- Tests jouerAction() avec l'action PrendreConnaissance OK (test sp??cifique au Role : "CHERCHEUSE")

    @Test
    void prendreConnaissanceChercheuseOk(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Role CHERCHEUSE : Joueur qui va donner
        pionJoueur2.setRoleJoueur(new CarteChercheuse(CouleurPionsRole.MARRON));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction prendreConnaissance = new PrendreConnaissance(pionJoueur2, chicago);
        assertDoesNotThrow(() -> instance.jouerAction(pionJoueur,prendreConnaissance));
        assertTrue(pionJoueur.isVilleOfCarteVilleDeckJoueur(chicago));
        assertFalse(pionJoueur2.isVilleOfCarteVilleDeckJoueur(chicago));
    }

//------------ 3- Tests jouerAction() avec l'action PrendreConnaissance KO

    @Test
    void prendreConnaissanceNbActionsMaxTourAtteint(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Joueur qui va donner
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action1 = new DeplacementNavette(alger);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementNavette(atlanta);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementNavette(alger);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 = new DeplacementNavette(atlanta);
        assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action4));
        IAction action5 = new PrendreConnaissance(pionJoueur2);
        assertThrows(NbActionsMaxTourAtteintException.class,
                () -> this.instance.jouerAction(pionJoueur,action5));
    }

    @Test
    void prendreConnaissanceJoueursNonPresentMemeVille(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Joueur qui va donner
        pionJoueur2.setVilleActuelle(chicago);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new PrendreConnaissance(pionJoueur2);
        Assertions.assertThrows(JoueursNonPresentMemeVilleException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

    @Test
    void prendreConnaissanceCarteVilleInexistanteDansDeckJoueur(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Joueur qui va donner
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        IAction action = new PrendreConnaissance(pionJoueur2);
        assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,
                () -> this.instance.jouerAction(pionJoueur,action));
    }

//------------ 4- Tests jouerAction() avec l'action PrendreConnaissance KO (tests sp??cifiques au Role : "CHERCHEUSE")

    @Test
    void prendreConnaissanceChercheuseDonneeManquante(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Role CHERCHEUSE : Joueur qui va donner
        pionJoueur2.setRoleJoueur(new CarteChercheuse(CouleurPionsRole.MARRON));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action = new PrendreConnaissance(pionJoueur2,null);
        assertThrows(DonneeManquanteException.class,
                () -> this.instance.jouerAction(pionJoueur, action));
    }

    @Test
    void prendreConnaissanceChercheuseCarteVilleInexistanteDansDeckJoueur(){
        // pionJoueur : Joueur qui va prendre
        // pionJoueur2 : Role CHERCHEUSE : Joueur qui va donner
        pionJoueur2.setRoleJoueur(new CarteChercheuse(CouleurPionsRole.MARRON));
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action = new PrendreConnaissance(pionJoueur2, atlanta);
        assertThrows(CarteVilleInexistanteDansDeckJoueurException.class,
                () -> this.instance.jouerAction(pionJoueur, action));
    }

//=============================================================================================================================
//                                                 ACTION DecouvrirRemede
//=============================================================================================================================

//-----------------------------------------------------------------------------------------------------------------------------
// - Les roles sont distribu??s al??atoirement entre les joueurs en d??but de partie
// Alors, pour effectuer certains tests, on devra pr??ciser lorsqu'il s'agit du role "SCIENTIFIQUE"
//-----------------------------------------------------------------------------------------------------------------------------

//------------ 1- Tests jouerAction() avec l'action DecouvrirRemede OK

    @Test
    void jouerTourActionDecouvrirRemedeOK() {
        atlanta.setStationDeRechercheVille(true);
        // pour le test on clear le deck actuel qui contient de base 2cartes distribu??s
        // pour ajouter 5 cartes de la m??me couleur
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("New_York")));
        int tailleDeckApresAjoutDesCartes = pionJoueur.getDeckJoueur().size();
        int tailleDefausseInitial = pionJoueur.getPlateau().getDefausseCarteJoueur().size();
        IAction action = new DecouvrirRemede();
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action));
        assertEquals(tailleDeckApresAjoutDesCartes - 5, pionJoueur.getDeckJoueur().size());
        assertEquals(tailleDefausseInitial + 5, pionJoueur.getPlateau().getDefausseCarteJoueur().size());
        String couleurVirus = atlanta.getCouleurVirusVille();
        assertEquals(pionJoueur.getPlateau().getLesVirus().get(couleurVirus).getEtatVirus(), EtatVirus.TRAITE);
    }

//------------ 2- Tests jouerAction() avec l'action DecouvrirRemede OK (test sp??cifique au Role : "SCIENTIFIQUE")

    @Test
    void jouerTourActionDecouvrirRemedeScientifiqueOK() {
        atlanta.setStationDeRechercheVille(true);
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        // pour le test on clear le deck actuel qui contient de base 2cartes distribu??s
        // pour ajouter 5 cartes de la m??me couleur
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        int tailleDeckInitial = pionJoueur.getDeckJoueur().size();
        int tailleDefausseInitial = pionJoueur.getPlateau().getDefausseCarteJoueur().size();
        IAction action = new DecouvrirRemede();
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action));
        assertEquals(tailleDeckInitial - 4, pionJoueur.getDeckJoueur().size());
        assertEquals(tailleDefausseInitial + 4, pionJoueur.getPlateau().getDefausseCarteJoueur().size());
        String couleurVirus = atlanta.getCouleurVirusVille();
        assertEquals(EtatVirus.TRAITE, pionJoueur.getPlateau().getLesVirus().get(couleurVirus).getEtatVirus());
    }

//------------ 3- Tests jouerAction() avec l'action DecouvrirRemede KO

    @Test
    void jouerTourActionDecouvrirRemedeVilleAvecAucuneStationDeRecherche() {
        atlanta.setStationDeRechercheVille(false);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("New_York")));
        IAction action = new DecouvrirRemede();
        Assertions.assertThrows(VilleAvecAucuneStationDeRechercheException.class, () -> this.instance.jouerAction(pionJoueur, action));
    }

    @Test
    void jouerTourActionDecouvrirRemedeNombreDeCartesVilleDansDeckJoueurInvalide() {
        atlanta.setStationDeRechercheVille(true);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new DecouvrirRemede();
        Assertions.assertThrows(NbCartesVilleDansDeckJoueurInvalideException.class, () -> this.instance.jouerAction(pionJoueur, action));
    }

    @Test
    void jouerTourActionDecouvrirRemedeNbActionsMaxTourAtteint() {
        atlanta.setStationDeRechercheVille(true);
        alger.setStationDeRechercheVille(true);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("New_York")));
        IAction action1 = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action1));
        IAction action2 = new DeplacementNavette(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action2));
        IAction action3 = new DeplacementNavette(alger);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action3));
        IAction action4 = new DeplacementNavette(atlanta);
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur,action4));
        IAction action5 = new DecouvrirRemede();
        Assertions.assertThrows(NbActionsMaxTourAtteintException.class, () -> this.instance.jouerAction(pionJoueur, action5));
    }

    @Test
    void jouerTourActionDecouvrirRemedeVirusDejaTraite() {
        atlanta.setStationDeRechercheVille(true);
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("New_York")));
        IAction action1 = new DecouvrirRemede();
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action1));

        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("New_York")));
        IAction action2 = new DecouvrirRemede();
        Assertions.assertThrows(VirusDejaTraiteException.class, () -> this.instance.jouerAction(pionJoueur, action2));
    }

//------------ 4- Tests jouerAction() avec l'action DecouvrirRemede KO (tests sp??cifiques au Role : "SCIENTIFIQUE")

    @Test
    void jouerTourActionDecouvrirRemedeScientifiqueVilleAvecAucuneStationDeRecherche() {
        atlanta.setStationDeRechercheVille(false);
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action = new DecouvrirRemede();
        Assertions.assertThrows(VilleAvecAucuneStationDeRechercheException.class, () -> this.instance.jouerAction(pionJoueur, action));
    }

    @Test
    void jouerTourActionDecouvrirRemedeScientifiqueNombreDeCartesVilleDansDeckJoueurInvalide() {
        atlanta.setStationDeRechercheVille(true);
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        IAction action = new DecouvrirRemede();
        Assertions.assertThrows(NbCartesVilleDansDeckJoueurInvalideException.class, () -> this.instance.jouerAction(pionJoueur, action));
    }

    @Test
    void jouerTourActionDecouvrirScientifiqueRemedeVirusDejaTraite() {
        atlanta.setStationDeRechercheVille(true);
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action1 = new DecouvrirRemede();
        Assertions.assertDoesNotThrow(() -> this.instance.jouerAction(pionJoueur, action1));

        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        IAction action2 = new DecouvrirRemede();
        Assertions.assertThrows(VirusDejaTraiteException.class, () -> this.instance.jouerAction(pionJoueur, action2));
    }

//=============================================================================================================================
//                                                 ROLE EXPERT_AUX_OPERATIONS
//=============================================================================================================================

 /*
 V??rifie que si le role du joueur est expert aux op??rations, l'action construire une station de recherche dans la ville que l'on occupe
 ne d??fausse aucune carte
  */

    @Test
    void construireStationRechercheExpertOperationOk(){
        pionJoueur.setRoleJoueur(new CarteExpertAuxOperations(CouleurPionsRole.VERT_CLAIR));
        IAction action = new ConstruireUneStation();
        pionJoueur.setVilleActuelle(chicago);
        CarteVille carteChicago = new CarteVille(chicago);
        pionJoueur.getDeckJoueur().add(carteChicago);
        Assertions.assertDoesNotThrow(()-> instance.jouerAction(pionJoueur,action));
        assertEquals(pionJoueur.getDeckJoueur().get(0),carteChicago);
    }


//=============================================================================================================================
//                                                 ROLE PLANIFICATEUR D'URGENCE
//=============================================================================================================================

    @Test
    void planificateurUrgenceOk(){
        CarteEvenement carteEvenement = new CartePontAerien();
        pionJoueur.getPlateau().getDefausseCarteJoueur().add(carteEvenement);
        pionJoueur.setRoleJoueur(new CartePlanificateurDurgence(CouleurPionsRole.BLEU));
        IAction action = new EntreposerEvenementRolePlanificateur(carteEvenement);
        assertDoesNotThrow(()-> instance.jouerAction(pionJoueur,action));
        assertEquals(carteEvenement,pionJoueur.getCartePlanificateurUrgenceEntrepose());
    }

//=============================================================================================================================
//                                                 ROLE SPECIALISTE_MISE_EN_QUARANTAINE
//=============================================================================================================================

    @Test
    void miseEnQuarantaineVille(){
        pionJoueur.setRoleJoueur(new CarteSpecialisteEnMiseEnQuarantaine(CouleurPionsRole.VERT_FONCE));
        IAction actionDeplacement = new DeplacementVoiture(chicago);
        assertDoesNotThrow(()->instance.jouerAction(pionJoueur,actionDeplacement));
        assertThrows((PropagationImpossibleCarSpecialisteQuarantaineException.class),() -> pionJoueur.getPlateau().propagationMaladie(pionJoueur.getVilleActuelle(),2));
    }

//=============================================================================================================================
//                                                 ROLE REPARTITEUR
//=============================================================================================================================

    @Test
    void repartiteurOk() {
        pionJoueur.setRoleJoueur(new CarteRepartiteur(CouleurPionsRole.ROSE));
        PionJoueur pionJoueur3 = new PionJoueur(instance.partie.getPlateau());
        pionJoueur2.setAutorisationDeplacementRepartiteur(true);
        pionJoueur3.setVilleActuelle(alger);
        instance.partie.getJoueurs().add(pionJoueur3);
        assertDoesNotThrow(()->instance.repartiteurDeplacementPion(pionJoueur,pionJoueur2,alger));
    }

//=============================================================================================================================
//                                                 ROLE MEDECIN
//=============================================================================================================================

    /*
    S'assurer que l'on ne puisse pas ajouter de nouveau cube maladie d??j?? soign??e
    quelque part dans la ville qui compte le m??decin parmi elle.
     */

    @Test
    void medecinOk() {
        // cas propagation de maladie
        pionJoueur.setRoleJoueur(new CarteMedecin(CouleurPionsRole.ORANGE));
        Virus virus = instance.partie.getPlateau().getLesVirus().get(pionJoueur.getVilleActuelle().getCouleurVirusVille());
        IAction traiter = new TraiterMaladie(virus);
        Assertions.assertDoesNotThrow(() -> instance.jouerAction(pionJoueur, traiter));
        pionJoueur.getVilleActuelle().getListeVaccinationContreVirus().put(pionJoueur.getVilleActuelle().getCouleurVirusVille(), virus);
        Assertions.assertDoesNotThrow(() -> instance.partie.getPlateau().propagationMaladie(pionJoueur.getVilleActuelle(), 1));
        assertEquals(0, pionJoueur.getVilleActuelle().getNbCubeVirusVille().get(virus.getVirusCouleur()));

        // cas ??closion dans une ville voisine
        Ville miami = instance.partie.getPlateau().getVilleByName("Miami");
        Virus virus2 = instance.partie.getPlateau().getLesVirus().get(miami.getCouleurVirusVille());
        miami.getNbCubeVirusVille().put(virus2.getVirusCouleur(), 3);
        pionJoueur.getVilleActuelle().getListeVaccinationContreVirus().put(miami.getCouleurVirusVille(), virus2);
        Assertions.assertDoesNotThrow(() -> instance.partie.getPlateau().eclosion(miami, virus2));
        assertEquals(0, pionJoueur.getVilleActuelle().getNbCubeVirusVille().get(virus2.getVirusCouleur()));
    }

//=============================================================================================================================
//                                                 AUTRES TESTS
//=============================================================================================================================

    @Test
    void jouerTourOk() {
        PionJoueur joueurActuel = instance.partie.getJoueurActuel();
        joueurActuel.setRoleJoueur(new CarteRepartiteur(CouleurPionsRole.ROSE));
        IAction action = new DeplacementVoiture(plateau.getVilleByName("Washington"));
        IAction action1 = new DeplacementVoiture(atlanta);
        IAction action2 = new DeplacementVoiture(chicago);
        IAction action3 = new DeplacementVoiture(atlanta);
        List<IAction> listeActions = List.of(action,action1,action2,action3);
        assertDoesNotThrow(()-> instance.jouerTour(listeActions));
        // verif que c'est bien au joueur suivant apr??s ce tour
        assertNotEquals(instance.partie.getJoueurActuel(),joueurActuel);
    }

    @Test
    void piocherCartes(){
        CarteJoueur premiereCarteVille = new CarteVille(atlanta);
        CarteJoueur deuxiemeCarteVille = new CarteVille(chicago);
        plateau.getPiocheCarteJoueur().addFirst(premiereCarteVille);
        plateau.getPiocheCarteJoueur().addFirst(deuxiemeCarteVille);
        int tailleDeckInitial = pionJoueur.getDeckJoueur().size();
        int taillePiocheInitial = plateau.getPiocheCarteJoueur().size();
        Assertions.assertDoesNotThrow(() -> this.instance.piocherCartes(pionJoueur));
        assertEquals(taillePiocheInitial-2, plateau.getPiocheCarteJoueur().size());
        assertEquals(tailleDeckInitial+2, pionJoueur.getDeckJoueur().size());
        assertTrue(pionJoueur.getDeckJoueur().contains(premiereCarteVille));
        assertTrue(pionJoueur.getDeckJoueur().contains(deuxiemeCarteVille));
    }

    @Test
    void piocherCartesEpidemie(){
        //Une carte ??pid??mie ?? trois effets ?? tester
        CarteJoueur premiereCarteVille = new CarteVille(atlanta);
        CarteJoueur carteEpidemie = new CarteEpidemie();
        CartePropagation cartePropagation = plateau.getPiocheCartePropagation().get(plateau.getPiocheCartePropagation().size() -1);
        Virus virus = plateau.getLesVirus().get(cartePropagation.getVilleCartePropagation().getCouleurVirusVille());
        plateau.getPiocheCarteJoueur().addFirst(premiereCarteVille);
        plateau.getPiocheCarteJoueur().addFirst(carteEpidemie);
        int tailleDeckInitial = pionJoueur.getDeckJoueur().size();
        int taillePiocheInitial = plateau.getPiocheCarteJoueur().size();
        int tailleDefausseCarteJoueurInitial = plateau.getDefausseCarteJoueur().size();
        int ancienMarqueurVitesseDePropagation = plateau.getMarqueurVitessePropagation();

        Assertions.assertDoesNotThrow(() -> this.instance.piocherCartes(pionJoueur));

        assertEquals(tailleDeckInitial + 1,pionJoueur.getDeckJoueur().size());
        assertEquals(tailleDefausseCarteJoueurInitial + 1, plateau.getDefausseCarteJoueur().size());
        assertEquals(taillePiocheInitial - 2, plateau.getPiocheCarteJoueur().size());

        // EFFET 1 : ACCELERATION
        assertEquals(ancienMarqueurVitesseDePropagation + 1, plateau.getMarqueurVitessePropagation());
        assertEquals(2,plateau.getVitesseDePropagation());

        // EFFET 2 : INFECTION
        assertEquals(3,cartePropagation.getVilleCartePropagation().getNbCubeVirusVille().get(virus.getVirusCouleur()));

        // EFFET 3 : INTENSIFICATION
        // 0, car notre defausseCartePropagation doit ??tre vide
        assertEquals(0, plateau.getDefausseCartePropagation().size());
        // 48 = le nombre de cartes Propagation existante
        assertEquals(48, plateau.getPiocheCartePropagation().size());
    }

//=============================================================================================================================
//                                                 EFFET Evenement
//=============================================================================================================================

    @Test
    void jouerCarteEventParUneNuitTranquille() {
        CarteEvenement carteEvenementNuitTranquille = new CarteParUneNuitTranquille();
        pionJoueur.getDeckJoueur().add(carteEvenementNuitTranquille);

        assertDoesNotThrow(() -> instance.jouerEvent(pionJoueur, carteEvenementNuitTranquille));
        assertThrows(NuitTranquilleException.class, () -> instance.propagation(pionJoueur));
        assertTrue(pionJoueur.getPlateau().isEffetParUneNuitTranquilleActif());
    }

    @Test
    void jouerCarteEventPopulationResiliente() {
        CartePropagation cartePropagation = new CartePropagation(atlanta);
        pionJoueur.getPlateau().getDefausseCartePropagation().add(cartePropagation);
        CartePopulationResiliente carteEvenementPopulationResiliente = new CartePopulationResiliente();
        pionJoueur.getDeckJoueur().add(carteEvenementPopulationResiliente);
        carteEvenementPopulationResiliente.setCartePropagationChoisis(cartePropagation);
        assertDoesNotThrow(() -> instance.jouerEvent(pionJoueur, carteEvenementPopulationResiliente));
    }

    @Test
    void jouerCarteEventPontAerien() {
        pionJoueur2.setVilleActuelle(atlanta);
        pionJoueur2.setPermissionPontAerien(true);

        CartePontAerien cartePontAerien = new CartePontAerien();
        cartePontAerien.setPionChoisis(pionJoueur2);
        cartePontAerien.setVilleChoisis(paris);
        pionJoueur.getDeckJoueur().add(cartePontAerien);
        assertDoesNotThrow(()-> instance.jouerEvent(pionJoueur, cartePontAerien));
    }

    @Test
    void jouerCarteEventPrevision() {
        Plateau plateauTest = instance.partie.getPlateau();
        List<CartePropagation> cartesPropagationTests = new LinkedList<>();
        cartesPropagationTests.add(new CartePropagation(Ville.builder().nomVille("Test1").build()));
        cartesPropagationTests.add(new CartePropagation(Ville.builder().nomVille("Test2").build()));
        cartesPropagationTests.add(new CartePropagation(Ville.builder().nomVille("Test3").build()));
        cartesPropagationTests.add(new CartePropagation(Ville.builder().nomVille("Test4").build()));
        cartesPropagationTests.add(new CartePropagation(Ville.builder().nomVille("Test5").build()));
        cartesPropagationTests.add(new CartePropagation(Ville.builder().nomVille("Test6").build()));
        plateauTest.setPiocheCartePropagation(cartesPropagationTests);
        pionJoueur.setPlateau(plateauTest);

        CartePrevision cartePrevision = new CartePrevision();
        cartePrevision.execEffet(pionJoueur);
        List<CartePropagation> mainAMelanger = pionJoueur.getMainAReorganiser();
        Collections.shuffle(mainAMelanger);
        cartePrevision.ajouterDansPiochePropagation(pionJoueur, mainAMelanger);
        pionJoueur.getDeckJoueur().add(cartePrevision);
        assertEquals(mainAMelanger, pionJoueur.getPlateau().getPiocheCartePropagation());
    }

    @Test
    void jouerCarteSubventionPublique() throws VilleIntrouvableException {
        Plateau plateauTest = plateau;
        pionJoueur.setPlateau(plateauTest);
        CarteSubventionPublique carteSubventionPublique = new CarteSubventionPublique();
        carteSubventionPublique.placerStationRecherche(pionJoueur, "Atlanta");
        pionJoueur.getDeckJoueur().add(carteSubventionPublique);
        assertDoesNotThrow(()-> instance.jouerEvent(pionJoueur, carteSubventionPublique));
    }

//=============================================================================================================================
//                                               Tests Fin de partie
//=============================================================================================================================

    @Test
    void jouerTourVictoirePartie(){
        pionJoueur.setRoleJoueur(new CarteScientifique(CouleurPionsRole.BLANC));
        // 4 cartes de virus BLEU
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(atlanta));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(chicago));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(milan));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(paris));
        // 4 cartes de virus ROUGE
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Tokyo")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Seoul")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Osaka")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Sydney")));
        // 4 cartes de virus JAUNE
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Mexico")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Miami")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Bogota")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Lima")));
        // 4 cartes de virus NOIR
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Alger")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Karachi")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Chennai")));
        pionJoueur.ajouterCarteVilleDeckJoueur(new CarteVille(plateau.getVilleByName("Mumbai")));

        IAction action2 = new DecouvrirRemede();
        IAction action1 = new DecouvrirRemede();
        IAction action3 = new DecouvrirRemede();
        IAction action4 = new DecouvrirRemede();
        List<IAction> listeActionPionJoueur =List.of(action1,action2,action3, action4);
        Assertions.assertThrows(VictoireFinDePartieException.class, () -> this.instance.jouerTour(listeActionPionJoueur));
    }

}