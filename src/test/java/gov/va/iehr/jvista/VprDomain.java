/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.va.iehr.jvista;

/**
 *
 * @author gaineys
 */
public enum VprDomain {
    
    ACCESSION("accession"),
    ALLERGY("allerg"),  // also "reaction"
    APPOINTMENT("appointment"),
    CLINICAL_PROCEDURE("clinicalProc"),
    CONSULT("consult"),
    DOCUMENT("document"),
    FLAG("flag"),
    HEALTH_FACTOR("factor"),
    IMMUNIZATION("immunization"),
    SKIN_TEST("skin"),
    EXAM("exam"), //  14 I X?1"exam".E      S Y="VPRDPXAM",X="exams"
    LAB("lab"),
    EDUCATION_TOPICS("educat"),
    INSURANCE_POLICES("insur"),
    PANEL("panel"),
    MED("med"),             // also "pharm"
    OBSERVATION("observ"),
    ORDER("order"),
    PROBLEM("problem"),
    PROCEDURE("procedure"),
    SURGERY("surg"),
    VISIT("visit"),
    VITAL("vital"),
    RADIOOLOGY("rad"),      // also "xray"
    NEW("new");    

    private String id;
    
    VprDomain(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
}
