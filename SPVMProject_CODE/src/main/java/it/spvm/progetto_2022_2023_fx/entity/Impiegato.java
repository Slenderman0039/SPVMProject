package it.spvm.progetto_2022_2023_fx.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Impiegato {

    private int matricola;
    private String nome;
    private String cognome;
    private Date d_nascita;
    private String mansione;
    private String servizio;
    private String n_telefono;
    private String IBAN;
    private String password;

    private String email;

    private boolean carriera_attiva;

    private String c_f;

    // Identificativo univoco dell'impiegato
    private int id;

    // Lista dei permessi presi dall'impiegato durante il mese
    private List<Integer> absences;

    // Servizio in cui lavora l'impiegato
    private int service;

    // Numero di giorni lavorativi dell'impiegato durante il mese
    private int numWorkingDays=0;

    public Impiegato(){

    }
    public Impiegato(int matricola, String cognome,String nome){
        this.matricola = matricola;
        this.cognome = cognome;
        this.nome = nome;
    }
    public Impiegato(int matricola,String cognome,String nome,Date d_nascita,String n_telefono,String mansione,String servizio,String IBAN,String password,String email,String c_f, boolean carriera_attiva){
        this.matricola = matricola;
        this.cognome = cognome;
        this.nome = nome;
        this.d_nascita = d_nascita;
        this.n_telefono = n_telefono;
        this.mansione = mansione;
        this.servizio = servizio;
        this.IBAN = IBAN;
        this.password = password;
        this.email = email;
        this.c_f = c_f;
        this.carriera_attiva = carriera_attiva;
        this.absences = new ArrayList<>();
    }

    public boolean getCarrireraAttiva(){
        return this.carriera_attiva;
    }
    public void setCarrieraAttiva(boolean carriera){
        this.carriera_attiva = carriera;
    }

    public String getCarrireraAttivaString(){
        if(this.getCarrireraAttiva()){
            return "Si";
        }
        return "No";
    }
    public int getMatricola(){
        return this.matricola;
    }
    public String getPassword(){
        return this.password;
    }
    public String getIBAN(){
        return this.IBAN;
    }
    public String getN_telefono(){
        return this.n_telefono;
    }
    public String getMansione(){
        return this.mansione;
    }

    public String getServizio(){
        return servizio;
    }

    public String getEmail(){return this.email;}
    public String getC_f(){return this.c_f;}
    public Date getD_nascita(){
        return d_nascita;
    }
    public String getCognome(){
        return this.cognome;
    }
    public String getNome(){
        return this.nome;
    }

    public void setEmail(String email){ this.email = email;}
    public void setC_f(String c_f){this.c_f = c_f;}

    public void setNome(String nome){
        this.nome = nome;
    }
    public void setCognome(String cognome){
        this.cognome = cognome;
    }

    public void setD_nascita(Date d_nascita){
        this.d_nascita = d_nascita;
    }
    public void setN_telefono(String n_telefono){
        this.n_telefono = n_telefono;
    }
    public void setMatricola(int matricola){
        this.matricola = matricola;
    }
    public void setMansione(String mansione){
        this.mansione = mansione;
    }
    public void setServizio(String servizio){
        this.servizio = servizio;
    }
    public void setIBAN(String IBAN){
        this.IBAN = IBAN;
    }
    public void setPassword(String password){
        this.password = password;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Integer> absences) {
        this.absences = absences;
    }
    public int getService() {
        if(getServizio().equalsIgnoreCase("A")){
            service = 0;
        }else if(getServizio().equalsIgnoreCase("B")){
            service = 1;
        }else if(getServizio().equalsIgnoreCase("C")){
            service = 2;
        }else{
            service = 3;
        }
        return service;
    }
    public int getNumWorkingDays() {
        return numWorkingDays;
    }

    public void setNumWorkingDays(int numWorkingDays) {
        this.numWorkingDays = numWorkingDays;
    }
    public void addAbsence(int day) {
        // Se l'impiegato ha lavorato per 5 giorni consecutivi, aggiungi un solo giorno di permesso alla lista di permessi
        if (numWorkingDays % 5 == 0 && numWorkingDays!=0 ) {
            absences.add(day);
            numWorkingDays = 0;
        }

        // Altrimenti, aggiungi solo un giorno di permesso
        else {
            absences.add(day);
        }
    }
    @Override
    public String toString(){
        return "("+this.getNome()+","+this.getCognome()+","+this.getMatricola()+","+this.getD_nascita()+","+this.getN_telefono()+","+this.getMansione()+","+this.getServizio()+","+this.getIBAN()+","+this.getPassword()+")";
    }
}
