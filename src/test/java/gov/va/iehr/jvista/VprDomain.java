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


    VITAL("vital"),
    PROBLEM("problem"),
    ALLERGY("allergy"),
    ORDER("order"),
    TREATMENT("treatment"),
    MED("med"),
    CONSULT("consult"),
    PROCEDURE("procedure"),
    OBS("obs"),
    LAB("lab"),
    IMAGE("image"),
    SURGERY("surgery"),
    DOCUMENT("document"),
    MH("mh"),
    IMMUNIZATION("immunization"),
    POV("pov"),
    SKIN("skin"),
    EXAM("exam"),
    CPT("cpt"),
    EDUCATION("education"),
    FACTOR("factor"),
    APPOINTMENT("appointment"),
    VISIT("visit"),
    PTF("ptf");

    private String id;

    VprDomain(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
