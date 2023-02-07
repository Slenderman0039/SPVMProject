package it.spvm.progetto_2022_2023_fx.entity;

public class Notifica {
    private int ref_impiegato;
    private String data_notifica;
    private String messaggio;
    private String titolo;
    private boolean letta;

    public Notifica(int ref_impiegato, String data_notifica, String messaggio, String titolo, boolean letta){
        this.ref_impiegato = ref_impiegato;
        this.data_notifica = data_notifica;
        this.messaggio = messaggio;
        this.titolo = titolo;
        this.letta = letta;
    }

    public int getRef_Impiegato(){
        return this.ref_impiegato;
    }

    public String getData_Notifica(){
        return this.data_notifica;
    }

    public String getMessaggio(){
        return this.messaggio;
    }
    
    public String getTitolo(){
        return this.titolo;
    }

    public String getLettaString(){
        if(getLetta()){
         return "Si";
        }else{
            return "No";
        }
    }

    public boolean getLetta(){
        return this.letta;
    }

    @Override
    public String toString(){
        return "("+this.getRef_Impiegato()+","+this.getData_Notifica()+","+this.getTitolo()+","+this.getMessaggio()+","+","+this.getLetta()+")";
    }
}
