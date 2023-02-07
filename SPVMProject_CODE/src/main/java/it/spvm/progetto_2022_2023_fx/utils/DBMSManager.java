package it.spvm.progetto_2022_2023_fx.utils;

import it.spvm.progetto_2022_2023_fx.entity.Impiegato;
import it.spvm.progetto_2022_2023_fx.entity.Notifica;
import it.spvm.progetto_2022_2023_fx.entity.Stipendio;
import it.spvm.progetto_2022_2023_fx.entity.Turno;
import javafx.scene.control.DatePicker;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class DBMSManager {

    private static final String baseUrl = "";

    private static final int port = 3306;

    private static final String user = "";

    private static final String pass = "";

    private static final String database = "";

    private static Connection conn = null;

    private static Impiegato impiegato;


    //ISTANZIO UNA NUOVA CONNESSIONE
    public static void startConnection() {
        String connectionString = "jdbc:mariadb://" + baseUrl + ":" + port + "/" + database + "?user=" + user + "&password=" + pass;
        try {
            if(conn == null || conn.isClosed()){
                conn = DriverManager.getConnection(connectionString);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //CHIUSURA DELLA CONNESSIONE
    public void closeConnection(){
        try {
            if(conn.isClosed() || conn == null)
                return;
            else
                conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //GET CONNECTION
    public static Connection getConnection(){
        startConnection();
        return conn;
    }

    //GET CURRENT IMPIEGATO
    public static Impiegato getCurrentImpiegato(){
        return impiegato;
    }
    //GET IMPIEGATO
    public static Impiegato getImpiegatoWhere(int matricola, String password){
            if(queryVerificaEsistenzaImpiegato(matricola)){
                if(queryVerificaCredenzialiImpiegato(matricola,password)){
                    if(!impiegato.getCarrireraAttiva()){
                        Alert.showDialog(false,"Errore","La tua carriera è stata disabilitata. Contatta un amministratore.");
                        impiegato = new Impiegato();
                    }else{
                        return impiegato;
                    }
                }else{
                    Alert.showDialog(false,"Errore","Password non corretta!");
                }
            }else{
                Alert.showDialog(false,"Errore","Matricola non valida!");

            }
            return null;
    }
    public static boolean queryRecuperaPasswordImpiegato(String email,String password){
        startConnection();
        String query = "UPDATE impiegati SET impiegati.password = ? WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, DigestUtils.sha1Hex(password));
            stmt.setString(2, email);
            var r = stmt.executeUpdate();
            return r == 1;
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }



    //VERIFICA CREDENZIALI IMPIEGATO
    private static boolean queryVerificaCredenzialiImpiegato(int matricola,String password) {
        startConnection();
        String query = "SELECT * FROM impiegati WHERE impiegati.matricola = ? AND impiegati.password=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            stmt.setString(2, DigestUtils.sha1Hex(password));
            var r = stmt.executeQuery();
            if (r.next()) {
                impiegato = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                return true;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }
    public static boolean queryVerificaIndirizzoEmail(String email)
    {
        startConnection();
        String query = "SELECT * FROM impiegati WHERE impiegati.email = ? AND carriera_attiva = 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            var r = stmt.executeQuery();
            if (r.next()) {
                return true;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    //QUERY ESISTENZA IMPIEGATO == RETURN TRUE (IMPIEGATO ESISTE) == RETURN FALSE (IMPIEGATO NON ESISTE)
    public static boolean queryVerificaEsistenzaImpiegato(int matricola) {
        startConnection();
        String query = "SELECT impiegati.matricola FROM impiegati WHERE matricola = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            var r = stmt.executeQuery();
            if (r.next()) {
                return true;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }
    public static boolean queryVerificaEsistenzaImpiegato(int matricola, String nome, String cognome) {
        startConnection();
        String query = "SELECT * FROM impiegati WHERE Matricola = ? AND Nome like ? AND Cognome like ? AND carriera_attiva = 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            var r = stmt.executeQuery();
            if (r.next()) {
                return true;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }


        return false;
    }
    public static boolean queryVerificaEsistenzaImpiegatoAttivo(int matricola) {
        startConnection();
        String query = "SELECT impiegati.matricola FROM impiegati WHERE Matricola = ? AND carriera_attiva = 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            var r = stmt.executeQuery();
            if (r.next()) {
                return true;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    //ERRORE DI COMUNICAZIONE CON IL DBMS
    private static void erroreComunicazioneDBMS(SQLException e) {
        System.out.println("Errore durante la comunicazione con il DBMS [ERROR CODE " + e.getErrorCode()+"]\nMessaggio di errore:"+ e.getMessage());
    }

    //AGGIORNA PASSWORD
    public static boolean queryAggiornaPassword(String nuovaPSW){
        startConnection();
            String query = "UPDATE impiegati SET password = ? WHERE matricola = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, DigestUtils.sha1Hex(nuovaPSW));
                stmt.setInt(2, getCurrentImpiegato().getMatricola());
                var r = stmt.executeUpdate();
                return r==1;
            }catch(SQLException e){
                erroreComunicazioneDBMS(e);
            }
        return false;
    }
    
    //AGGIORNA NUMERO DI TELEFONO
    public static boolean queryAggiornaNumeroTelefono(String n_telefono){
        startConnection();
        String query = "UPDATE impiegati SET N_Telefono = ? WHERE matricola = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, n_telefono);
                stmt.setInt(2, getCurrentImpiegato().getMatricola());
                var r = stmt.executeUpdate();
                return r == 1;
            }catch(SQLException e){
            erroreComunicazioneDBMS(e);
            }
        return false;
    }

    //AGGIORNA IBAN
    public static boolean queryAggiornaIBAN(String IBAN){
        startConnection();
        String query = "UPDATE impiegati SET iban = ? WHERE matricola = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, IBAN);
                stmt.setInt(2, getCurrentImpiegato().getMatricola());
                var r = stmt.executeUpdate();
                return r == 1;
            }catch(SQLException e){
            erroreComunicazioneDBMS(e);
            }
        return false;
    }
    
    //GET STIPENDI
    public static ArrayList<Stipendio> queryStipendioWhere(int matricola){
        startConnection();
        String query = "SELECT stipendi.data_accredito, stipendi.stipendio, stipendi.causale FROM stipendi WHERE ref_impiegato = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            var r = stmt.executeQuery(); 
            if(r.next()){
                ArrayList<Stipendio> lista_stipendi = new ArrayList<>();
                Stipendio stipendio = new Stipendio(r.getString("data_accredito"),r.getString("causale"), r.getFloat("stipendio"));
                lista_stipendi.add(stipendio);
                while(r.next()){
                    Stipendio stipendio_next = new Stipendio(r.getString("data_accredito"),r.getString("causale"), r.getFloat("stipendio"));
                    lista_stipendi.add(stipendio_next);
                }
                return lista_stipendi;
            }
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static boolean queryInserisciPermesso(String data_inizio_permesso,String data_fine_permesso, String tipo_permesso) {
        startConnection();
        String inizio_convertita = data_inizio_permesso;
        String fine_convertita = data_fine_permesso;
        if(data_inizio_permesso.contains(".")){
             inizio_convertita = data_inizio_permesso.substring(0,data_inizio_permesso.indexOf("."));
        }
        if(data_fine_permesso.contains(".")){
            fine_convertita = data_fine_permesso.substring(0,data_fine_permesso.indexOf("."));
        }
        String query2 ="SELECT Ref_impiegato,Data_inizio_permesso,Data_fine_permesso FROM permessi WHERE Ref_impiegato=? AND DATE(Data_inizio_permesso)=DATE(?) AND DATE(Data_fine_permesso)=DATE(?)";
        try (PreparedStatement stmt2 = conn.prepareStatement(query2)) {
            stmt2.setInt(1,getCurrentImpiegato().getMatricola());
            stmt2.setString(2,inizio_convertita);
            stmt2.setString(3,fine_convertita);
            var r = stmt2.executeQuery();
            if(r.next()){
                Alert.showDialog(false,"Errore","Hai già richiesto un permesso per quelle date.");
            }else{
        String query1 = "SELECT * FROM permessi WHERE (Data_inizio_permesso BETWEEN ? AND ?) OR (Data_fine_permesso BETWEEN ? AND  ?) AND Ref_impiegato = ?;";
        try (PreparedStatement stmt1 = conn.prepareStatement(query1)) {
            stmt1.setString(1, inizio_convertita);
            stmt1.setString(2, fine_convertita);
            stmt1.setString(3, inizio_convertita);
            stmt1.setString(4, fine_convertita);
            stmt1.setInt(5, getCurrentImpiegato().getMatricola());
            var r1 = stmt1.executeQuery();
            if(r1.next()){
                Alert.showDialog(false,"Errore","Esiste già un permesso per quelle date.");
            }else{
                String query = "INSERT INTO permessi (Ref_impiegato,Data_inizio_permesso, Data_fine_permesso, Tipo_permesso) VALUES (?,?,?,?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, getCurrentImpiegato().getMatricola());
                    stmt.setString(2, inizio_convertita);
                    stmt.setString(3, fine_convertita);
                    stmt.setString(4, tipo_permesso);
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        Alert.showDialog(false, "Errore", "L'impiegato possiede già un permesso per quel giorno-ora!");
                    } else {
                        Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                        erroreComunicazioneDBMS(e);
                    }
                }
                return false;
            }
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
            }
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryInserisciPermessoAdmin(int matricola, String data_inizio_permesso,String data_fine_permesso, String tipo_permesso) {
        startConnection();
        String inizio_convertita = data_inizio_permesso;
        String fine_convertita = data_fine_permesso;
        if(data_inizio_permesso.contains(".")){
            inizio_convertita = data_inizio_permesso.substring(0,data_inizio_permesso.indexOf("."));
        }
        if(data_fine_permesso.contains(".")){
            fine_convertita = data_fine_permesso.substring(0,data_fine_permesso.indexOf("."));
        }
        String query2 ="SELECT Ref_impiegato,Data_inizio_permesso,Data_fine_permesso FROM permessi WHERE Ref_impiegato=? AND DATE(Data_inizio_permesso)=DATE(?) AND DATE(Data_fine_permesso)=DATE(?)";
        try (PreparedStatement stmt2 = conn.prepareStatement(query2)) {
            stmt2.setInt(1,matricola);
            stmt2.setString(2,inizio_convertita);
            stmt2.setString(3,fine_convertita);
            var r = stmt2.executeQuery();
            if(r.next()){
                Alert.showDialog(false,"Errore","Hai già richiesto un permesso per quelle date.");
            }else {
                String query1 = "SELECT * FROM permessi WHERE (Data_inizio_permesso BETWEEN ? AND ?) OR (Data_fine_permesso BETWEEN ? AND  ?) AND Ref_impiegato = ?;";
                try (PreparedStatement stmt1 = conn.prepareStatement(query1)) {
                    stmt1.setString(1, inizio_convertita);
                    stmt1.setString(2, fine_convertita);
                    stmt1.setString(3, inizio_convertita);
                    stmt1.setString(4, fine_convertita);
                    stmt1.setInt(5, matricola);
                    var r1 = stmt1.executeQuery();
                    if (r1.next()) {
                        Alert.showDialog(false, "Errore", "Esiste già un permesso per quelle date.");
                    } else {
                        String query = "INSERT INTO permessi (Ref_impiegato,Data_inizio_permesso, Data_fine_permesso, Tipo_permesso) VALUES (?,?,?,?)";
                        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                            stmt.setInt(1, matricola);
                            stmt.setString(2, inizio_convertita);
                            stmt.setString(3, fine_convertita);
                            stmt.setString(4, tipo_permesso);
                            return stmt.executeUpdate() > 0;
                        } catch (SQLException e) {
                            if (e.getErrorCode() == 1062) {
                                Alert.showDialog(false, "Errore", "L'impiegato possiede già un permesso per quel giorno-ora!");
                            } else {
                                Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                                erroreComunicazioneDBMS(e);
                            }
                        }
                        return false;
                    }
                } catch (SQLException e) {
                    erroreComunicazioneDBMS(e);
                }
                return false;
            }
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryInserisciPresenzaRitardo(String motivazione) {
        startConnection();
        String query = "INSERT INTO presenze (Ref_impiegato,Ora_inizio,Ora_fine, motivazione) VALUES (?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, getCurrentImpiegato().getMatricola());
            stmt.setString(2, LocalDateTime.now().toString());
            stmt.setString(3, null);
            stmt.setString(4, motivazione);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryInserisciRitardoImpiegato(int matricola,String motivazione){
        startConnection();
        String query = "INSERT INTO presenze (Ref_impiegato,Ora_inizio, Ora_fine, motivazione) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            stmt.setString(2, LocalDateTime.now().toString());
            stmt.setString(3, null);
            stmt.setString(4, motivazione);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryVerificaFirma(int matricola){
        startConnection();
        String query = "SELECT * FROM presenze WHERE presenze.Ref_impiegato = ? AND presenze.Ora_inizio LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            stmt.setString(2,LocalDate.now().toString()+" %");
            var r = stmt.executeQuery();
            if(r.next()){
                return true;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean checkFirmaImpiegato(){
        startConnection();
        String query = "SELECT * FROM presenze WHERE presenze.Ref_impiegato = ? AND presenze.Ora_inizio LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, getCurrentImpiegato().getMatricola());
            stmt.setString(2,LocalDate.now().toString()+" %");
            var r = stmt.executeQuery();
            if(r.next()){
                return true;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }




    public static ArrayList<Impiegato> queryImpiegati() {
        startConnection();
        ArrayList<Impiegato> impiegati;
        String query = "SELECT * FROM impiegati";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            if (r.next()) {
                impiegati = new ArrayList<>();
                Impiegato impiegato = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                impiegati.add(impiegato);
                while (r.next()) {
                    Impiegato impiegato_next = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                    impiegati.add(impiegato_next);
                }
                return impiegati;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static ArrayList<Impiegato> queryImpiegatiAttivi() {
        startConnection();
        ArrayList<Impiegato> impiegati;
        String query = "SELECT * FROM impiegati WHERE carriera_attiva = 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            if (r.next()) {
                impiegati = new ArrayList<>();
                Impiegato impiegato = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                impiegati.add(impiegato);
                while (r.next()) {
                    Impiegato impiegato_next = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                    impiegati.add(impiegato_next);
                }
                return impiegati;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static ArrayList<Impiegato> queryImpiegatiOrderBySurname() {
        startConnection();
        ArrayList<Impiegato> impiegati;
        String query = "SELECT * FROM impiegati order by impiegati.Cognome";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            if (r.next()) {
                impiegati = new ArrayList<>();
                Impiegato impiegato = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                impiegati.add(impiegato);
                while (r.next()) {
                    Impiegato impiegato_next = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                    impiegati.add(impiegato_next);
                }
                return impiegati;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }
    public static ArrayList<Stipendio> queryStipendiDipendenti(){
        startConnection();
        String query = "SELECT i.Matricola, i.Nome, i.Cognome, s.data_accredito, s.causale, s.stipendio FROM impiegati i, stipendi s WHERE s.Ref_impiegato = i.Matricola order by s.data_accredito DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            var r = stmt.executeQuery();
            if(r.next()){
                ArrayList<Stipendio> stipendi = new ArrayList<>();

                Stipendio stipendio = new Stipendio(r.getString("data_accredito"),r.getString("causale"), r.getFloat("stipendio"));
                stipendio.setNome(r.getString("Nome"));
                stipendio.setCognome(r.getString("Cognome"));
                stipendio.setMatricola(r.getInt("Matricola"));

                stipendi.add(stipendio);

                while(r.next()){
                    Stipendio stipendio_next = new Stipendio(r.getString("data_accredito"),r.getString("causale"), r.getFloat("stipendio"));
                    stipendio_next.setNome(r.getString("Nome"));
                    stipendio_next.setCognome(r.getString("Cognome"));
                    stipendio_next.setMatricola(r.getInt("Matricola"));

                    stipendi.add(stipendio_next);
                }
                return stipendi;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static ArrayList<Turno> queryTurniImpiegato(){
        startConnection();
        String query = "SELECT * FROM turni WHERE turni.Ref_impiegato = ? AND turni.inizio_turno >= now()";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1,getCurrentImpiegato().getMatricola());
            var r = stmt.executeQuery();
            ArrayList<Turno> turni = new ArrayList<>();
            if(r.next()){
                Timestamp inizio_timestamp= (Timestamp) r.getObject("inizio_turno");
                Timestamp fine_timestamp=   (Timestamp) r.getObject("fine_turno");
                String servizio = r.getString("Servizio");
                Turno t = new Turno(inizio_timestamp.toLocalDateTime().toLocalDate(),inizio_timestamp.toLocalDateTime().toLocalTime(),fine_timestamp.toLocalDateTime().toLocalTime(),servizio);
                turni.add(t);
                while(r.next()){
                    Timestamp inizio_timestamp_next= (Timestamp) r.getObject("inizio_turno");
                    Timestamp fine_timestamp_next=   (Timestamp) r.getObject("fine_turno");
                    String servizio_next = r.getString("Servizio");
                    Turno t1 = new Turno(inizio_timestamp_next.toLocalDateTime().toLocalDate(),inizio_timestamp_next.toLocalDateTime().toLocalTime(),fine_timestamp_next.toLocalDateTime().toLocalTime(),servizio_next);
                    turni.add(t1);
                }
                return turni;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static ArrayList<Turno> queryTurniImpiegato(int matricola){
        startConnection();
        String query = "SELECT * FROM turni WHERE turni.Ref_impiegato = ? AND turni.inizio_turno >= now()";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1,matricola);
            var r = stmt.executeQuery();
            ArrayList<Turno> turni = new ArrayList<>();
            if(r.next()){
                Timestamp inizio_timestamp= (Timestamp) r.getObject("inizio_turno");
                Timestamp fine_timestamp=   (Timestamp) r.getObject("fine_turno");
                String servizio = r.getString("Servizio");
                Turno t = new Turno(inizio_timestamp.toLocalDateTime().toLocalDate(),inizio_timestamp.toLocalDateTime().toLocalTime(),fine_timestamp.toLocalDateTime().toLocalTime(),servizio);
                turni.add(t);
                while(r.next()){
                    Timestamp inizio_timestamp_next= (Timestamp) r.getObject("inizio_turno");
                    Timestamp fine_timestamp_next=   (Timestamp) r.getObject("fine_turno");
                    String servizio_next = r.getString("Servizio");
                    Turno t1 = new Turno(inizio_timestamp_next.toLocalDateTime().toLocalDate(),inizio_timestamp_next.toLocalDateTime().toLocalTime(),fine_timestamp_next.toLocalDateTime().toLocalTime(),servizio_next);
                    turni.add(t1);
                }
                return turni;
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

public static int queryGetCountImpiegati(){
    startConnection();
    String query = "SELECT COUNT(*) as NUMIMPIEGATI FROM impiegati WHERE impiegati.carriera_attiva AND impiegati.Mansione like 'Employee'";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        var r = stmt.executeQuery();
        if(r.next()){
            return r.getInt("NUMIMPIEGATI");
        }
    }catch (SQLException e) {
        erroreComunicazioneDBMS(e);
    }
    return 0;
}

    public static int queryGetCountImpiegatiAdmin(){
        startConnection();
        String query = "SELECT COUNT(*) as NUMIMPIEGATI FROM impiegati WHERE impiegati.carriera_attiva AND impiegati.Mansione like 'Employee' OR impiegati.Mansione like 'Admin'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            if(r.next()){
                return r.getInt("NUMIMPIEGATI");
            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return 0;
    }

public static ArrayList<Impiegato> queryImpiegatiTurni(){
    startConnection();
    ArrayList<Impiegato> impiegati;
    String query = "SELECT * FROM impiegati WHERE impiegati.carriera_attiva AND impiegati.Mansione like 'Employee' OR impiegati.Mansione like 'Admin'";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        var r = stmt.executeQuery();
        if (r.next()) {
            impiegati = new ArrayList<>();
            Impiegato impiegato = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
            impiegati.add(impiegato);
            while (r.next()) {
                Impiegato impiegato_next = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                impiegati.add(impiegato_next);
            }
            return impiegati;
        }
    } catch (SQLException e) {
        erroreComunicazioneDBMS(e);
    }
    return null;
}


    public static Impiegato getImpiegatoByMatricola(int matricola){
        startConnection();
        ArrayList<Impiegato> impiegati;
        String query = "SELECT * FROM impiegati WHERE impiegati.carriera_attiva = 1 AND matricola = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            var r = stmt.executeQuery();
            if (r.next()) {
                Impiegato i = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                return i;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    //LICENZIAMENTO IMPIEGATO
    public static boolean queryLicenziamentoImpiegato(int ref_impiegato){
        startConnection();
        String query = "UPDATE impiegati SET carriera_attiva = 0 WHERE Matricola = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, ref_impiegato);
            var r = stmt.executeUpdate();
            return r == 1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    //AGGIUNGI INCREMENTO ATTIVITA'
    public static boolean queryInserisciIncrementoAttivita(String inizio_ia, String fine_ia){
        startConnection();
        String query = "INSERT INTO incremento_attivita(Inizio_IA, Fine_IA) VALUES (?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, inizio_ia);
            stmt.setString(2, fine_ia);
            var r = stmt.executeUpdate();
            return r==1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryVerificaEsistenzaPeriodoIncrementoAttivita(String data_inizio,String data_fine) {
        startConnection();
        String query = "SELECT * FROM incremento_attivita WHERE (Inizio_IA BETWEEN ? AND ?) OR (Fine_IA BETWEEN ? AND ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data_inizio);
            stmt.setString(2, data_fine);
            stmt.setString(3, data_inizio);
            stmt.setString(4, data_fine);
            var r = stmt.executeQuery();
            if (r.next()) {
                return true;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryAssunzioneImpiegato(String nome, String cognome, String d_nascita, String n_telefono,  String servizio, String iban, String mail, String cod_fisc, String psw) {
        startConnection();
        String query = "INSERT INTO impiegati(nome, cognome, data_nascita, mansione, n_telefono, iban, password, email, c_f, livello) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, d_nascita);
            stmt.setString(4, "Employee");
            stmt.setString(5, n_telefono);
            stmt.setString(6, iban);
            stmt.setString(7, psw);
            stmt.setString(8, mail);
            stmt.setString(9, cod_fisc);
            stmt.setString(10, servizio);
            var r = stmt.executeUpdate();
            if(r==1) {
                    String query1 = "SELECT DATEDIFF(?,CURRENT_DATE) AS days_inclusive";
                    try (PreparedStatement stmt1 = conn.prepareStatement(query1)) {
                        stmt1.setString(1, DBMSManager.queryUltimoTurno());
                        int days=0;
                        var r1 = stmt1.executeQuery();
                        if(r1.next())
                             days = r1.getInt("days_inclusive");
                        Random random = new Random();
                        int random_num = random.nextInt(5);
                        int random_service = random.nextInt(3);
                        String dt = LocalDate.now().toString();
                        for (int i = 0; i < days; ++i) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(sdf.parse(dt));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            c.add(Calendar.DATE, 1);
                            dt = sdf.format(c.getTime());
                            if (random_num % 5 != 0) {

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate localDate = LocalDate.parse(dt, formatter);

                                if (random_service == 0) {
                                    String inizio_turno = localDate.toString() + " " + "00:01";
                                    String fine_turno = localDate.toString() + " " + "7:59";
                                    queryInsertTurni(queryGetMatricolaImpiegato(mail, nome, cognome).getMatricola(), inizio_turno, servizio, fine_turno);
                                } else if (random_service == 1) {
                                    String inizio_turno = localDate.toString() + " " + "08:00";
                                    String fine_turno = localDate.toString() + " " + "15:59";
                                    queryInsertTurni(queryGetMatricolaImpiegato(mail, nome, cognome).getMatricola(), inizio_turno, servizio, fine_turno);
                                } else {
                                    String inizio_turno = localDate.toString() + " " + "16:00";
                                    String fine_turno = localDate.toString() + " " + "23:59";
                                    queryInsertTurni(queryGetMatricolaImpiegato(mail, nome, cognome).getMatricola(), inizio_turno, servizio, fine_turno);
                                }
                            }
                            random_service = random.nextInt(3);
                            random_num++;
                        }
                    } catch (SQLException e) {
                        erroreComunicazioneDBMS(e);
                    }
                    return true;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryAggiornaTurni(Impiegato impiegato, Turno t){
        String query = "INSERT INTO Turni VALUES (?,?,?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1,impiegato.getMatricola());
            stmt.setString(2,t.getGiorno()+" " + t.getOrario_Inizio());
            stmt.setString(3,t.getServizio());
            stmt.setString(4,t.getGiorno()+" " + t.getOrario_Fine());
            var r = stmt.executeUpdate();
            return r==1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static Impiegato queryGetMatricolaImpiegato(String email, String nome, String cognome) {
        startConnection();
        String query = "SELECT * FROM impiegati WHERE impiegati.email = ? AND impiegati.nome = ? AND impiegati.cognome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            var r = stmt.executeQuery();
            if (r.next()) {
                return new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));

            }
        }catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static boolean queryRilevazioneIngresso(int matricola, String nome, String cognome){
        startConnection();
        if(queryVerificaEsistenzaImpiegato(matricola, nome, cognome)){
            if(!queryVerificaFirma(matricola)) {
                String query = "INSERT INTO presenze (Ref_impiegato) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, matricola);
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                    erroreComunicazioneDBMS(e);
                }
            }else{
                Alert.showDialog(false, "Errore", "Hai già firmato oggi!");
            }
        }else{
            Alert.showDialog(false, "Errore", "Dati inseriti non corretti!");
        }

        return false;
    }

    //RILEVAZIONE USCITA
    public static boolean queryRilevazioneUscita(int matricola, String nome, String cognome){
        startConnection();
        if(queryVerificaEsistenzaImpiegato(matricola, nome, cognome)){
            String query = "UPDATE presenze SET ora_fine = ? WHERE Ref_impiegato = ? AND Ora_inizio LIKE ? AND ora_fine = null";
            try (PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setString(1, LocalDateTime.now().toString());
                stmt.setInt(2, matricola);
                stmt.setString(3, LocalDate.now().toString()+" %");
                var r = stmt.executeUpdate();
                if(r<=0){
                    Alert.showDialog(false, "Errore", "Impossibile firmare l'uscita!");
                }
                return r > 0;
            }catch (SQLException e){
                erroreComunicazioneDBMS(e);
            }
        }else{
            Alert.showDialog(false, "Errore", "Dati inseriti non corretti!");
        }
        return false;
    }

    public static ArrayList<Impiegato> getImpiegatiMancataFirma(String i_turno,String f_turno){
        startConnection();
        ArrayList<Impiegato> impiegati;
        String query = "SELECT impiegati.* \n" +
                "            FROM impiegati, turni \n" +
                "            WHERE impiegati.Matricola = turni.Ref_impiegato AND turni.inizio_turno = ? AND turni.fine_turno = ? \n" +
                "            AND Matricola NOT IN( \n" +
                "                                    SELECT presenze.Ref_impiegato\n" +
                "                                    FROM impiegati, presenze\n" +
                "                                    WHERE impiegati.Matricola = presenze.Ref_impiegato AND presenze.Ora_inizio >= ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1,LocalDate.now()+" " + i_turno);
            stmt.setString(2,LocalDate.now()+" " + f_turno);
            stmt.setString(3,LocalDate.now()+" " + i_turno);
            var r = stmt.executeQuery();
            if (r.next()) {
                impiegati = new ArrayList<>();
                Impiegato impiegato = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                impiegati.add(impiegato);
                while (r.next()) {
                    Impiegato impiegato_next = new Impiegato(r.getInt("Matricola"),r.getString("Cognome"),r.getString("Nome"), r.getDate("Data_nascita"),r.getString("N_Telefono"),r.getString("Mansione"),r.getString("livello"),r.getString("IBAN"),r.getString("Password"), r.getString("email"), r.getString("c_f"), r.getBoolean("carriera_attiva"));
                    impiegati.add(impiegato_next);
                }
                return impiegati;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static boolean getImpiegatiMancataFirmaImpiegato(int matricola,String i_turno,String f_turno){
        startConnection();
        String query = "SELECT impiegati.* \n" +
                "            FROM impiegati, turni \n" +
                "            WHERE impiegati.Matricola = turni.Ref_impiegato AND turni.inizio_turno = ? AND turni.fine_turno = ? AND Matricola = ? \n" +
                "            AND Matricola NOT IN( \n" +
                "                                    SELECT presenze.Ref_impiegato\n" +
                "                                    FROM impiegati, presenze\n" +
                "                                    WHERE impiegati.Matricola = presenze.Ref_impiegato AND presenze.Ora_inizio >= ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1,LocalDate.now()+" " + i_turno);
            stmt.setString(2,LocalDate.now()+" " + i_turno);
            stmt.setString(3,LocalDate.now()+" " + f_turno);
            stmt.setString(4,LocalDate.now()+" " + i_turno);
            var r = stmt.executeQuery();
            if (r.next()) {
                return true;
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryRimuoviTurni(int matricola, String inizio_turno,String fine_turno){
        String query = "DELETE FROM turni WHERE Ref_impiegato = ? AND inizio_turno = ? AND fine_turno = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            stmt.setString(2, inizio_turno);
            stmt.setString(3,fine_turno);
            var r = stmt.executeUpdate();
            return r==1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }
    public static boolean queryVerificaEsistenzaTurno(int matricola,String i_permesso,String f_permesso){
        startConnection();
        String query = "SELECT * FROM turni WHERE Ref_impiegato = ? AND inizio_turno between ? and ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            stmt.setString(2, i_permesso);
            stmt.setString(3,f_permesso);
            var r = stmt.executeQuery();
            if (r.next()) {
                queryRimuoviTurni(r.getInt("Ref_impiegato"),r.getString("inizio_turno"),r.getString("fine_turno"));
                while(r.next()){
                    queryRimuoviTurni(r.getInt("Ref_impiegato"),r.getString("inizio_turno"),r.getString("fine_turno"));
                }
                return true;
            }
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;

    }
    public static boolean queryInsertTurni(int matricola, String inizio_turno, String servizio, String fine_turno){
        startConnection();
        String query = "INSERT INTO turni (Ref_impiegato, inizio_turno, Servizio, fine_turno) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            stmt.setString(2, inizio_turno);
            stmt.setString(3,servizio);
            stmt.setString(4,fine_turno);
            var r = stmt.executeUpdate();
            return r==1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static String queryUltimoTurno(){
        startConnection();
        String query = "SELECT date(turni.fine_turno) as ultimo FROM turni ORDER BY turni.fine_turno DESC;";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            if (r.next()) {
                return r.getString("ultimo");
            }
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return "";
    }

    public static  HashMap<Integer, Float> queryOreLavorative(){
        startConnection();
        int matricola = 0;
        int ore = 0;
        HashMap<Integer, Float> totali = new HashMap<Integer, Float>();

        String query = "SELECT DISTINCT(t.Ref_impiegato), SUM(CEILING(TIMESTAMPDIFF(SECOND, p.Ora_inizio, p.Ora_fine)/3600)) AS hours,  t.Servizio\n" +
                "FROM presenze p, turni t\n" +
                "WHERE p.Ref_impiegato=t.Ref_impiegato AND t.inizio_turno<=p.Ora_inizio AND p.Ora_fine<=t.fine_turno\n" +
                "GROUP BY\n" +
                "t.Ref_impiegato,t.Servizio";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            if(r.next()){
               ore = r.getInt("hours");
               matricola = r.getInt("Ref_impiegato");
               float parziale = 0;
               if(r.getString("Servizio").equals("A")){
                    parziale = ore * 8;
               }else if(r.getString("Servizio").equals("B")){
                    parziale = ore * 7;
               }else if(r.getString("Servizio").equals("C")){
                    parziale = ore * 6;
               }else if(r.getString("Servizio").equals("D")){
                    parziale = ore * 5;
               }
               if(totali.containsKey(matricola)){
                   float old_value = totali.get(matricola);
                   float totale = old_value + parziale;
                   totali.put(matricola,totale);
               }else{
                   totali.put(matricola,parziale);
               }
            }
            while(r.next()){
                ore = r.getInt("hours");
                matricola = r.getInt("Ref_impiegato");
                float parziale = 0;
                if(r.getString("Servizio").equals("A")){
                    parziale = ore * 8;
                }else if(r.getString("Servizio").equals("B")){
                    parziale = ore * 7;
                }else if(r.getString("Servizio").equals("C")){
                    parziale = ore * 6;
                }else if(r.getString("Servizio").equals("D")){
                    parziale = ore * 5;
                }
                if(totali.containsKey(matricola)){
                    float old_value = totali.get(matricola);
                    float totale = old_value + parziale;
                    totali.put(matricola,totale);
                }else{
                    totali.put(matricola,parziale);
                }
            }
            return totali;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return null;
    }
    public static boolean queryAggiornaStipendio(int matricola, String data_accredito, float stipendio, String causale){
        startConnection();
        String query = "INSERT INTO stipendi (Ref_impiegato, data_accredito, stipendio, causale) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            stmt.setString(2, data_accredito);
            stmt.setFloat(3,stipendio);
            stmt.setString(4,causale);
            var r = stmt.executeUpdate();
            return r==1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }
    public static ArrayList<Notifica> queryVisualizzaNotifiche(){
        startConnection();
        ArrayList<Notifica> notifiche = new ArrayList<>();
        String query = "SELECT n.Ref_impiegato,n.Titolo, n.Messaggio, n.Data_notifica, n.letta FROM notifiche n WHERE n.Ref_impiegato=? AND DATEDIFF(CURRENT_TIMESTAMP,n.Data_notifica)<=31 ORDER BY letta DESC;";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, DBMSManager.getCurrentImpiegato().getMatricola());
            var r = stmt.executeQuery();
            if(r.next()) {
                Notifica notifica = new Notifica(r.getInt("Ref_impiegato"),r.getString("Data_notifica"), r.getString("Messaggio"), r.getString("Titolo"),r.getBoolean("letta"));
                notifiche.add(notifica);
            }
            while (r.next()){
                Notifica notifica = new Notifica(r.getInt("Ref_impiegato"),r.getString("Data_notifica"), r.getString("Messaggio"), r.getString("Titolo"),r.getBoolean("letta"));
                notifiche.add(notifica);
            }
            return notifiche;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return null;
    }

    public static boolean queryAggiornaNotifica(String data_notifica){
        startConnection();
        String query = "UPDATE notifiche\n" +
                "SET letta = 1\n" +
                "WHERE Ref_impiegato = ? AND Data_notifica = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, getCurrentImpiegato().getMatricola());
            stmt.setString(2,data_notifica);
            var r = stmt.executeUpdate();
            return r == 1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static ArrayList<Integer> queryImpiegatiServizio(String servizio){
        startConnection();
        String query = "SELECT Ref_impiegato FROM turni_attivi WHERE Servizio like ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,servizio);
            var r = stmt.executeQuery();
            ArrayList<Integer> impiegati_servizio = new ArrayList<>();
            if(r.next()){
                impiegati_servizio.add(r.getInt("Ref_impiegato"));
            }
            while(r.next()){
                impiegati_servizio.add(r.getInt("Ref_impiegato"));
            }
            return impiegati_servizio;
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return null;
    }


    //CHIUSURA SERVIZIO
    public static ArrayList<Integer> queryChiusuraServizio(){
        startConnection();
        ArrayList<Impiegato> Imp = new ArrayList<>();
        startConnection();
        String query = "SELECT Servizio, COUNT(Ref_impiegato) AS Numero_impiegati FROM turni_attivi GROUP BY Servizio";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            var r = stmt.executeQuery();
            int[] num_impiegati = new int[4];
            while(r.next()){
                int k=0;
                num_impiegati[k]=r.getInt("Numero_impiegati");
                System.out.println("Impiegati nel servizio " + k  + ": " + num_impiegati[k]);
                k++;
            }
            if(num_impiegati[3] <= 2) {
                query = "SELECT Ref_Impiegato FROM turni_attivi WHERE Servizio = 'D'";
                try(PreparedStatement stmt2 = conn.prepareStatement(query)) {
                    r = stmt2.executeQuery();
                }
                while(r.next()) {
                    query = "UPDATE turni\n" +
                            "SET Servizio='C'\n" +
                            "WHERE  DATE(inizio_turno)=CURDATE() AND Ref_impiegato=? AND Ref_impiegato " +
                            "IN (SELECT DISTINCT(t.Ref_impiegato)\n" +
                            "FROM turni t, presenze p\n" +
                            "WHERE t.Ref_impiegato=p.Ref_impiegato AND p.Ora_fine IS NULL AND DATE(t.inizio_turno)=CURDATE() AND DATE(p.ora_inizio)=CURDATE() AND t.inizio_turno=p.Ora_inizio)";
                    try(PreparedStatement stmt3 = conn.prepareStatement(query)) {
                        stmt3.setInt(1,r.getInt("Ref_impiegato"));
                        stmt3.executeUpdate();
                        num_impiegati[3]--;
                    }
                    queryInserisciNotifica(r.getInt("Ref_impiegato"),"Sei stato spostato al servizio C.","Chiusura servizio");
                }
            }
            if(num_impiegati[2] <= 3){
                while (num_impiegati[3]>num_impiegati[2]){
                    query = "SELECT Ref_Impiegato FROM turni_attivi WHERE Servizio = 'D'";
                    try(PreparedStatement stmt2 = conn.prepareStatement(query)) {
                        r = stmt2.executeQuery();
                    }
                    while(r.next()) {
                        query = "UPDATE turni\n" +
                                "SET Servizio='C'\n" +
                                "WHERE  DATE(inizio_turno)=CURDATE() AND Ref_impiegato=? AND Ref_impiegato " +
                                "IN (SELECT DISTINCT(t.Ref_impiegato)\n" +
                                "FROM turni t, presenze p\n" +
                                "WHERE t.Ref_impiegato=p.Ref_impiegato AND p.Ora_fine IS NULL AND DATE(t.inizio_turno)=CURDATE() AND DATE(p.ora_inizio)=CURDATE() AND t.inizio_turno=p.Ora_inizio)";
                        try(PreparedStatement stmt3 = conn.prepareStatement(query)) {
                            stmt3.setInt(1,r.getInt("Ref_impiegato"));
                            stmt3.executeUpdate();
                            num_impiegati[3]--;
                        }
                        queryInserisciNotifica(r.getInt("Ref_impiegato"),"Sei stato spostato al servizio C.","Chiusura servizio");

                    }
                }
                if(num_impiegati[2] <= 3) {
                    query = "SELECT Ref_Impiegato FROM turni_attivi WHERE Servizio = 'C'";
                    try (PreparedStatement stmt2 = conn.prepareStatement(query)) {
                        r = stmt2.executeQuery();
                    }
                    while(r.next()) {
                        query = "UPDATE turni\n" +
                                "SET Servizio='B'\n" +
                                "WHERE  DATE(inizio_turno)=CURDATE() AND Ref_impiegato=? AND Ref_impiegato " +
                                "IN (SELECT DISTINCT(t.Ref_impiegato)\n" +
                                "FROM turni t, presenze p\n" +
                                "WHERE t.Ref_impiegato=p.Ref_impiegato AND p.Ora_fine IS NULL AND DATE(t.inizio_turno)=CURDATE() AND DATE(p.ora_inizio)=CURDATE() AND t.inizio_turno=p.Ora_inizio)";
                        try(PreparedStatement stmt3 = conn.prepareStatement(query)) {
                            stmt3.setInt(1,r.getInt("Ref_impiegato"));
                            stmt3.executeUpdate();
                            num_impiegati[2]--;
                        }
                        queryInserisciNotifica(r.getInt("Ref_impiegato"),"Sei stato spostato al servizio B.","Chiusura servizio");

                    }
                }
            }
            if(num_impiegati[1] <= 4){
                int i=0;
                String[] s = {"D","C"};
                while (num_impiegati[3-i]>num_impiegati[1] && i<2){
                    query = "SELECT Ref_Impiegato FROM turni_attivi WHERE Servizio=?";
                    try(PreparedStatement stmt2 = conn.prepareStatement(query)) {
                        stmt2.setString(1,s[i]);
                        r = stmt2.executeQuery();
                    }
                    while(r.next()) {
                        query = "UPDATE turni\n" +
                                "SET Servizio='B'\n" +
                                "WHERE  DATE(inizio_turno)=CURDATE() AND Ref_impiegato=? AND Ref_impiegato " +
                                "IN (SELECT DISTINCT(t.Ref_impiegato)\n" +
                                "FROM turni t, presenze p\n" +
                                "WHERE t.Ref_impiegato=p.Ref_impiegato AND p.Ora_fine IS NULL AND DATE(t.inizio_turno)=CURDATE() AND DATE(p.ora_inizio)=CURDATE() AND t.inizio_turno=p.Ora_inizio)";
                        try(PreparedStatement stmt3 = conn.prepareStatement(query)) {
                            stmt3.setInt(1,r.getInt("Ref_impiegato"));
                            stmt3.executeUpdate();
                            num_impiegati[3-i]--;
                        }
                    }
                    queryInserisciNotifica(r.getInt("Ref_impiegato"),"Sei stato spostato al servizio B.","Chiusura servizio");
                    if(!(num_impiegati[3-i]>num_impiegati[1]))
                        i++;
                }
                if(num_impiegati[1] <= 4){
                    query = "SELECT Ref_Impiegato FROM turni_attivi WHERE Servizio = 'B'";
                    try (PreparedStatement stmt2 = conn.prepareStatement(query)) {
                        r = stmt2.executeQuery();
                    }
                    while(r.next()) {
                        query = "UPDATE turni\n" +
                                "SET Servizio='A'\n" +
                                "WHERE  DATE(inizio_turno)=CURDATE() AND Ref_impiegato=? AND Ref_impiegato " +
                                "IN (SELECT DISTINCT(t.Ref_impiegato)\n" +
                                "FROM turni t, presenze p\n" +
                                "WHERE t.Ref_impiegato=p.Ref_impiegato AND p.Ora_fine IS NULL AND DATE(t.inizio_turno)=CURDATE() AND DATE(p.ora_inizio)=CURDATE() AND t.inizio_turno=p.Ora_inizio)";
                        try(PreparedStatement stmt3 = conn.prepareStatement(query)) {
                            stmt3.setInt(1,r.getInt("Ref_impiegato"));
                            stmt3.executeUpdate();
                            num_impiegati[1]--;
                        }
                        queryInserisciNotifica(r.getInt("Ref_impiegato"),"Sei stato spostato al servizio A.","Chiusura servizio");
                    }
                }
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

    public static boolean queryInserisciNotifica(int matricola,String messaggio,String titolo){
        startConnection();
        String query = "INSERT INTO notifiche (Ref_impiegato,Messaggio,Titolo) VALUES (?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, matricola);
            stmt.setString(2, messaggio);
            stmt.setString(3,titolo);
            var r = stmt.executeUpdate();
            return r==1;
        }catch (SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return false;
    }

    public static boolean queryInserisciNotifiche(List<Integer> matricole,List<String> messaggi,List<String> titoli){
        startConnection();
        for(int i=0;i<matricole.size();++i) {
            String query = "INSERT INTO notifiche (Ref_impiegato,Messaggio,Titolo) VALUES (?,?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, matricole.get(i));
                stmt.setString(2, messaggi.get(i));
                stmt.setString(3, titoli.get(i));
                var r = stmt.executeUpdate();
                return r == 1;
            } catch (SQLException e) {
                erroreComunicazioneDBMS(e);
            }
        }
        return false;
    }

    public static boolean queryRimuoviFerie(int matricola, String datapicker_from, String datapicker_to){
        String query = "DELETE"+
        "FROM permessi p"+
        "WHERE p.Ref_impiegato=? AND p.Tipo_permesso='Ferie' AND (? BETWEEN p.Data_inizio_permesso AND p.Data_fine_permesso) OR(? BETWEEN p.Data_inizio_permesso AND p.Data_fine_permesso)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, matricola);
            stmt.setString(2, datapicker_from);
            stmt.setString(3, datapicker_to);
            var r = stmt.executeUpdate();
            return r==1;
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
    return false;
    }

    /*
    public static Map<Impiegato,List<Integer>> queryGetPermessi(){
        String query ="SELECT DISTINCT(p.Ref_impiegato), SUM(CEILING(TIMESTAMPDIFF(SECOND, p.Data_inizio_permesso, p.Data_fine_permesso)/3600)) AS hours, p.Tipo_permesso\n" +
                " FROM permessi p\n" +
                " WHERE p.Ref_impiegato IS NOT NULL AND p.Tipo_permesso<>'Malattia'\n" +
                " GROUP BY\n" +
                " p.Ref_impiegato,p.Tipo_permesso\n" +
                " ORDER BY hours DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r= stmt.executeQuery();
            Map<Integer,List<Integer>> lista_permessi;
            if(r.next()){
               lista_permessi.put(r.getInt("Ref_impiegato"),
            }
            while (r.next()){

            }
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return null;

    }*/
    public static Map<Integer,String> queryListaFerie(){
       String query=" SELECT DISTINCT(p.Ref_impiegato), SUM(CEILING(TIMESTAMPDIFF(SECOND, p.Data_inizio_permesso, p.Data_fine_permesso)/3600)) AS hours, p.Tipo_permesso\n"+
        "FROM permessi p, impiegati i\n"+
        "WHERE p.Tipo_permesso<>'Malattia' AND i.Matricola=p.Ref_impiegato AND i.Mansione='Employee'\n"+
        "GROUP BY"+
        "p.Ref_impiegato,p.Tipo_permesso"+
        "ORDER BY hours DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            Map<Integer,String> lista_ferie = null;
            if(r.next()){
                lista_ferie = new HashMap<>();
                lista_ferie.put(r.getInt("Ref_impiegato"),r.getString("Tipo_permesso"));
            }
            while(r.next()){
                lista_ferie.put(r.getInt("Ref_impiegato"),r.getString("Tipo_permesso"));
            }
            return lista_ferie;
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return null;
    }



    public static Map<Impiegato,Turno> queryRichiesteMalattiaImpiegati(){
        startConnection();
        Map<Impiegato,Turno>  malattie = new HashMap<>();
        Random p = new Random();
        String query = "SELECT p.Ref_impiegato,p.Data_inizio_permesso, p.Data_fine_permesso,i.livello\n" +
                "FROM permessi p, impiegati i\n" +
                "WHERE p.Tipo_permesso='Malattia' AND DATEDIFF(p.Data_fine_permesso,p.Data_inizio_permesso)>1 AND DATEDIFF(DATE(p.Data_fine_permesso),CURRENT_DATE)>1 AND (CURRENT_DATE BETWEEN DATE(p.Data_inizio_permesso) AND DATE(p.Data_fine_permesso)) AND i.Matricola=p.Ref_impiegato";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            while(r.next()){
                Impiegato imp = new Impiegato();
                imp.setMatricola(r.getInt("p.Ref_impiegato"));
                Turno turn = new Turno();
                turn.setServizio(r.getString("i.livello"));
                turn.setGiorno(LocalDate.now());
                int k = p.nextInt(3);
                if(k==0) {
                    turn.setOrario_inizio(LocalTime.parse("00:01:00"));
                    turn.setOrario_fine(LocalTime.parse("00:08:00"));
                } else if(k==1){
                    turn.setOrario_inizio(LocalTime.parse("00:08:00"));
                    turn.setOrario_fine(LocalTime.parse("00:15:59"));
                } else{
                    turn.setOrario_inizio(LocalTime.parse("00:16:00"));
                    turn.setOrario_fine(LocalTime.parse("00:23:59"));
                }
                malattie.put(imp,turn);
            }
        } catch (SQLException e) {
            erroreComunicazioneDBMS(e);
        }
        return malattie;
    }




    public static List<Impiegato> queryImpiegatiDisponibili(){
        String query = "SELECT DISTINCT(p.Ref_impiegato), SUM(CEILING(TIMESTAMPDIFF(SECOND, p.Data_inizio_permesso, p.Data_fine_permesso)/3600)) AS hours, p.Tipo_permesso FROM permessi p, impiegati i WHERE p.Tipo_permesso<> 'Malattia' AND i.Matricola=p.Ref_impiegato AND i.Mansione='Employee' GROUP BY p.Ref_impiegato,p.Tipo_permesso ORDER BY hours DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            var r = stmt.executeQuery();
            List<Impiegato> lista_impiegati_disponibili= new ArrayList<>();
            while(r.next()){
                lista_impiegati_disponibili.add(DBMSManager.getImpiegatoByMatricola(r.getInt("p.Ref_impiegato")));
            }
            return  lista_impiegati_disponibili;
        }catch(SQLException e){
            erroreComunicazioneDBMS(e);
        }
        return null;
    }
}
