package sample.database;

public enum Table {
    ABWESENHEIT, AUFGABE, BEINHALTET, DURCHGEFUEHRT_AUF, FELD, FELDAUFGABE, GELAGERT_IN, GESCHAEFTSPARTNER, INVENTARGEGENSTAND, KOSTENSTELLE, LAGERSILO, MASCHINE,
    PARTNER_BEARBEITET, PERSONAL, PERSONAL_BEARBEITET, SEX, SONSTIGES, STANDORT, VERTRIEB, VORRAETE, WARTUNG, ZUSTAND, //22

    /*Inventar*/INVENTARLISTE,VORRAETEMITSTANDORT,LAGERSILOLISTE,MASCHINENLISTE,FELDERLISTE,BENOETIGTEVORRAETE,GEERNTETEPRODUKTE,/*SUMMEVORRAETE,*/
    /*Personal*/PERSONALLISTE,MITARBEITERAUFTEILUNG,BEARBEITUNGSGESCHWINDIGKEIT,
    /*Aufgaben*/AUFGABENBEREICHE,AUFGABENLISTE,AUFGABENVERTEILUNG,TAETIGKEITENUEBERSICHT,
    /*Finanzen*/FINANZUEBERSICHT,KOSTENSTELLENLISTE,
    GESCHAEFTSPARTNERLISTE/*, STANDORTEBYTYP*/
//    BEARBEITUNGSGESCHWINDIGKEIT2,VORRAETELISTE,TAETIGKEITENÜBERSICHT,MITARBEITERAUFTEILUNGV1,MITARBEITERAUFTEILUNGV2
}
