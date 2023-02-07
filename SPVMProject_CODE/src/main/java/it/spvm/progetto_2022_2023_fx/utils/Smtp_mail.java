package it.spvm.progetto_2022_2023_fx.utils;


import it.spvm.progetto_2022_2023_fx.entity.Impiegato;
import it.spvm.progetto_2022_2023_fx.entity.Turno;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Smtp_mail {

	Session session;
	String from;
	String host;

	//Costruttore che inizializza la connessione SMTP con i parametri di Aruba mail
	public Smtp_mail() {
	        String from = "";
	        String host = "";

	        final String username = "";
	        final String password = "";
	 
	        System.out.println("TLSEmail Start");

	        Properties properties = System.getProperties();
	         
	        properties.setProperty("mail.smtp.starttls.enable", "true");
	        properties.setProperty("mail.smtp.auth", "true");
	        properties.setProperty("mail.debug.auth", "true");
	        properties.setProperty("mail.smtp.from", from);
	        properties.setProperty("mail.smtp.user", username);
	        properties.setProperty("mail.smtp.password","");
	        properties.setProperty("mail.smtp.host", host);
	        properties.setProperty("mail.smtp.port", "465");
	         
	        // SSL Factory
	        properties.put("mail.smtp.socketFactory.class",
	                "javax.net.ssl.SSLSocketFactory"); 

	        Session session = Session.getDefaultInstance(properties,
	            new javax.mail.Authenticator() {
	                 
	                // override the getPasswordAuthentication
	                // method
	                protected PasswordAuthentication
	                        getPasswordAuthentication() {
	                    return new PasswordAuthentication("",
	                                                    "");
	                }
	            });
	        this.session = session;
	        this.from = from;
	}

	//METODO PER INVIARE MAIL SUL RECUPERO PASSWORD
	public void sendMessage(String to, int code) throws MessagingException {
	 
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	    Date date = new Date();  
	 //compose the message

	    // javax.mail.internet.MimeMessage class is mostly
	    // used for abstraction.
	    MimeMessage message = new MimeMessage(session);
	     
	    // header field of the header.
	    message.setFrom(new InternetAddress(from));
	     
	    message.addRecipient(Message.RecipientType.TO,
	                          new InternetAddress(to));
	    message.setSubject("Comunicazione azienda");
	    String html_message="<br><br><br>"
	    		+ "<center><h2>Il tuo codice di recupero</h2>"
	    		+ "<div style='background-color: green;'>"
	    		+ "<h3 style='color: white;'>"+code+"</h3>" //GENERARE IL CODICE
	    		+ "</div>"
	    		+ "<br><p>*Si consiglia di non condividere questo codice con nessuno.</p>"
	    		+ "<p>"+formatter.format(date)+"</p></center>";
	    message.setContent(html_message, "text/html; charset=utf-8");
	    // Send message
	    Transport.send(message);
	    System.out.println("Email inviata con successo!");
	    session.getTransport().close();
	}

	public void sendEmail(int matricola, String nome, String cognome, Turno t){
		Impiegato i = DBMSManager.getImpiegatoByMatricola(matricola);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		Date date = new Date();
		//compose the message

		// javax.mail.internet.MimeMessage class is mostly
		// used for abstraction.
		MimeMessage message = new MimeMessage(session);

		try {
			// header field of the header.
			message.setFrom(new InternetAddress(from));

			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(i.getEmail()));
			message.setSubject("Comunicazione azienda");
			String html_message = "<br><br><br>"
					+ "<center><h2>Sostituzione</h2>"
					+ "<div style='background-color: green;'>"
					+ "<h3 style='color: white;'> Turno da sostituire</h3>"
					+ "<h3 style='color: white;'> Giorno" + t.getGiorno() + "</h3>"
					+ "<h3 style='color: white;'> Dalle" + t.getOrario_Inizio() + "</h3>"
					+ "<h3 style='color: white;'> Alle" + t.getOrario_Fine() + "</h3>"
					+ "<h3 style='color: white;'> Servizio" + t.getServizio() + "</h3>"
					+ "</div>"
					+ "<br>"
					+ "<p>" + formatter.format(date) + "</p></center>";
			message.setContent(html_message, "text/html; charset=utf-8");
			// Send message
			Transport.send(message);
			System.out.println("Email inviata con successo!");
			session.getTransport().close();
		}catch (MessagingException e){
			e.printStackTrace();
		}
	}

	//METODO PER INVIARE MAIL ALL'IMPIEGATO ASSUNTO (comprende la matricola da stampare nella mail)
	public void sendMessageAssumiImpiegato(String to, int matricola,String password) throws MessagingException {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		//compose the message

		// javax.mail.internet.MimeMessage class is mostly
		// used for abstraction.
		MimeMessage message = new MimeMessage(session);

		// header field of the header.
		message.setFrom(new InternetAddress(from));

		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(to));
		message.setSubject("Comunicazione azienda");
		String html_message="<br><br><br>"
				+ "<center><h2>Assunzione</h2>"
				+ "<div style='background-color: green;'>"
				+ "<h3 style='color: white;'>La tua matricola: " + matricola +"</h3>"
				+ "<h3 style='color: white;'>La tua password: " + password +"</h3>"
				+ "</div>"
				+ "<br><p>*Si consiglia di non condividere questo codice con nessuno.</p>"
				+ "<p>"+formatter.format(date)+"</p></center>";
		message.setContent(html_message, "text/html; charset=utf-8");
		// Send message
		Transport.send(message);
		System.out.println("Email inviata con successo!");
		session.getTransport().close();
	}

	//METODO PER INVIARE MAIL ALL'IMPIEGATO CON MANCATA FIRMA (comprende le informazioni dell'impiegato + il turno che non ha firmato)
	public void sendMessageMancataFirma(String to, int matricola,String nome, String cognome, String giorno,String turno) throws MessagingException {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		//compose the message

		// javax.mail.internet.MimeMessage class is mostly
		// used for abstraction.
		MimeMessage message = new MimeMessage(session);

		// header field of the header.
		message.setFrom(new InternetAddress(from));

		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(to));
		message.setSubject("Comunicazione azienda");
		String html_message="<br><br><br>"
				+ "<center><h2>Assenza sul posto di lavoro</h2>"
				+ "<div style='background-color: orange;'> <br>"
				+ "<h3 style='color: white;'>Matricola: " + matricola +"</h3>"
				+ "<h3 style='color: white;'>Cognome: " + cognome +"</h3>"
				+ "<h3 style='color: white;'>Nome: " + nome +"</h3>"
				+ "<h3 style='color: white;'>Giorno: " + giorno +"</h3>"
				+ "<h3 style='color: white;'>Turno: " + turno +"</h3>"
				+ "<br></div>"
				+ "<p>"+formatter.format(date)+"</p></center>";
		message.setContent(html_message, "text/html; charset=utf-8");
		// Send message
		Transport.send(message);
		System.out.println("Email inviata con successo!");
		session.getTransport().close();
	}

	
}
