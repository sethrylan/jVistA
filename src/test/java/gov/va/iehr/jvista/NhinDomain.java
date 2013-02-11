package gov.va.iehr.jvista;

/**
 * @author gaineys
 */
public enum NhinDomain {

    ACCESSION("accession"),
    ALLERGY("allergy"),
    APPOINTMENT("appointment"),
    CONSULT("consult"),
    DOCUMENT("document"),
    IMMUNIZATION("immunization"),
    LAB("lab"),
    PANEL("panel"),
    MED("med"),
    RX("rx"),
    ORDER("order"),
    PATIENT("patient"),
    PROBLEM("problem"),
    PROCEDURE("procedure"),
    SURGERY("surgery"),
    VISIT("visit"),
    VITAL("vital"),
    RADIOOLOGY("radiology"),
    NEW("new");    

    private String id;
    
    NhinDomain(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
        
}
