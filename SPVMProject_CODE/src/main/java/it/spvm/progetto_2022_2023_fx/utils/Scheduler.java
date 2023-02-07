package it.spvm.progetto_2022_2023_fx.utils;

import it.spvm.progetto_2022_2023_fx.entity.Impiegato;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
public class Scheduler {
    /*
     *  timetable[impiegati][giorni] = "servizio" - "turno" rappresenta una matrice in cui l'elemento (i,j) rappresenta un turno in un determinato servizio per quell'impiegato nel giorno scelto
     *  numImpiegati[][] serve semplicemente a contare in un determinato giorno, quanti impiegati per servizio e turno ci sono così in caso da riempire i "buchi"
     * list<Employee> lista degli impiegati per prelevare le informazioni (matricola,servizio,lista dei permessi)
     *  prende in input anche il numero di servizi ed il numero di impiegati per servizio
     *
     */
    public static String[][] timetable;
    private int[][] numImpiegati;
    private List<Impiegato> employees;
    private int numServices;
    private int[] numImpiegatiperServ;
    /*
     *  costruttore
     */
    public Scheduler(List<Impiegato> employees,int numServices, int[] numImpiegatiperServ){
        this.employees=employees;
        this.numServices=numServices;
        timetable = new String[employees.size()][72];
        this.numImpiegatiperServ = numImpiegatiperServ;
        this.numImpiegati= new int[numServices][3];
        generateTimetable();
        numImpiegati = new int[numServices][3];
    }
    /*
     *  metodo che genera la timetable
     */
    private void generateTimetable(){
        int numDays = 72;
        int i=0;
        String prova;
        //per ogni giorno
        for(int d = 0; d < numDays; d++){
            //per ogni servizio
            for(int s = 0; s < numServices;  s++){
                //per ogni turno
                for(int t = 0; t < 3; t++){
                    //contatore per contare quanti impiegati sono stati assegnati per questo turno in questo servizio
                    i=0;
                    //cerca per ogni impiegato
                    for(Impiegato e : employees){
                        //l'impiegato per quel servizio che non ha un permesso per quel giorno e che non gli è stato assegnato un turno per quel giorno
                        if(e.getService() == s && !(e.getAbsences().contains(d)) && timetable[e.getId()][d]== null){
                            /*
                             *  tutta questa parte (cilo + if) serve a randomizzare quanto più possibile i turni cercando di rendere il più randomica possibile l'asegnazione del turno
                             * il ciclo serve a ricercare il turno precedente, verifica quale sia il turno precedentemente fatto se è nel turno 2 randomizza i turni 0 e 1 altrimenti rompe il ciclo dell'impiegato
                             *
                             */
                            int l=1;
                            while(d-l>0){
                                if(timetable[e.getId()][d-l]!=null)
                                    break;
                                l++;
                            }
                            if(d!=0 && timetable[e.getId()][d-l]!= null){
                                prova= new String(timetable[e.getId()][d-l]);
                                char prova2 = prova.charAt(prova.length()-1);
                                if(Character.getNumericValue(prova2)==t){
                                    if(t==2){
                                        Random r= new Random();
                                        int y= r.nextInt(1);
                                        timetable[e.getId()][d] = s + " - " + y;
                                        numImpiegati[s][y]++;
                                    }
                                    continue;
                                }
                            }
                                 /*
                                  * parte di assegnazione vera e propria, in sostanza assegna alla cella (i,j) dove i è l'id dell'impiegato e j il giorno: il turno come scritto come stringa
                                    poi aggiunge un giorno di lavoro
                                    arrivati a 5 il giorno successivo non lavorerà
                                  */
                            timetable[e.getId()][d]= s + " - " + t;
                            numImpiegati[s][t]++;
                            i++;
                            e.setNumWorkingDays(e.getNumWorkingDays() +1);
                            if(e.getNumWorkingDays()==5){
                                e.addAbsence(d+1);
                            }
                            //rompe il ciclo se per quel determinato servizio (ovvero turno) ci sono abbastanza impiegati
                            if(i>numImpiegatiperServ[s])
                                break;

                        }
                    }
                } //fine ciclo turni
            } // fine ciclo servizio
            /*
             * questo ciclo è molto simile a quello degli impiegati di poco fa perché
             * in questa zona del codice terminano i cicli dei turni e dei servizi (ma non in quello dei giorni)
             * serve ad assegnare
             */
            for(int k=0;k<numServices; k++){
                for(int l=0;l<3;l++){
                    for(Impiegato e : employees){
                        if(!(e.getAbsences().contains(d)) && timetable[e.getId()][d]== null &&  numImpiegati[k][l]<numImpiegatiperServ[k]){
                            int a=1;
                            while(d-a>0){
                                if(timetable[e.getId()][d-a]!=null)
                                    break;
                                a++;
                            }
                            if(d!=0 && timetable[e.getId()][d-a]!= null){
                                prova= new String(timetable[e.getId()][d-a]);
                                char prova2 = prova.charAt(prova.length()-1);
                                if(Character.getNumericValue(prova2)==l){
                                    if(l==2){
                                        Random r= new Random();
                                        int y= r.nextInt(1);
                                        timetable[e.getId()][d] = k + " - " + y;
                                        numImpiegati[k][y]++;
                                    }
                                    continue;
                                }
                            }
                            timetable[e.getId()][d]= k + " - " + l;
                            numImpiegati[k][l]++;
                            e.setNumWorkingDays(e.getNumWorkingDays() +1);
                            if(e.getNumWorkingDays()==5){
                                e.addAbsence(d+1);
                            }
                        }
                    }
                }
            }
        }
    }
    public void printTimetable(){
        int numDays = 72;
        System.out.print("Employee\\Day\t");
        for (int d = 0; d < numDays; d++) {
            System.out.print(d + 1 + "\t");
        }

        System.out.println();
        for(int k = 0; k< employees.size(); k++){
            System.out.print(employees.get(k).getId() + "\t\t");
            for(int j=0; j < numDays; j++){
                System.out.print(" " + timetable[k][j]);
            }
            System.out.println();
        }
    }

