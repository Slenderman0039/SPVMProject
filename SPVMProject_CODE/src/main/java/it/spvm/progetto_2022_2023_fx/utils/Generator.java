package it.spvm.progetto_2022_2023_fx.utils;

import it.spvm.progetto_2022_2023_fx.entity.Impiegato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Generator {

    //metodo che genera il calendario trimestrale partendo da una data (from_data)
    public static void generateShifts(String from_data){
        // Numero di impiegati
        int numEmployees = DBMSManager.queryGetCountImpiegatiAdmin();

        // Numero di servizi
        int numServices = 4;

        // Lista di impiegati
        List<Impiegato> employees = DBMSManager.queryImpiegatiTurni();

        // Generatore di numeri casuali
        Random random = new Random();

        // Crea una lista di impiegati assegnati a un servizio in modo casuale
        for (int i = 0; i < numEmployees; i++) {
            employees.get(i).setId(i);
            employees.get(i).setNumWorkingDays(random.nextInt(5));
        }

        // Numero di giorni del mese
        int numDays = 72;

        DBMSManager.startConnection();
        Connection conn = DBMSManager.getConnection();
        for (int i = 0; i < numEmployees; i++) {
            employees.get(i).setId(i + 1000);
        }
        for(Impiegato k: employees) {
            String dt = DBMSManager.queryUltimoTurno();
            for (int d = 0; d < numDays; d++) {

                // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(dt));
                }catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                c.add(Calendar.DATE, 1);
                String query= "SELECT Ref_impiegato,Data_inizio_permesso,Data_fine_permesso\n" +
                        "FROM permessi\n" +
                        "WHERE (? BETWEEN DATE(Data_inizio_permesso) AND DATE(Data_fine_permesso)) AND  Ref_impiegato=?";
                dt = sdf.format(c.getTime());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(dt, formatter);
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1,localDate.toString());
                    stmt.setInt(2,k.getId());
                    var r= stmt.executeQuery();
                    if(r.next()) {
                        k.getAbsences().add(d);
                    }
                }catch(SQLException e){
                    System.out.println("Errore durante la comunicazione con il DBMS [ERROR CODE " + e.getErrorCode()+"]\nMessaggio di errore:"+ e.getMessage());
                }
            }
        }
        for (int i = 0; i < numEmployees; i++) {
            employees.get(i).setId(employees.get(i).getId()-1000);
        }

        int[] k = {5,4,3,2};
        Scheduler schedule = new Scheduler(employees, numServices,k);

        // Stampa la timetable

        schedule.printTimetable();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.print("[");
        for(int i=0;i<employees.size();++i){
            System.out.print(employees.get(i).getMatricola()+",");
        }
        System.out.println("]");
        ArrayList<LocalDate> all_date = Scheduler.FromNumberToDate(from_data,numDays);
        ArrayList<String> all_services = (Scheduler.ServicesConverter(Scheduler.timetable, numEmployees, numDays));
        ArrayList<String> all_shifts = Scheduler.ShiftsConverter(Scheduler.timetable, numEmployees, numDays);

        for(int i=0;i<employees.size(); ++i){
            for(int j=0;j<all_date.size();++j){
                int index = (numDays*i)+j;
                if(!all_services.get(index).equalsIgnoreCase("null")){
                    System.out.println("==== Employee " + employees.get(i).getId()+" ===");
                    System.out.println("[[[ GIORNO: " + all_date.get(j)+" ]]]");
                    System.out.println("SERVIZIO: " + all_services.get(index));
                    System.out.println("TURNO : " + all_shifts.get(index));
                    String[] turno = all_shifts.get(index).split("-");

                    System.out.println("DALLE " + turno[0].trim());
                    System.out.println("ALLE " + turno[1].trim());

                    DBMSManager.queryInsertTurni(employees.get(i).getMatricola(),all_date.get(j)+" "+turno[0].trim()+":00",all_services.get(index),all_date.get(j)+" "+turno[1].trim()+":00");
                }else{
                    System.out.println("SERVIZIO: GIORNO LIBERO");
                    System.out.println("TURNO : GIORNO LIBERO");
                }
            }
        }
    }
}
