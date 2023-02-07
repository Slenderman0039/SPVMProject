package it.spvm.progetto_2022_2023_fx.entity;

import java.time.LocalDate;
import java.time.LocalTime;
public class Turno{
    private LocalDate giorno;
    private LocalTime orario_inizio;
    private LocalTime orario_fine;
    private String servizio;

    public Turno(LocalDate giorno, LocalTime orario_inizio, LocalTime orario_fine, String servizio){
        this.giorno = giorno;
        this.orario_inizio = orario_inizio;
        this.orario_fine = orario_fine;
        this.servizio = servizio;
    }

    public Turno(){
        ;
    }
    public void setGiorno(LocalDate giorno) {
        this.giorno = giorno;
    }

    public void setOrario_fine(LocalTime orario_fine) {
        this.orario_fine = orario_fine;
    }

    public void setOrario_inizio(LocalTime orario_inizio) {
        this.orario_inizio = orario_inizio;
    }

    public void setServizio(String servizio) {
        this.servizio = servizio;
    }
    public LocalDate getGiorno(){
        return this.giorno;
    }

    public LocalTime getOrario_Inizio(){
        return this.orario_inizio;
    }

    public LocalTime getOrario_Fine(){
        return this.orario_fine;
    }

    public String getServizio(){
        return this.servizio;
    }

    @Override
    public String toString(){
        return "("+this.getGiorno()+","+this.getOrario_Inizio()+","+this.getOrario_Fine()+","+this.getServizio()+")";
    }
}