    public static ArrayList<LocalDate> FromNumberToDate(String startDate, int numDays){
        ArrayList<LocalDate> date_turni = new ArrayList<>();
        String dt = startDate;  // Start date

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();

        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        c.add(Calendar.DATE, -1);
        for(int i=0;i<numDays;++i){
            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dt, formatter);
            date_turni.add(localDate);
        }
        return date_turni;
    }

    private static ArrayList<String> ServicesExtractor(String timetable[][], int employee_size, int numDays){
        ArrayList<String> lista_servizi = new ArrayList<>();
        for(int i=0;i<employee_size; ++i){
            for(int j=0; j<numDays;j++){
                if(timetable[i][j] != null){
                    String value = timetable[i][j];
                    String servizio = value.charAt(0)+"";
                    lista_servizi.add(servizio);
                }else{
                    lista_servizi.add("null");
                }
            }
        }
        return lista_servizi;
    }

    public static ArrayList<String> ServicesConverter(String timetable[][], int employee_size, int numDays){
        ArrayList<String> servicesint= ServicesExtractor( timetable,  employee_size,  numDays);
        ArrayList<String> services = new ArrayList<>();
        for(int i=0;i<servicesint.size();++i){
            if(servicesint.get(i).equalsIgnoreCase("0")){
                services.add("A");
            } else if (servicesint.get(i).equalsIgnoreCase("1")) {
                services.add("B");
            }else if (servicesint.get(i).equalsIgnoreCase("2")) {
                services.add("C");
            }else if (servicesint.get(i).equalsIgnoreCase("3")) {
                services.add("D");
            }else{
                services.add("null");
            }
        }
        return services;
    }

    public static ArrayList<String> ShiftsExtractor(String timetable[][], int employee_size, int numDays){
        ArrayList<String> lista_servizi = new ArrayList<>();
        for(int i=0;i<employee_size; ++i){
            for(int j=0; j<numDays;j++){
                if(timetable[i][j] != null){
                    String value = timetable[i][j];
                    String servizio = value.charAt(4)+"";
                    lista_servizi.add(servizio);
                }else{
                    lista_servizi.add("null");
                }
            }
        }
        return lista_servizi;
    }

    public static ArrayList<String> ShiftsConverter(String timetable[][], int employee_size, int numDays){
        ArrayList<String> shiftsint= ShiftsExtractor( timetable,  employee_size,  numDays);
        ArrayList<String> shifts = new ArrayList<>();
        for(int i=0;i<shiftsint.size();++i){
            if(shiftsint.get(i).equalsIgnoreCase("0")){
                shifts.add("00:01 - 7:59");
            } else if (shiftsint.get(i).equalsIgnoreCase("1")) {
                shifts.add("8:00 - 15:59");
            }else if (shiftsint.get(i).equalsIgnoreCase("2")) {
                shifts.add("16:00 - 23:59");
            }else{
                shifts.add("null");
            }
        }
        return shifts;
    }
}

