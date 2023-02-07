package it.spvm.progetto_2022_2023_fx.entity;

public class Stipendio extends Impiegato{

    private String data;
    private String causale;
    private float stipendio;

    public Stipendio(String data, String causale, float totale){
        this.data = data;
        this.causale = causale;
        this.stipendio = totale;
    }

    public String getData() {
        return data;
    }

    public String getCausale() {
        return causale;
    }
    public float getStipendio(){ return stipendio;}

    @Override
    public String toString(){
        return "("+this.getStipendio()+","+this.getCausale()+","+this.getData()+")";
    }

}
