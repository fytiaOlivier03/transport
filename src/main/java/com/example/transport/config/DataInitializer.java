package com.example.transport.config;

import com.example.transport.entity.*;
import com.example.transport.repository.*;
import com.example.transport.service.ParametreService;
import com.example.transport.service.TarificationService;
import com.example.transport.util.NumeroUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Jeu de donnees de demonstration (execute uniquement si la base est vide). */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final MissionRepository missionRepository;
    private final PreuveLivraisonRepository preuveRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParametreService parametreService;
    private final TarificationService tarificationService;

    @Override
    public void run(String... args) {
        parametreService.initialiserParDefaut();
        if (utilisateurRepository.count() > 0) {
            return;
        }
        utilisateur("admin", "admin123", Role.ADMIN);
        utilisateur("manager", "manager123", Role.MANAGER);
        utilisateur("operator", "operator123", Role.OPERATOR);
        utilisateur("user", "user123", Role.USER);

        Vehicule v1 = vehicule("1234 TAA", "Toyota", "Corolla", 2019, 4, 300.0, 85000.0, StatutVehicule.DISPONIBLE);
        Vehicule v2 = vehicule("5678 TAB", "Hyundai", "i10", 2021, 4, 250.0, 32000.0, StatutVehicule.DISPONIBLE);
        Vehicule v3 = vehicule("9012 TAC", "Renault", "Kangoo", 2020, 2, 600.0, 61000.0, StatutVehicule.DISPONIBLE);
        Vehicule v4 = vehicule("3456 TAD", "Peugeot", "Partner", 2018, 2, 700.0, 120000.0, StatutVehicule.DISPONIBLE);
        Vehicule v5 = vehicule("7890 TAE", "Honda", "CG125", 2022, 1, 40.0, 15000.0, StatutVehicule.DISPONIBLE);
        Vehicule v6 = vehicule("2468 TAF", "Toyota", "Hiace", 2017, 12, 1000.0, 210000.0, StatutVehicule.MAINTENANCE);

        Chauffeur rakoto = chauffeur("Rakoto", "Jean", "034 11 111 11", "PC-0001", LocalDate.now().plusYears(2),
                "Centre", -18.9100, 47.5250, StatutChauffeur.DISPONIBLE, v1);
        Chauffeur rasoa = chauffeur("Rasoa", "Marie", "034 22 222 22", "PC-0002", LocalDate.now().plusYears(1),
                "Nord", -18.8800, 47.5200, StatutChauffeur.DISPONIBLE, v2);
        Chauffeur andry = chauffeur("Andry", "Hery", "033 33 333 33", "PC-0003", LocalDate.now().plusMonths(18),
                "Sud", -18.9500, 47.5300, StatutChauffeur.DISPONIBLE, v3);
        chauffeur("Fanilo", "Tojo", "032 44 444 44", "PC-0004", LocalDate.now().plusYears(3),
                "Centre", -18.9150, 47.5180, StatutChauffeur.DISPONIBLE, v4);
        chauffeur("Mamy", "Njaka", "034 55 555 55", "PC-0005", LocalDate.now().minusMonths(2),
                "Nord", -18.8900, 47.5100, StatutChauffeur.DISPONIBLE, v5); // permis expire (demo)
        chauffeur("Lalaina", "Voahangy", "033 66 666 66", "PC-0006", LocalDate.now().plusYears(1),
                "Sud", -18.9600, 47.5400, StatutChauffeur.SUSPENDU, v6);

        Client c1 = client("Randria", "Solo", "034 10 000 01", "solo@example.com", "Analakely, Antananarivo");
        Client c2 = client("Ravelo", "Hanta", "033 10 000 02", "hanta@example.com", "Ivandry, Antananarivo");
        Client c3 = client("Mada Fresh", "SARL", "032 10 000 03", "contact@madafresh.mg", "Ankorondrano, Antananarivo");

        // Missions terminees / annulee (statistiques)
        mission(TypeMission.COURSE, c1, "Analakely", "Ivato Aeroport", "Centre", 15.0, null, null, 2,
                Priorite.NORMALE, jour(5, 9), StatutMission.TERMINEE, rakoto);
        mission(TypeMission.COURSE, c2, "Ivandry", "Antaninarenina", "Nord", 8.0, null, null, 1,
                Priorite.URGENTE, jour(3, 14), StatutMission.TERMINEE, rasoa);
        Mission livraison = mission(TypeMission.LIVRAISON, c3, "Ankorondrano", "Isotry", "Nord", 12.0, 40.0, 0.3, null,
                Priorite.NORMALE, jour(2, 10), StatutMission.TERMINEE, andry);
        PreuveLivraison preuve = new PreuveLivraison();
        preuve.setMission(livraison);
        preuve.setDateHeure(livraison.getDateFin());
        preuve.setNomReceptionnaire("Mme Rabe");
        preuve.setCodeConfirmation("CONF-4821");
        preuve.setCommentaire("Livré en main propre");
        preuveRepository.save(preuve);
        mission(TypeMission.COURSE, c1, "Isotry", "Analakely", "Centre", 4.0, null, null, 1,
                Priorite.NORMALE, jour(1, 17), StatutMission.TERMINEE, rakoto);
        mission(TypeMission.LIVRAISON, c2, "Ivandry", "Ambohimanarina", "Nord", 6.0, 10.0, 0.1, null,
                Priorite.NORMALE, jour(1, 11), StatutMission.ANNULEE, null);

        // Missions en attente d'attribution
        mission(TypeMission.COURSE, c2, "Ivandry", "Gare Soarano", "Nord", 9.0, null, null, 3,
                Priorite.URGENTE, LocalDateTime.now().plusHours(1).withSecond(0).withNano(0), StatutMission.CREEE, null);
        mission(TypeMission.LIVRAISON, c3, "Ankorondrano", "Andoharanofotsy", "Sud", 14.0, 120.0, 0.8, null,
                Priorite.TRES_URGENTE, LocalDateTime.now().plusHours(2).withSecond(0).withNano(0), StatutMission.CREEE, null);
    }

    private LocalDateTime jour(int ilYaJours, int heure) {
        return LocalDate.now().minusDays(ilYaJours).atTime(heure, 0);
    }

    private void utilisateur(String username, String motDePasse, Role role) {
        Utilisateur u = new Utilisateur();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(motDePasse));
        u.setRole(role);
        utilisateurRepository.save(u);
    }

    private Vehicule vehicule(String immat, String marque, String modele, int annee, int places, double kg,
                              double km, StatutVehicule statut) {
        Vehicule v = new Vehicule();
        v.setImmatriculation(immat);
        v.setMarque(marque);
        v.setModele(modele);
        v.setAnnee(annee);
        v.setCapacitePlaces(places);
        v.setCapaciteKg(kg);
        v.setKilometrage(km);
        v.setStatut(statut);
        return vehiculeRepository.save(v);
    }

    private Chauffeur chauffeur(String nom, String prenom, String tel, String permis, LocalDate expiration,
                                String zone, double lat, double lng, StatutChauffeur statut, Vehicule vehicule) {
        Chauffeur c = new Chauffeur();
        c.setNom(nom);
        c.setPrenom(prenom);
        c.setTelephone(tel);
        c.setNumeroPermis(permis);
        c.setDateExpirationPermis(expiration);
        c.setDateEmbauche(LocalDate.now().minusYears(2));
        c.setZone(zone);
        c.setLatitude(lat);
        c.setLongitude(lng);
        c.setStatut(statut);
        c.setVehicule(vehicule);
        return chauffeurRepository.save(c);
    }

    private Client client(String nom, String prenom, String tel, String email, String adresse) {
        Client c = new Client();
        c.setNom(nom);
        c.setPrenom(prenom);
        c.setTelephone(tel);
        c.setEmail(email);
        c.setAdresse(adresse);
        return clientRepository.save(c);
    }

    private Mission mission(TypeMission type, Client client, String depart, String destination, String zone,
                            double km, Double poids, Double volume, Integer passagers, Priorite priorite,
                            LocalDateTime datePrevue, StatutMission statut, Chauffeur chauffeur) {
        Mission m = new Mission();
        m.setNumero(NumeroUtil.generer(type));
        m.setType(type);
        m.setClient(client);
        m.setAdresseDepart(depart);
        m.setAdresseDestination(destination);
        m.setZone(zone);
        m.setLatDepart(zone.equals("Nord") ? -18.8850 : zone.equals("Sud") ? -18.9550 : -18.9120);
        m.setLngDepart(zone.equals("Nord") ? 47.5180 : zone.equals("Sud") ? 47.5320 : 47.5230);
        m.setDistanceKm(km);
        m.setPoids(poids);
        m.setVolume(volume);
        m.setNombrePassagers(passagers);
        m.setPriorite(priorite);
        m.setDatePrevue(datePrevue);
        m.setPrix(tarificationService.calculerPrix(type, km, poids, volume, priorite));
        m.setStatut(statut);
        m.setDateCreation(datePrevue.minusHours(2));
        if (chauffeur != null) {
            m.setChauffeur(chauffeur);
            m.setVehicule(chauffeur.getVehicule());
            m.setDateAssignation(datePrevue.minusHours(1));
            m.setDateDebut(datePrevue);
        }
        if (statut == StatutMission.TERMINEE) {
            m.setDateFin(datePrevue.plusMinutes(45));
            m.setCommission(tarificationService.calculerCommission(m.getPrix()));
        }
        if (statut == StatutMission.ANNULEE) {
            m.setDateFin(datePrevue.minusMinutes(30));
        }
        return missionRepository.save(m);
    }
}
